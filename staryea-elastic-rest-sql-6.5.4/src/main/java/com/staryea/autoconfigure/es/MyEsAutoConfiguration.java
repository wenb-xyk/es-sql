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

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.staryea.rest.ObjectEsSqlRest;

@Configuration
@ConditionalOnClass({ Client.class, ObjectEsSqlRest.class })
@EnableConfigurationProperties(MyEsProperties.class)
public class MyEsAutoConfiguration {
	private final MyEsProperties properties;

	public MyEsAutoConfiguration(MyEsProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public RestHighLevelClient elasticsearchClient() throws Exception {
		String[] hostNamesPort = properties.getRestHosts().split(",");

		String host;
		int port;
		String[] temp;

		RestClientBuilder restClientBuilder = null;

		/* restClient 初始化 */
		if (0 != hostNamesPort.length) {
			for (String hostPort : hostNamesPort) {
				temp = hostPort.split(":");
				host = temp[0].trim();
				port = Integer.parseInt(temp[1].trim());
				restClientBuilder = RestClient.builder(new HttpHost(host, port, "http"));
			}
		}
		if(!StringUtils.isEmpty( properties.getUsername()) &&  !StringUtils.isEmpty( properties.getPassword()) ) {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials( properties.getUsername(), properties.getPassword()));
			restClientBuilder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
				@Override
				public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder f) {
					return f.setDefaultCredentialsProvider(credentialsProvider);
				}
			});
		}
		
		return new RestHighLevelClient(restClientBuilder);

	}

	@Bean
	@ConditionalOnMissingBean
	public ObjectEsSqlRest esSql(RestHighLevelClient client) {
		ObjectEsSqlRest support = new ObjectEsSqlRest();
		support.setClient(client);
		return support;
	}

}
