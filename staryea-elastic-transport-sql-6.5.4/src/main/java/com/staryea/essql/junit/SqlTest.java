package com.staryea.essql.junit;

import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.fastjson.JSON;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.nlpcn.es4sql.parse.SqlParser;

import java.util.List;

public class SqlTest {

//    private static SQLStatementParser createSqlStatementParser(String sql) {
//        ElasticLexer lexer = new ElasticLexer(sql);
//        lexer.nextToken();
//        return new MySqlStatementParser(lexer);
//    }



    public static void main(String[] args) {
String  sql ="CREATE TABLE `uacp_in_release_api_info` (\n" +
        "  `api_id` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT 'api能力Id',\n" +
        "  `api_name` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API名称',\n" +
        "  `api_type` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API类型(V:虚指令、C：指令(模板)编排、S：场景)',\n" +
        "  `commond_id` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '指令ID',\n" +
        "  `request_method` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '请求方法类型POST、GET、DELETE',\n" +
        "  `request_format` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '请求包体类型XML、JSON',\n" +
        "  `request_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '请求地址',\n" +
        "  `tran_protocol` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '传输协议HTTP、WebService、RESTful',\n" +
        "  `comm_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '通讯方式同步、异步',\n" +
        "  `api_desc` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'API描述',\n" +
        "  `reg_time` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '注册时间',\n" +
        "  `reg_man` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '操作人',\n" +
        "  `api_status` varchar(4) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'api状态(0,预发布，1:已发布, 2:已取消）',\n" +
        "  `api_mode` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'API发布模式',\n" +
        "  `flow_config` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '流控设置',\n" +
        "  `overtime` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '超时时间',\n" +
        "  `api_version` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '版本信息',\n" +
        "  `route_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '路由规则ID',\n" +
        "  `callback_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '回调接口地址',\n" +
        "  `api_class` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'API发布分类根据字典s_dic_tree.api_class获取',\n" +
        "  `route_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '路由类型,取字典表dic_type=route_type',\n" +
        "  `forward_url` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '转发URL，针对需要进行特殊填充参数',\n" +
        "  `dcoos_url` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'dcoos中配置发布的URL',\n" +
        "  `is_ems` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否专业网管',\n" +
        "  `api_code` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'API服务编码',\n" +
        "  `api_sertype` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '业务类型，查字典表s_dic_tree.dic_type=api_sertype',\n" +
        "  PRIMARY KEY (`api_id`) USING BTREE\n" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;\n" +
        "\n";



        SQLCreateTableParser parser = new SQLCreateTableParser(sql);

        System.out.println( JSON.toJSONString(parser));


//        Create delete = new SqlParser().   .parseDelete(deleteStatement);
//        return new DeleteQueryAction(client, delete);
//
//        return new ShowQueryAction(client,sql);
    }



}
