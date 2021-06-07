package com.staryea.essql.junit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;

public class ElasticSearchBulkIn {

	public static void main(String[] args) {

		try {

			Settings settings = Settings.builder().put("cluster.name", "mytest").put("client.transport.sniff", true)
					.build();

			TransportClient client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.2.170"), 9300));
			// ObjectEs543Sql support = new ObjectEs543Sql ();
			// support.setClient(client);
			for (int i = 0; i < 105; i++) {
				String line = null;
				long time1 = System.currentTimeMillis();
				BulkRequestBuilder bulkRequest = client.prepareBulk();
				int count = 0;
				JSONObject js = new JSONObject();
				SimpleDateFormat  sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				int num = 0;
				String x = "{\r\n" + "    \"BRANCH_NAME\": \"\",\r\n" + "    \"ORGIN_IP\": \"61.139.108.1" + i
						+ "\",\r\n" + "    \"CLEARANCETIMESTAMP\": \""+sf.format(new Date())+"\",\r\n"
						+ "    \"OSS_DEV_NAME\": \"SC-CD-LQZXJ5F-B-2.STN.ZX9K18EA\",\r\n"
						+ "    \"SEND_TIME\": \"2020-09-14 09:15:35\",\r\n" + "    \"ONLY_ID\": \"210\",\r\n"
						+ "    \"ALARMTEXT\": \"devicetype=ZXR10 ZXCTN 9000-18EA\",\r\n"
						+ "    \"ALARMORIGIN\": \"133.38.94.217\",\r\n"
						+ "    \"ADDITIONALTEXT\": \"MOI=SHELF:9999,SLOT:9999,PORT:9999;EntPhysicalName=,;additionalText=,(描述:,);告警名称=端口链路协议DOWN;\",\r\n"
						+ "    \"PERCEIVEDSEVERITY\": \"5\",\r\n" + "    \"EVENTTIME\": \"2020-09-14 09:15:35\",\r\n"
						+ "    \"SPECIALTY\": \"新型城域网\",\r\n" + "    \"NECLASS\": \"LEAF\",\r\n"
						+ "    \"CLEARANCEREPORTFLAG\": \"1\",\r\n" + "    \"ALARMNAME\": \"SNMP_Link_Down\",\r\n"
						+ "    \"LOCATIONINFO\": \"中兴\",\r\n"
						+ "    \"EQUIPMENTNAME\": \"SC-CD-LQZXJ5F-B-2.STN.ZX9K18EA\",\r\n"
						+ "    \"SERIAL\": \"d1f1dfa10b2740c3a173b5e8e56592a2/1.3.6.1.6.3.1.1.5.3(1.3.6.1.6.3.1.1.5.4)/1600046135\",\r\n"
						+ "    \"REGION\": \"成都\",\r\n" + "    \"IPADDRESS\": \"61.139.108.12\",\r\n"
						+ "    \"REPAIRACTIONADVICE\": \"\",\r\n"
						+ "    \"EQUIPMENTNAME_ALIAS\": \"SC-CD-LQZXJ5F-B-2.STN.ZX9K18EA\",\r\n"
						+ "    \"RENOVATE_FLAG\": \"0\",\r\n"
						+ "    \"@timestamp\": \"2020-09-14T09:15:35.175+0800\"\r\n" + "  }\r\n" + "";

				Map map = JSON.parseObject(x, Map.class);

				// while(true){
				bulkRequest.add(client.prepareIndex("alarm-msg-20200914", "current").setSource(map));

				long time2 = System.currentTimeMillis();
				bulkRequest.execute().actionGet();
				System.out.println(time2 - time1);
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}