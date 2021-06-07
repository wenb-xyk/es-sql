package com.staryea.essql.junit;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.staryea.essql.ObjectEsSql;
/**
 * 
 * @author wenb
 * 
 */
public class SelectTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Settings settings = Settings.builder().put("cluster.name", "cnoss").put("client.transport.sniff", false)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

		
		ObjectEsSql support = new ObjectEsSql ();
		 support.setClient(client);
		
//		List<Map<String, Object>> ll1 = support.queryForList("select * from flux*", 0, 50);
//		System.out.println(ll1);
		
		System.out.println(support.queryForListGroup("SELECT ip,count(ip) FROM flux-*  group by ip"));
		

	}
}
