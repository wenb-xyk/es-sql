package com.staryea.essql.junit;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.staryea.essql.ObjectEsSql;

public class CreateMapping {

	public static void main(String[] args) throws IOException {

		try {

			Settings settings = Settings.builder().put("cluster.name", "mytest").put("client.transport.sniff", true)
					.build();

			TransportClient client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.2.170"), 9300));
			ObjectEsSql support = new ObjectEsSql();
			support.setClient(client);
			String typeName = "current";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("type", "keyword");
			map.put("ignore_above",256);
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("keyword", map);
			XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
			mapBuilder.startObject().startObject(typeName).startObject("properties").
			    startObject("ADDITIONALTEXT")
					.field("type", "text").field("fields",map1).endObject()
					.startObject("ALARMNAME").field("type", "keyword").endObject()
					.startObject("ALARMORIGIN").field("type", "keyword").endObject()
					.startObject("ALARMTEXT").field("type", "keyword").endObject()
					.startObject("BRANCH_NAME").field("type", "keyword").endObject()
					.startObject("CLEARANCEREPORTFLAG").field("type", "integer").endObject()
					.startObject("CLEARANCETIMESTAMP").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
					.startObject("EQUIPMENTNAME").field("type", "keyword").endObject()
					.startObject("EQUIPMENTNAME_ALIAS").field("type", "keyword").endObject()
					.startObject("EVENTTIME").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
					.startObject("IPADDRESS").field("type", "keyword").endObject()
					.startObject("LOCATIONINFO").field("type", "keyword").endObject()
					.startObject("PERCEIVEDSEVERITY").field("type", "keyword").endObject()
					.startObject("REGION").field("type", "keyword").endObject()
					.startObject("RENOVATE_FLAG").field("type", "keyword").endObject()
					.startObject("REPAIRACTIONADVICE").field("type", "keyword").endObject()
					.startObject("SEND_TIME").field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
					.startObject("SERIAL").field("type", "keyword").endObject()
					.startObject("SPECIALTY").field("type", "keyword").endObject()
					.startObject("OSS_DEV_NAME").field("type", "keyword").endObject()
					.startObject("NECLASS").field("type", "keyword").endObject()
					.startObject("ONLY_ID").field("type", "keyword").endObject()


					.endObject().endObject().endObject();

			PutMappingRequest putMappingRequest = Requests.putMappingRequest("alarm-msg-20200914").type(typeName).source(mapBuilder);
			client.admin().indices().putMapping(putMappingRequest).actionGet();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
