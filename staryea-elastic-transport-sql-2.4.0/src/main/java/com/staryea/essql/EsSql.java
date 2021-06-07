package com.staryea.essql;

import org.elasticsearch.client.Client;

import java.util.List;
import java.util.Map;

/**
 * @author wenb
 */
public interface EsSql {
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
     * @param column
     * @param start
     * @param time
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryForListGroupHours(String sql, String column, long start, long time) throws Exception;

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
    long queryForCount(String sql) throws Exception;

    /**
     * delete record by query
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
    long deleteBySql(String sql) throws Exception;


    /**
     * query for uniq record
     *
     * @param sql
     * @param he
     * @return T or <code>null</code>
     * @throws Exception
     */
    Map<String, Object> queryForOne(String sql) throws Exception;

    /**
     * query for records
     *
     * @param sql
     * @param he
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryForList(String sql) throws Exception;

    /**
     * query for records by union
     *
     * @param sql
     * @param he
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> queryForListUnion(String sql) throws Exception;

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
    List<Map<String, Object>> queryForList(String sql, int s, int r) throws Exception;

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
    List<Map<String, Object>> queryForListGroup(String sql) throws Exception;

    /**
     * query for records by group and by page (start is 0)
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
    List<Map<String, Object>> queryForListGroup(String sql, int r) throws Exception;

    /**
     * set client by spring inject
     *
     * @param client
     */
    void setClient(Client client);
}
