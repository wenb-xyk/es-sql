package com.staryea.essql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation.SingleValue;
import org.nlpcn.es4sql.domain.Field;
import org.nlpcn.es4sql.query.ESActionFactory;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;

public class ObjectEs4Sql_alias<T> implements Es4Sql<T> {

	private Client client;

	@Override
	public long queryForCountGroup(String sql) throws Exception {
		sql = sql.trim();
		check();
		List<Map<String, Object>> list = queryForListGroup(sql, 0);
		return list.size();
	}

	@Override
	public long queryForCount(String sql) throws Exception {
		sql = sql.trim();
		check();
		SearchResponse x = (SearchResponse) createSearchRequestBuilder(client, sql).get();

		SearchHits shs = x.getHits();
		long size = shs.getTotalHits();
		return size;
	}

	@Override
	public T queryForOne(String sql, HitEntry<T> he) throws Exception {
		sql = sql.trim();
		check();

		SearchResponse x = (SearchResponse) createSearchRequestBuilder(client, sql).get();

		SearchHits shs = x.getHits();
		long size = shs.getTotalHits();
		if (size < 1)
			return null;
		return he.mapper(shs.getHits()[0]);
	}

	@Override
	public List<T> queryForList(String sql, HitEntry<T> he) throws Exception {
		// must set size, otherwise get 200 records only
		sql = sql.trim();
		return queryForList(sql, 0, 10000, he);
	}

	@Override
	public List<T> queryForList(String sql, int s, int r, HitEntry<T> he) throws Exception {
		sql = sql.trim();
		check();
		SqlElasticSearchRequestBuilder x = (SqlElasticSearchRequestBuilder) createSearchRequestBuilder(client, sql);
		SearchRequestBuilder y = (SearchRequestBuilder) x.getBuilder();

		y.setFrom(s).setSize(r);
		return mappers(y, he);
	}

	@Override
	public List<Map<String, Object>> queryForListGroup(String sql) throws Exception {
		sql = sql.trim();
		return queryForListGroup(sql, 200);
	}

	@Override
	public List<Map<String, Object>> queryForListGroup(String sql, int r) throws Exception {
		check();
		sql = sql.trim();
		MyAggregationQueryAction aqa = (MyAggregationQueryAction) MyESActionFactory.create(client, sql);
		SearchRequestBuilder srb = aqa.explain(r);
		if (r != Integer.MIN_VALUE)
			srb.setFrom(0);
		SearchResponse sr = srb.get();
		Mapp parent = null;
		List<Mapp> mapps = new ArrayList<>();
		for (Aggregation agt : sr.getAggregations().asList()) {
			if (agt instanceof InternalTerms) {
				InternalTerms terms = (InternalTerms) agt;
				Collection<Bucket> buckets = terms.getBuckets();

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
			}
		}
		List<Field> fields = aqa.getFields();
		// merge
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		LinkedHashMap<String, Map<String, Object>> retMap = new LinkedHashMap<String, Map<String, Object>>();
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
		Collection<Map<String, Object>> it = retMap.values();

		for (Map<String, Object> map : it) {
			list.add(map);
		}

		mapps.clear();
		mapps = null;
		retMap.clear();
		retMap = null;
		return list;
	}

	private void loopBucket(List<Mapp> mapps, Mapp parent, InternalTerms terms, Bucket bucket) {
		List<Aggregation> aggs = bucket.getAggregations().asList();
		if (aggs.isEmpty()) {
			mapps.add(parent);
			return;
		}

		for (Aggregation agg : aggs) {

			if (agg instanceof InternalTerms) {
				InternalTerms it = (InternalTerms) agg;
				List<Bucket> buckets = it.getBuckets();
				for (Bucket bk : buckets) {
					Mapp cm = new Mapp();
					cm.key = it.getName();
					cm.value = bk.getKeyAsString();
					cm.parent = parent;
					loopBucket(mapps, cm, it, bk);
				}

				// for (Bucket bk : it.getBuckets()) {
				// Mapp cm = new Mapp();
				// cm.key = it.getName();
				// cm.value = bk.getKeyAsText().string();
				// cm.parent = parent;
				// loopBucket(mapps, cm, it, bk);
				// }
			} else if (agg instanceof SingleValue) {

				SingleValue sv = (SingleValue) agg;
				Mapp cm = new Mapp();
				cm.key = sv.getName();
//				cm.value = String.valueOf(sv.value());
//				cm.value = String.valueOf(sv.value());
				// ����ȥ����ֵ�����.0
				String temp = String.valueOf(sv.value());
				if (temp.endsWith(".0")) {
					temp = temp.substring(0, temp.length() - 2);
				}
				cm.value = temp;
				cm.parent = parent;
				mapps.add(cm);
			}
		}
	}

	private List<T> mappers(SearchRequestBuilder srb, HitEntry<T> he) {
		check();
		SearchHits shs = srb.get().getHits();
		List<T> list = new ArrayList<>((int) shs.getTotalHits());
		Iterator<SearchHit> it = shs.iterator();
		SearchHit y = null;
		while (it.hasNext())
			y = it.next();
		Map<String, SearchHitField> also = y.getFields();
		Map map = (Map) he.mapper(y);
		for (String key : also.keySet()) {
			map.put(key, also.get(key).getValue());
		}
		list.add((T) map);
		return list;
	}

	private SqlElasticSearchRequestBuilder createSearchRequestBuilder(Client client, String sql) throws Exception {

		return (SqlElasticSearchRequestBuilder) (ESActionFactory.create(client, sql)).explain();
	}

	private void check() {
		if (this.client == null)
			throw new NullPointerException("please inject client transport!!!");
	}

	@Override
	public void setClient(Client client) {
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
