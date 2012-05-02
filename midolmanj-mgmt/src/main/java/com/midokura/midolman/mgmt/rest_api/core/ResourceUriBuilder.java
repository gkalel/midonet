/*
 * @(#)ResourceUriBuilder.java        1.6 11/11/15
 *
 * Copyright 2011 Midokura KK
 */
package com.midokura.midolman.mgmt.rest_api.core;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import com.midokura.midolman.packets.IntIPv4;

public class ResourceUriBuilder {

    public static final String ROOT = "/";
    public static final String TENANTS = "/tenants";
    public static final String ROUTERS = "/routers";
    public static final String BRIDGES = "/bridges";
    public static final String FILTER_DB = "/filteringDB";
    public static final String DHCP = "/dhcp";
    public static final String DHCP_HOSTS = "/hosts";
    public static final String PORTS = "/ports";
    public static final String CHAINS = "/chains";
    public static final String RULES = "/rules";
    public static final String ROUTES = "/routes";
    public static final String VIFS = "/vifs";
    public static final String BGP = "/bgps";
    public static final String AD_ROUTES = "/ad_routes";
    public static final String VPN = "/vpns";
    public static final String HOSTS = "/hosts";
    public static final String INTERFACES = "/interfaces";
    public static final String COMMANDS = "/commands";

    private ResourceUriBuilder() {
    }

    public static URI getRoot(URI baseUri) {
        return UriBuilder.fromUri(baseUri).path(ROOT).build();
    }

    public static URI getTenants(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(TENANTS).build();
    }

    public static URI getTenant(URI baseUri, String tenantId) {
        return UriBuilder.fromUri(getTenants(baseUri)).path(tenantId).build();
    }

    public static URI getRouters(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(ROUTERS).build();
    }

