package com.staryea.rest;

import java.sql.SQLFeatureNotSupportedException;

import org.elasticsearch.client.RestHighLevelClient;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class MyESActionFactory {
	public static MyQueryAction create(RestHighLevelClient client, String sql)
			throws SqlParseException, SQLFeatureNotSupportedException {
		String firstWord = sql.substring(0, sql.indexOf(' '));
		String str1 = firstWord.toUpperCase();
		int i = -1;
		switch (str1.hashCode()) {
		case -1852692228:
			if (!str1.equals("SELECT"))
				break;
			i = 0;
			break;
		case 2012838315:
			if (!str1.equals("DELETE"))
				break;
			i = 1;
		}
		switch (i) {
		case 0:
			SQLQueryExpr sqlExpr = (SQLQueryExpr) toSqlExpr(sql);
			Select select = new SqlParser().parseSelect(sqlExpr);
			if (select.getGroupBys().size() > 0) {
				return new MyAggregationQueryAction(client, select);
			}
			return new MyDefaultQueryAction(client, select);
		case 1:
			SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
			SQLDeleteStatement deleteStatement = parser.parseDeleteStatement();
//			Delete delete = new SqlParser().parseDelete(deleteStatement);
			// 暂不支持
			throw new SQLFeatureNotSupportedException(String.format("Unsupported delete: %s", new Object[] { sql }));
		}

		throw new SQLFeatureNotSupportedException(String.format("Unsupported query: %s", new Object[] { sql }));
	}

	private static SQLExpr toSqlExpr(String sql) {
		SQLExprParser parser = new ElasticSqlExprParser(sql);
		SQLExpr expr = parser.expr();

		if (parser.getLexer().token() != Token.EOF) {
			throw new ParserException("illegal sql expr : " + sql);
		}

		return expr;
	}

}