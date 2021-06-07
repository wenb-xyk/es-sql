package com.staryea.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation.SingleValue;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.nlpcn.es4sql.domain.Field;

/**
 * @author wenb
 */
public class ObjectEsSqlRest {
	private RestHighLevelClient client;

	private final static Pattern UN_P = Pattern.compile("[ ]+[uU][nN][iI][oO][nN][ ]+([aA][lL][lL])?");

	private static boolean unionExists(String sql) {
		Matcher m = UN_P.matcher(sql);
		if (m.find()) {
			sql = sql.replaceAll(UN_P.pattern(), "union");
		}
		return sql.contains("union");
	}

	private static String[] splitSql(String sql) {
		String[] sqlArray = sql.split("union");
		return sqlArray;
	}

	public long queryForCount(String sql) throws Exception {
		check();
		MyDefaultQueryAction my = (MyDefaultQueryAction) MyESActionFactory.create(client, sql);
		SearchResponse x = my.get(0,0 );
		SearchHits shs = x.getHits();
		long size = shs.getTotalHits();
		return size;
	}

	public Map<String, Object> queryForOne(String sql) throws Exception {
		check();

		MyDefaultQueryAction my = (MyDefaultQueryAction) MyESActionFactory.create(client, sql);
		SearchResponse x = my.get(0, 1);

		SearchHits shs = x.getHits();
		long size = shs.getTotalHits();
		if (size < 1)
			return null;
		return shs.getHits()[0].getSourceAsMap();
	}

	public List<Map<String, Object>> queryForList(String sql) throws Exception {
		// must set size, otherwise get 10000 records only
		return queryForList(sql, 0, 10000);
	}

	public List<Map<String, Object>> queryForList(String sql, int s, int r) throws Exception {
		check();

		MyDefaultQueryAction my = (MyDefaultQueryAction) MyESActionFactory.create(client, sql);
		SearchResponse x = my.get(s, r);

		SearchHits shs = x.getHits();
		List<Map<String, Object>> list = new ArrayList<>((int) shs.getTotalHits());
		Iterator<SearchHit> it = shs.iterator();
		while (it.hasNext())
			list.add(it.next().getSourceAsMap());
		return list;

	}

	public List<Map<String, Object>> queryForListGroup(String sql) throws Exception {
		return queryForListGroup(sql, 200);
	}

	public List<Map<String, Object>> queryForListUnion(String sql) throws Exception {
		check();

		if (unionExists(sql)) {
			String[] sqlArray = splitSql(sql);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (String s : sqlArray) {
				list.addAll(queryForList(s));
			}
			return list;
		} else {
			return queryForList(sql);
		}
	}

	public List<Map<String, Object>> queryForListGroup(String sql, int r) throws Exception {
		check();
		MyAggregationQueryAction my = (MyAggregationQueryAction) MyESActionFactory.create(client, sql);
		SearchResponse sr = my.get(0, r);
		Mapp parent = null;
		List<Mapp> mapps = new ArrayList<>();

		for (Aggregation agt : sr.getAggregations().asList()) {
			if (agt instanceof ParsedStringTerms) {
				ParsedStringTerms terms = (ParsedStringTerms) agt;
				Collection<? extends Bucket> buckets = terms.getBuckets();
				for (Bucket b : buckets) {
					parent = new Mapp();
					parent.key = terms.getName();
					parent.value = b.getKeyAsString();
					loopBucket(mapps, parent, terms, b);
				}
			} else if (agt instanceof SingleValue) {
				SingleValue sv = (SingleValue) agt;
				Mapp mapp = new Mapp();
				mapp.key = sv.getName();
				mapp.value = String.valueOf(sv.value());
				mapp.parent = parent;
			}else if (agt instanceof ParsedSum) {

				ParsedSum sv = (ParsedSum) agt;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}
			else if (agt instanceof ParsedAvg) {

				ParsedAvg sv = (ParsedAvg) agt;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			} else if (agt instanceof ParsedMin) {

				ParsedMin sv = (ParsedMin) agt;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}else if (agt instanceof ParsedValueCount) {

				ParsedValueCount sv = (ParsedValueCount) agt;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}

		}
		List<Field> fields = my.getFields();
		// merge
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> retMap = new LinkedHashMap<String, Map<String, Object>>();
		for (Mapp mapp : mapps) {
			String key = "";
			Map<String, Object> dataMap = mapp.entry();
			for (Field f : fields) {
				if (dataMap.containsKey(f.getName())) {
					key += dataMap.get(f.getName()) + "-";
				}
			}
			if (IsEmpty(key)) {
				continue;
			}

			if (retMap.containsKey(key)) {
				retMap.get(key).putAll(dataMap);
			} else {
				retMap.put(key, dataMap);
			}
		}
		list.addAll(retMap.values());
		mapps.clear();
		mapps = null;
		retMap.clear();
		retMap = null;
		return list;
	}

	private void loopBucket(List<Mapp> mapps, Mapp parent, ParsedStringTerms terms, Bucket bucket) {
		List<Aggregation> aggs = bucket.getAggregations().asList();
		if (aggs.isEmpty()) {
			mapps.add(parent);
			return;
		}

		for (Aggregation agg : aggs) {

			if (agg instanceof ParsedStringTerms) {
				ParsedStringTerms it = (ParsedStringTerms) agg;
				List<? extends Bucket> buckets = it.getBuckets();
				for (Bucket bk : buckets) {
					Mapp cm = new Mapp();
					cm.key = it.getName();
					cm.value = bk.getKeyAsString();
					cm.parent = parent;
					loopBucket(mapps, cm, it, bk);
				}

			} else if (agg instanceof SingleValue) {

				SingleValue sv = (SingleValue) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}else if (agg instanceof ParsedSum) {

				ParsedSum sv = (ParsedSum) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}
			else if (agg instanceof ParsedAvg) {

				ParsedAvg sv = (ParsedAvg) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			} else if (agg instanceof ParsedMin) {

				ParsedMin sv = (ParsedMin) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}else if (agg instanceof ParsedValueCount) {

				ParsedValueCount sv = (ParsedValueCount) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
				cm.value = String.valueOf(sv.value());
				cm.parent = parent;
				mapps.add(cm);
			}
		}
	}

	private void check() {
		if (this.client == null)
			throw new NullPointerException("please inject client transport!!!");
	}

	public void setClient(RestHighLevelClient client) {
		this.client = client;
	}

	class Mapp {

		public Mapp parent;
		public String key;
		public String value;

		public Mapp() {
		}

		public Mapp(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return "{ parent : " + parent + ", key : " + key + ", value : " + value + " }";
		}

		public Map<String, Object> entry() {
			Map<String, Object> map = new HashMap<>();
			map.put(key, value);
			if (parent != null) {
				map.put(parent.key, parent.value);
				loop(map, parent);
			}
			return map;
		}

		private void loop(Map<String, Object> map, Mapp parent) {
			if (parent.parent != null) {
				map.put(parent.parent.key, parent.parent.value);
				loop(map, parent.parent);
			}
		}
	}

	public static boolean IsEmpty(String tem) {
		return IsEmpty(tem, true);
	}

	public static boolean IsEmpty(String tem, boolean flag) {
		if (tem == null) {
			return true;
		}

		if (true == flag) {
			if (tem.trim().length() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (tem.length() == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
