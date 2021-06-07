package com.staryea.essql.junit;

import net.sf.json.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

//import org.elasticsearch.common.transport.InetSocketTransportAddress;
public class ElasticSearchBulkIn2 {

	public static void main(String[] args) {

		try {

			Settings settings = Settings.builder().put("cluster.name", "mytest")
					.put("client.transport.sniff", true).build();

			TransportClient client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.2.170"), 9300));
			// ObjectEs543Sql support = new ObjectEs543Sql ();
			// support.setClient(client);

			String line = null;
			long time1 = System.currentTimeMillis();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count = 0;
			JSONObject js = new JSONObject();
			int num = 0;

			// while(true){
			js.put("a", "是");
			js.put("d", "人民广场");
			js.put("b", new Random().nextInt(100) % (100 - 1 + 1) + 1);
			bulkRequest.add(client.prepareIndex("wb", "b").setSource(js));

			js.put("a", "中国人");
			js.put("d", "人民");
			js.put("b", new Random().nextInt(100) % (100 - 1 + 1) + 1);
			bulkRequest.add(client.prepareIndex("wb", "b").setSource(js));

			js.put("a", "人");
			js.put("d", "人");
			js.put("b", new Random().nextInt(100) % (100 - 1 + 1) + 1);
			bulkRequest.add(client.prepareIndex("wb", "b").setSource(js));

			// }
			long time2 = System.currentTimeMillis();
			bulkRequest.execute().actionGet();
			System.out.println(time2 - time1);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
