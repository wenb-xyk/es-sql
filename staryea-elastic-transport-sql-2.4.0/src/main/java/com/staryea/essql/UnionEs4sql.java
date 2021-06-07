package com.staryea.essql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.client.Client;

/**
 * 
 * @author wenb
 * 
 */
public class UnionEs4sql<T> implements Es4Sql<T> {

	private final static Pattern UN_P = Pattern.compile("[ ]+[uU][nN][iI][oO][nN][ ]+([aA][lL][lL])?");
	private Es4Sql<T> es4sql;

	public UnionEs4sql(Es4Sql<T> es4sql) {
		this.es4sql = es4sql;
	}

	private static boolean unionExists(String sql) {
		Matcher m = UN_P.matcher(sql);
		if (m.find()) {
			sql = sql.replaceAll(UN_P.pattern(), "UNION");
		}
		return sql.contains("UNION");
	}

	private static String[] splitSql(String sql) {
		String[] sqlArray = sql.split("UNION");
		return sqlArray;
	}

	@Override
	public long queryForCount(String sql) throws Exception {
		return es4sql.queryForCount(sql);
	}

	@Override
	public T queryForOne(String sql, HitEntry<T> he) throws Exception {
		if (unionExists(sql)) {
			String[] sqlArray = splitSql(sql);
			T result = null;
			for (String s : sqlArray) {
				result = es4sql.queryForOne(s, he);
				if (result != null)
					return result;
			}
			return null;
		} else {
			return es4sql.queryForOne(sql, he);
		}
	}

	@Override
	public List<T> queryForList(String sql, HitEntry<T> he) throws Exception {
		if (unionExists(sql)) {
			String[] sqlArray = splitSql(sql);
			List<T> list = new ArrayList<T>();
			for (String s : sqlArray) {
				list.addAll(es4sql.queryForList(s, he));
			}
			return list;
		} else {
			return es4sql.queryForList(sql, he);
		}
	}

	@Override
	public List<T> queryForList(String sql, int s, int r, HitEntry<T> he) throws Exception {
		return es4sql.queryForList(sql, s, r, he);
	}

	@Override
	public List<Map<String, Object>> queryForListGroup(String sql) throws Exception {
		return es4sql.queryForListGroup(sql);
	}

	@Override
	public List<Map<String, Object>> queryForListGroup(String sql, int r) throws Exception {
		return es4sql.queryForListGroup(sql, r);
	}

	@Override
	public void setClient(Client client) {
		// do nothing
	}

	@Override
	public long queryForCountGroup(String sql) throws Exception {
		return es4sql.queryForCountGroup(sql);
	}
}
