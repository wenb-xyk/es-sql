package com.staryea.rest.junit;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.staryea.rest.ObjectEsSqlRest;

public class Test {
public static void main(String[] args) throws Exception {
	RestHighLevelClient client = new RestHighLevelClient(
	        RestClient.builder(
	                new HttpHost("127.0.0.1", 9200, "http") 
	                 ));
	
	ObjectEsSqlRest  rest  = new ObjectEsSqlRest();
	rest.setClient(client);
	System.out.println(rest.queryForCount("SELECT ip,count(ip) FROM flux-20210323 where time_str > '2021-03-23 13:50:00' group by ip.keyword"));
	return ;
	
}
}
