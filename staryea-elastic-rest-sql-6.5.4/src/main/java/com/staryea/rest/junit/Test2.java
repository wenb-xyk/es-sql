package com.staryea.rest.junit;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.staryea.rest.ObjectEsSqlRest;

public class Test2 {
public static void main(String[] args) throws Exception {
	RestHighLevelClient client = new RestHighLevelClient(
	        RestClient.builder(
	                new HttpHost("127.0.0.1", 9200, "http") 
	                 ));
	
	ObjectEsSqlRest  rest  = new ObjectEsSqlRest();
	rest.setClient(client);
//	List<Map<String, Object>> ll1 = rest.queryForList("select * from flux-*", 0, 50);
//	System.out.println(ll1);

	System.out.println(rest.queryForListGroup("SELECT ip,count(ip) FROM flux-*  group by ip"));
 
}
}