    public static URI getRouter(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouters(baseUri))
                .path(routerId.toString()).build();
    }

    public static URI getTenantRouters(URI baseUri, String tenantId) {
        return UriBuilder.fromUri(getTenant(baseUri, tenantId)).path(ROUTERS)
                .build();
    }

    public static URI getBridges(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(BRIDGES).build();
    }

    public static URI getBridge(URI baseUri, UUID bridgeId) {
        return UriBuilder.fromUri(getBridges(baseUri))
                .path(bridgeId.toString()).build();
    }

    public static URI getBridgeRouters(URI baseUri, UUID bridgeId) {
        return UriBuilder.fromUri(getBridge(baseUri, bridgeId)).path(ROUTERS)
                .build();
    }

    public static URI getBridgeRouter(
            URI baseUri, UUID bridgeId, UUID routerId) {
        return UriBuilder.fromUri(getBridgeRouters(baseUri, bridgeId))
                .path(routerId.toString()).build();
    }

    public static URI getTenantBridges(URI baseUri, String tenantId) {
        return UriBuilder.fromUri(getTenant(baseUri, tenantId)).path(BRIDGES)
                .build();
    }

    public static URI getPorts(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(PORTS).build();
    }

    public static URI getPort(URI baseUri, UUID portId) {
        return UriBuilder.fromUri(getPorts(baseUri)).path(portId.toString())
                .build();
    }

    public static URI getBridgePorts(URI baseUri, UUID bridgeId) {
        return UriBuilder.fromUri(getBridge(baseUri, bridgeId)).path(PORTS)
                .build();
    }

    public static URI getFilteringDb(URI baseUri, UUID bridgeId) {
        return UriBuilder.fromUri(getBridge(baseUri, bridgeId)).path(FILTER_DB)
                .build();
    }

    public static URI getBridgeDhcps(URI baseUri, UUID bridgeId) {
        return UriBuilder.fromUri(getBridge(baseUri, bridgeId)).path(DHCP)
                .build();
    }

    public static URI getBridgeDhcp(URI bridgeDhcpsUri, IntIPv4 subnetAddr) {
        return UriBuilder.fromUri(bridgeDhcpsUri)
                .path(subnetAddr.toString()).build();
    }

    public static URI getBridgeDhcp(URI baseUri, UUID bridgeId,
                                    IntIPv4 subnetAddr) {
        URI dhcpsUri = getBridgeDhcps(baseUri, bridgeId);
        return getBridgeDhcp(dhcpsUri, subnetAddr);
    }

    public static URI getDhcpHosts(URI bridgeDhcpUri) {
        return UriBuilder.fromUri(bridgeDhcpUri).path(DHCP_HOSTS).build();
    }

    public static String macToUri(String mac) {
        return mac.replace(':', '-');
    }

    public static String macFromUri(String mac) {
        return mac.replace('-', ':');
    }

    public static URI getDhcpHost(URI bridgeDhcpUri, String macAddr) {
        return UriBuilder.fromUri(getDhcpHosts(bridgeDhcpUri))
                .path(macToUri(macAddr)).build();
    }

    public static URI getRouterPorts(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(PORTS)
                .build();
    }

    public static URI getChains(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(CHAINS).build();
    }

    public static URI getChain(URI baseUri, UUID chainId) {
        return UriBuilder.fromUri(getChains(baseUri)).path(chainId.toString())
                .build();
    }

    public static URI getRouterChains(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(CHAINS)
                .build();
    }

    public static URI getRules(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(RULES).build();
    }

    public static URI getRule(URI baseUri, UUID ruleId) {
        return UriBuilder.fromUri(getRules(baseUri)).path(ruleId.toString())
                .build();
    }

    public static URI getChainRules(URI baseUri, UUID chainId) {
        return UriBuilder.fromUri(getChain(baseUri, chainId)).path(RULES)
                .build();
    }

    public static URI getRouterRouters(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(ROUTERS)
                .build();
    }

    public static URI getBgps(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(BGP).build();
    }

    public static URI getBgp(URI baseUri, UUID bgpId) {
        return UriBuilder.fromUri(getBgps(baseUri)).path(bgpId.toString())
                .build();
    }

    public static URI getPortBgps(URI baseUri, UUID portId) {
        return UriBuilder.fromUri(getPort(baseUri, portId)).path(BGP).build();
    }

    public static URI getAdRoutes(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(AD_ROUTES).build();
    }

    public static URI getAdRoute(URI baseUri, UUID adRouteId) {
        return UriBuilder.fromUri(getAdRoutes(baseUri))
                .path(adRouteId.toString()).build();
    }

    public static URI getBgpAdRoutes(URI baseUri, UUID bgpId) {
        return UriBuilder.fromUri(getBgp(baseUri, bgpId)).path(AD_ROUTES)
                .build();
    }

    public static URI getVpns(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(VPN).build();
    }

    public static URI getVpn(URI baseUri, UUID vpnId) {
        return UriBuilder.fromUri(getVpns(baseUri)).path(vpnId.toString())
                .build();
    }

    public static URI getPortVpns(URI baseUri, UUID portId) {
        return UriBuilder.fromUri(getPort(baseUri, portId)).path(VPN).build();
    }

    public static URI getVifs(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(VIFS).build();
    }

    public static URI getVif(URI baseUri, UUID vifId) {
        return UriBuilder.fromUri(getVifs(baseUri)).path(vifId.toString())
                .build();
    }

    public static URI getRouterLinks(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(ROUTERS)
                .build();
    }

    public static URI getRouterLink(URI baseUri, UUID routerId,
                                    UUID peerRouterId) {
        return UriBuilder.fromUri(getRouterLinks(baseUri, routerId))
                .path(peerRouterId.toString()).build();
    }

    public static URI getRouterBridges(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(BRIDGES)
                .build();
    }

    public static URI getRouterBridge(URI baseUri, UUID routerId,
                                    UUID bridgeId) {
        return UriBuilder.fromUri(getRouterBridges(baseUri, routerId))
                .path(bridgeId.toString()).build();
    }

    public static URI getRoutes(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(ROUTES).build();
    }

    public static URI getRoute(URI baseUri, UUID routeId) {
        return UriBuilder.fromUri(getRoutes(baseUri)).path(routeId.toString())
                .build();
    }

    public static URI getRouterRoutes(URI baseUri, UUID routerId) {
        return UriBuilder.fromUri(getRouter(baseUri, routerId)).path(ROUTES)
                .build();
    }

    public static URI getHosts(URI baseUri) {
        return UriBuilder.fromUri(getRoot(baseUri)).path(HOSTS).build();
    }

    public static URI getHost(URI baseUri, UUID hostId) {
        return UriBuilder.fromUri(getHosts(baseUri)).path(hostId.toString())
                 .build();
    }

    public static URI getHostInterfaces(URI baseUri, UUID hostId) {
        return UriBuilder.fromUri(getHost(baseUri, hostId)).path(INTERFACES).build();
    }

    public static URI getHostInterface(URI baseUri, UUID hostId, UUID interfaceId) {
        return UriBuilder.fromUri(getHostInterfaces(baseUri, hostId))
                         .path(interfaceId.toString()).build();
    }

    public static URI getHostCommands(URI baseUri, UUID hostId) {
        return UriBuilder
            .fromUri(getHost(baseUri, hostId)).path(COMMANDS).build();
    }
    public static URI getHostCommand(URI baseUri, UUID hostId, Integer id) {
        return UriBuilder
            .fromUri(getHostCommands(baseUri, hostId))
            .path(id.toString()).build();
    }
}
