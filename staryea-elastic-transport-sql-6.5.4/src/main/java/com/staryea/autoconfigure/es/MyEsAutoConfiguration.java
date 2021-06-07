/**
 * Copyright 2015-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.staryea.autoconfigure.es;

import java.net.InetAddress;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.staryea.essql.ObjectEsSql;

@Configuration
@ConditionalOnClass({Client.class, ObjectEsSql.class})
@EnableConfigurationProperties(MyEsProperties.class)
public class MyEsAutoConfiguration {
    private final MyEsProperties properties;

    public MyEsAutoConfiguration(MyEsProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public TransportClient elasticsearchClient() throws Exception {
        TransportClient client = new PreBuiltTransportClient(settings());
        if (properties == null || !StringUtils.hasText(properties.getClusterNodes())) {
            client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9200));
        } else {
            String[] nodeArr = properties.getClusterNodes().split(",");
            for (String node : nodeArr) {
                String[] split = node.split(":");
                client.addTransportAddress(new TransportAddress(InetAddress.getByName(split[0]), Integer.parseInt(split[1])));
            }
        }
        return client;
    }

    private Settings settings() {
        if (properties == null
                || properties.getProperties() == null
                || properties.getProperties().isEmpty()) {
            return Settings.builder()
                    .put("cluster.name", this.properties.getClusterName())
                    .put("client.transport.sniff", true)
                    .put("client.transport.ignore_cluster_name", false)
                    .put("client.transport.ping_timeout", "5s")
                    .put("client.transport.nodes_sampler_interval", "5s")
                    .build();
        }
        Settings.Builder builder = Settings.builder()
                .put("cluster.name", this.properties.getClusterName());
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }


    @Bean
    @ConditionalOnMissingBean
    public ObjectEsSql esSql(TransportClient transportClient) {
        ObjectEsSql support = new ObjectEsSql();
        support.setClient(transportClient);
        return support;
    }

}
