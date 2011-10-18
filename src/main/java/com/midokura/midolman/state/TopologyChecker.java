/**
 * ZkDumper.java - simple command line utility to dump data from ZooKeeper.
 *
 * Run with:
 *       mvn exec:java -Dexec.mainClass=com.midokura.midolman.state.ZkDumper
 *                     -Dexec.args=<arguments>
 * TODO: Package this into a standalone .jar
 *
 * Copyright 2011 Midokura Inc.
 */

package com.midokura.midolman.state;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.codehaus.jackson.JsonParseException;

import com.midokura.midolman.layer3.Route;
import com.midokura.midolman.packets.IntIPv4;
import com.midokura.midolman.rules.Rule;
import com.midokura.midolman.state.ChainZkManager.ChainConfig;
import com.midokura.midolman.state.PortDirectory.LogicalRouterPortConfig;
import com.midokura.midolman.util.JSONSerializer;

public class TopologyChecker {

    static ZooKeeper zk;
    static final Semaphore available = new Semaphore(0);

    public static void main(String args[]) throws StateAccessException,
            ZkStateSerializationException, KeeperException,
            InterruptedException, JsonParseException, IOException {
        Options options = new Options();
        options.addOption("h", "host", true, "ZooKeeper server hostname");
        options.addOption("p", "port", true, "ZooKeeper server port");
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new GnuParser();
        CommandLine cl;
        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Bad command arguments: " + e);
            formatter.printHelp("TopologyChecker routerUUID", options);
            System.exit(-1);
            cl = null; // javac thinks cl used uninitialized because it
                       // doesn't know .exit() is no-return.
        }

        int zkPort = Integer.parseInt(cl.getOptionValue('p', "2181"));
        String zkHost = cl.getOptionValue('h', "localhost");

        List<String> argList = cl.getArgList();
        if (argList.size() != 1) {
            System.err.println("Requires one router UUID argument.");
            System.exit(-1);
        }
        UUID routerId = UUID.fromString(argList.get(0));

        try {
            setupZKConnection(zkHost, zkPort);
        } catch (Exception e) {
            System.err
                    .println("Failed to establish ZooKeeper connection: " + e);
            System.exit(-1);
        }
        ZkDirectory zkDir = new ZkDirectory(zk, "", Ids.OPEN_ACL_UNSAFE, null);

        // TODO(pino): get the base path from a config file.
        String basePath = "/midonet/v1/midolman";
        checkRouter(routerId, basePath, zkDir);

        try {
            zk.close();
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e);
            System.exit(-1);
        }
    }

    public static void checkRouter(UUID routerId, String basePath,
            Directory zkDir) throws StateAccessException,
            ZkStateSerializationException, KeeperException,
            InterruptedException, JsonParseException, IOException {

        ZkPathManager pathMgr = new ZkPathManager(basePath);

        // First print out all the rule chains.
        System.out.println("Router's chains:");
        ChainZkManager chainMgr = new ChainZkManager(zkDir, basePath);
        RuleZkManager ruleMgr = new RuleZkManager(zkDir, basePath);
        List<ZkNodeEntry<UUID, ChainConfig>> chains = chainMgr.list(routerId);
        for (ZkNodeEntry<UUID, ChainConfig> chain : chains) {
            System.out.println("  " + chain.key + " has name "
                    + chain.value.name + " and rules:");
            List<ZkNodeEntry<UUID, Rule>> rules = ruleMgr.list(chain.key);
            for (ZkNodeEntry<UUID, Rule> rule : rules) {
                System.out.println("    " + rule.key + " " + rule.value);
            }
        }
        System.out.println();
        System.out.println("Router's routes:");
        RouteZkManager routeMgr = new RouteZkManager(zkDir, basePath);
        List<ZkNodeEntry<UUID, Route>> routes = routeMgr.listRouterRoutes(
                routerId, null);
        for (ZkNodeEntry<UUID, Route> route : routes) {
            System.out.println("  " + route.key + " " + route.value);
        }
        System.out.println();
        System.out.println("Router's ports (and their routes):");
        Directory portLocationDirectory = zkDir.getSubDirectory(pathMgr
                .getVRNPortLocationsPath());
        PortToIntNwAddrMap portLocationMap = new PortToIntNwAddrMap(
                portLocationDirectory);
        portLocationMap.start();

        PortZkManager portMgr = new PortZkManager(zkDir, basePath);
        // Keep track of ports that are in the PortLocationMap.
        Set<UUID> portsInMap = new HashSet<UUID>();
        List<ZkNodeEntry<UUID, PortConfig>> ports = portMgr
                .listRouterPorts(routerId);
        for (ZkNodeEntry<UUID, PortConfig> port : ports) {
            System.out.println("  Port " + port.key + " " + port.value);
            IntIPv4 loc = portLocationMap.get(port.key);
            if (null == loc) {
                if (!(port.value instanceof LogicalRouterPortConfig))
                    System.out.println("    Warn: not in the PortLocationMap");
            } else {
                System.out.println("    Located at host " + loc.toString()
                        + " on the physical network.");
                portsInMap.add(port.key);
            }
            System.out.println("    Routes:");
            routes = routeMgr.listPortRoutes(port.key);
            for (ZkNodeEntry<UUID, Route> route : routes) {
                System.out.println("    " + route.key + " " + route.value);
            }
        }
        System.out.println();
        System.out.println("Router's routing table:");
        RouterZkManager routerMgr = new RouterZkManager(zkDir, basePath);
        Directory rtableDir = routerMgr.getRoutingTableDirectory(routerId);
        Set<String> rtStrings = rtableDir.getChildren("", null);
        JSONSerializer<Route> serializer = new JSONSerializer<Route>();
        // Keep track of ports that are the target of routes.
        Set<UUID> portsInTable = new HashSet<UUID>();
        for (String str : rtStrings) {
            Route rt = serializer.bytesToObj(str.getBytes(), Route.class);
            System.out.println("  " + rt.toString());
            if (rt.nextHop == Route.NextHop.PORT)
                portsInTable.add(rt.nextHopPort);
        }
        // Now warn about any ports that are in the location map but aren't
        // the target of any ports.
        for (UUID port : portsInMap) {
            if (!portsInTable.contains(port))
                System.out.println("  WARN: port is in location map but "
                        + "isn't the next hop for any routes: "
                        + port.toString());
        }
    }

    private static void setupZKConnection(final String host, final int port)
            throws Exception {
        int magic = 3000; // FIXME
        System.out.println("Connecting to ZooKeeper at " + host + ":" + port);
        zk = new ZooKeeper(host + ":" + port, magic, new Watcher() {
            @Override
            public synchronized void process(WatchedEvent event) {
                if (event.getState() == KeeperState.Disconnected) {
                    System.err.println("Disconnected from ZooKeeper");
                    System.exit(-1);
                } else if (event.getState() == KeeperState.SyncConnected) {
                    System.out.println("Connected to ZooKeeper at " + host
                            + ":" + port);
                    available.release();
                } else if (event.getState() == KeeperState.Expired) {
                    System.err.println("Session expired");
                    System.exit(-1);
                }
            }
        });
        System.out.println("In progress to ZooKeeper at " + host + ":" + port);

        available.acquire();
    }

}
