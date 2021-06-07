package com.staryea.essql.junit;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.staryea.essql.HitEntry;
import com.staryea.essql.MapHitEntry;
import com.staryea.essql.ObjectEs4Sql;

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
		// Map<String,String> setting = new HashMap<String,String>();
		// setting.put( "cluster.name", "cluster_cms" );
		// setting.put( "client.transport.ping_timeout", "10s" );
		// setting.put( "client.transport.sniff", "true" );
		// Settings settings = ImmutableSettings.settingsBuilder().put( setting
		// ).build();
		// TransportClient tc = new TransportClient( settings );
		// tc.addTransportAddress( new InetSocketTransportAddress(
		// "192.168.56.2", 9300 ) )
		// .addTransportAddress( new InetSocketTransportAddress( "192.168.56.2",
		// 9301 ) );
		Settings settings = Settings.settingsBuilder().put("client.transport.sniff", false).put("path.home", ".")
				.put("cluster.name", "ndi").put("searchguard.ssl.transport.enabled", true)
				.put("searchguard.ssl.transport.keystore_filepath", "C:/Users/admin/Desktop/es/admin-keystore.jks")
				.put("searchguard.ssl.transport.truststore_filepath", "C:/Users/admin/Desktop/es/truststore.jks")
				.put("searchguard.ssl.transport.keystore_password", "elastic")
				.put("searchguard.ssl.transport.truststore_password", "elastic")
				.put("searchguard.ssl.transport.enforce_hostname_verification", false).build();
		TransportClient client = TransportClient.builder().settings(settings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 5300));
		ObjectEs4Sql<Map> support = new ObjectEs4Sql();
		support.setClient(client);
		HitEntry<Map> he = new MapHitEntry();
		// support.queryForList(sql, he)

		// ȫ����ѯ
		long time1 = System.currentTimeMillis();

		// List<Map> ll1 = support
		// .queryForList(
		// "SELECT sn,accessnumber FROM stb_data_2017_10_23 ",
		// 0, 10000, he);
		// System.out.println(ll1.size());
		// for (Map map : ll1) {
		// String sn = map.get("sn") + "";
		// String accessnumber = map.get("accessnumber") + "";
		// List<Map> ll2 = support.queryForList(
		// "SELECT SN FROM stb_decline_2017_10_23 where SN='" + sn
		// + "' ", 0, 1, he);
		// if (ll2.size() == 0) {
		// continue;
		// }
		// ll2 = support.queryForList(
		// "SELECT sn FROM stb_quality_2017_10_23 where sn='" + sn
		// + "' ", 0, 1, he);
		// if (ll2.size() == 0) {
		// continue;
		// }
		// ll2 = support.queryForList(
		// "SELECT sn FROM stb_business_2017_10_23 where sn='" + sn
		// + "' ", 0, 1, he);
		// if (ll2.size() == 0) {
		// continue;
		// }
		// ll2 = support.queryForList(
		// "SELECT * FROM stb_outage_2017_10_23 where sn='" + sn
		// + "' ", 0, 1, he);
		// if (ll2.size() == 0) {
		// continue;
		// }
		// if(StringUtil.IsEmpty(accessnumber)){
		// continue;
		// }
		// System.out.println("accessnumber:"+accessnumber);
		// ll2 = support.queryForList(
		// "SELECT * FROM tree_main_2017_10_23 where ACCOUNT_NO='" +
		// accessnumber
		// + "' ", 0, 1, he);
		// if (ll2.size()> 0) {
		// System.out.println(ll2.get(0).get("LOID"));
		// break;
		// }
		//
		//
		// }
		// try{
		//
		// List<Map> x =
		// support.queryForList("select * from tree_main_2017_11_07 ",0,1,he);
		// System.out.println(x);
		// }catch (Exception e) {
		// e.printStackTrace();
		// }
		// UpdateRequest uRequest = new UpdateRequest();
		// uRequest.index("optical_info_es_20180506");
		// uRequest.type("optical_info_es_20180506");
		// uRequest.id("5711SN02832498");
		// uRequest.doc(map) ;
		// client.update(uRequest);
		// client.close();
		List<Map<String, Object>> x = support.queryForListGroup(
				"select    count(distinct  orgleve5code)  from rpt_svr_open_billdata_info where archdate_int >=1525104000 and archdate_int <=1526659200 and kd_branch_office_id='570013'   and  kd_branch_office_id is not null   and kd_branch_post_office_id  is not null and  kd_branch_office_id='570013' group  by  kd_branch_post_office_id,kd_branch_post_office  order  by  kd_branch_post_office desc",
				100);
		System.out.println(x);

//		List<Map> x = support.queryForList("SELECT * FROM ter_distribution_info_es where ter_flag=0 and status=3 and serial_id like '%_%'", 0,200000,  he);
////		List<Map<String, Object>> y = getCountBykey(x,"kd_branch_post_office","test");
//		System.out.println(x.size());
//		List<Map>  y= new  ArrayList<Map>();
//		for(Map map:x){
//			String serial_id=String.valueOf(map.get("serial_id"));
//			if(serial_id.contains("_") &&  serial_id.length()==24){
//				serial_id= serial_id.replace("_", "-");
//				UpdateRequest uRequest = new UpdateRequest();
//				uRequest.index("ter_distribution_info_es");
//				uRequest.type("ter_distribution_info_es");
//				uRequest.id(serial_id);
//				map.put("serial_id", serial_id);
//				uRequest.doc(map);
//				y.add(map);
//			}
//			
//		}
//		  BulkRequestBuilder bulkRequest=client.prepareBulk();
//// 		ObjectEs4Sql<Map> support = new ObjectEs4Sql<Map>();
//		for(Map map:x){
//			String serial_id=String.valueOf(map.get("serial_id"));
//			UpdateRequest uRequest = new UpdateRequest();
//			uRequest.index("ter_distribution_info_es");
//			uRequest.type("ter_distribution_info_es");
//			uRequest.id(serial_id);
//			map.put("serial_id", serial_id);
//			map.put("ter_flag", "1");
//			map.put("status", "3");
//			uRequest.doc(map);
//			bulkRequest.add(uRequest);	
//			
//		}
//		 bulkRequest.execute().actionGet();
//		System.out.println(x.size());
		// System.out.println(x);
		// List<Map> temp =
		// support.queryForList("select * from monitor_20180509", 0, 1,he);
		// System.out.println(temp);
	}

}
