/*
 * Copyright 2016 Midokura SARL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.midonet.cluster.rest_api.neutron.models;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.midonet.cluster.data.ZoomClass;
import org.midonet.cluster.data.ZoomField;
import org.midonet.cluster.data.ZoomObject;
import org.midonet.cluster.models.Neutron;
import org.midonet.util.collection.ListUtil;

import static org.apache.commons.collections4.ListUtils.hashCodeForList;
import static org.apache.commons.collections4.ListUtils.isEqualList;

@ZoomClass(clazz = Neutron.TapFlow.class)
public class TapFlow extends ZoomObject {

    @ZoomField(name = "id")
    public UUID id;

    @JsonProperty("tenant_id")
    @ZoomField(name = "tenant_id")
    public String tenantId;

    @ZoomField(name = "name")
    public String name;

    @ZoomField(name = "description")
    public String description;

    @JsonProperty("tap_service_id")
    @ZoomField(name = "tap_service_id")
    public UUID tapServiceId;

    @JsonProperty("source_port")
    @ZoomField(name = "source_port")
    public UUID sourcePort;

    @ZoomField(name = "direction")
    public TapFlowDirection direction;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TapFlow that = (TapFlow) o;

        return Objects.equal(id, that.id) &&
               Objects.equal(tenantId, that.tenantId) &&
               Objects.equal(name, that.name) &&
               Objects.equal(description, that.description) &&
               Objects.equal(tapServiceId, that.tapServiceId) &&
               Objects.equal(sourcePort, that.sourcePort) &&
               Objects.equal(direction, that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, tenantId, name, description,
                                tapServiceId, sourcePort, direction);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .omitNullValues()
            .add("id", id)
            .add("tenantId", tenantId)
            .add("name", name)
            .add("description", description)
            .add("tapServiceId", tapServiceId)
            .add("sourcePort", sourcePort)
            .add("direction", direction)
            .toString();
    }
}
