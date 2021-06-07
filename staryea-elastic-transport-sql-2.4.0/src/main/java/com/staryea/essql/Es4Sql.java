package com.staryea.essql;


import java.util.List;
import java.util.Map;
import org.elasticsearch.client.Client;

/**
 * 
 * @author wenb
 * 
 */
public interface Es4Sql<T> {

	/**
	 * query record's size
	 * 
	 * <ul>
	 * support write ways
	 * <li>select count(*)</li>
	 * <li>select count(column)</li>
	 * </ul>
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	long queryForCount( String sql ) throws Exception;

	/**
	 * query for uniq record
	 * 
	 * @param sql
	 * @param he
	 * @return T or <code>null</code>
	 * @throws Exception
	 */
	T queryForOne( String sql, HitEntry<T> he ) throws Exception;

	/**
	 * query for records
	 * 
	 * @param sql
	 * @param he
	 * @return
	 * @throws Exception
	 */
	List<T> queryForList( String sql, HitEntry<T> he ) throws Exception;

	/**
	 * query for records by page
	 * 
	 * @param sql
	 * @param s
	 * @param r
	 * @param he
	 * @return
	 * @throws Exception
	 */
	List<T> queryForList( String sql, int s, int r, HitEntry<T> he ) throws Exception;

	/**
	 * query for records by group
	 * 
	 * <ul>
	 * support group functions
	 * <li>sum</li>
	 * <li>max</li>
	 * <li>min</li>
	 * <li>avg</li>
	 * <li>count</li>
	 * <li>stats</li>
	 * <li>tophits</li>
	 * </ul>
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> queryForListGroup( String sql ) throws Exception;

	/**
	 * query for records by group and by page
	 * 
	 * <ul>
	 * support group functions
	 * <li>sum</li>
	 * <li>max</li>
	 * <li>min</li>
	 * <li>avg</li>
	 * <li>count</li>
	 * <li>stats</li>
	 * <li>tophits</li>
	 * </ul>
	 * 
	 * @param sql
	 * @param r  ȡ������
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> queryForListGroup( String sql,   int r ) throws Exception;

	/**
	 * query for records by group and by page
	 * 
	 * <ul>
	 * support group functions
	 * <li>sum</li>
	 * <li>max</li>
	 * <li>min</li>
	 * <li>avg</li>
	 * <li>count</li>
	 * <li>stats</li>
	 * <li>tophits</li>
	 * </ul>
	 * 
	 * @param sql
	 * @param s
	 * @param r
	 * @return
	 * @throws Exception
	 */
	long queryForCountGroup( String sql) throws Exception;
	
	
	/**
	 * set client by spring inject
	 * 
	 * @param client
	 */
	void setClient( Client client );
}
