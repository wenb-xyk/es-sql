package com.staryea.essql;


import java.sql.SQLFeatureNotSupportedException;

//import org.durid.sql.SQLUtils;
//import org.durid.sql.ast.expr.SQLQueryExpr;
//import org.durid.sql.ast.statement.SQLDeleteStatement;
//import org.durid.sql.parser.SQLParserUtils;
//import org.durid.sql.parser.SQLStatementParser;
import org.elasticsearch.client.Client;
import org.nlpcn.es4sql.domain.Delete;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;
import org.nlpcn.es4sql.query.DefaultQueryAction;
import org.nlpcn.es4sql.query.DeleteQueryAction;
import org.nlpcn.es4sql.query.QueryAction;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class MyESActionFactory {
	public static QueryAction create(Client client, String sql)
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

			if (select.isAgg) {
				System.out.println(select.isAgg);
				return new MyAggregationQueryAction(client, select);
			}
			return new DefaultQueryAction(client, select);
		case 1:
			SQLStatementParser parser = SQLParserUtils
					.createSQLStatementParser(sql, "mysql");
			SQLDeleteStatement deleteStatement = parser.parseDeleteStatement();
			Delete delete = new SqlParser().parseDelete(deleteStatement);
			return new DeleteQueryAction(client, delete);
		}

		throw new SQLFeatureNotSupportedException(String.format(
				"Unsupported query: %s", new Object[] { sql }));
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