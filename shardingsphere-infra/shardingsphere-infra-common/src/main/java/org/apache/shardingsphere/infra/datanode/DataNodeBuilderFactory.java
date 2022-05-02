/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.datanode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.spi.type.ordered.OrderedSPIRegistry;

import java.util.Collection;
import java.util.Map;

/**
 * Data node builder factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataNodeBuilderFactory {
    
    static {
        ShardingSphereServiceLoader.register(DataNodeBuilder.class);
    }
    
    /**
     * Create new instance of data node builder.
     * 
     * @param rules rules
     * @return new instance of data node builder
     */
    @SuppressWarnings("rawtypes")
    public static Map<ShardingSphereRule, DataNodeBuilder> newInstance(final Collection<ShardingSphereRule> rules) {
        return OrderedSPIRegistry.getRegisteredServices(DataNodeBuilder.class, rules);
    }
}