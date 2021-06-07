package com.staryea.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.nlpcn.es4sql.domain.Field;
import org.nlpcn.es4sql.domain.KVValue;
import org.nlpcn.es4sql.domain.MethodField;
import org.nlpcn.es4sql.domain.Order;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.domain.Where;
import org.nlpcn.es4sql.domain.hints.Hint;
import org.nlpcn.es4sql.domain.hints.HintType;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.maker.QueryMaker;

/**
 * Transform SQL query to standard Elasticsearch search query
 */
public class MyDefaultQueryAction extends MyQueryAction {

	private final Select select;
	private SearchSourceBuilder request;
	private SearchRequest searchRequest;

	public MyDefaultQueryAction(RestHighLevelClient client, Select select) {
		super(client, select);
		this.select = select;
	}

	public void intialize(SearchSourceBuilder request) throws SqlParseException {
		this.request = request;
	}

	@Override
	public SearchResponse get(int s, int r) throws SqlParseException, IOException {
		Hint scrollHint = null;
		for (Hint hint : select.getHints()) {
			if (hint.getType() == HintType.USE_SCROLL) {
				scrollHint = hint;
				break;
			}
		}
		this.request = new SearchSourceBuilder();

		this.searchRequest = new SearchRequest();
		setIndicesAndTypes();

		// zhongshu-comment ��Select�����з�װ��sql token��Ϣת����������Ա����es�����������request��
		setFields(select.getFields());

		setWhere(select.getWhere());
		setSorts(request, select.getOrderBys());
		setLimit(request, select.getOffset(), select.getRowCount(), s, r);

		//
		if (scrollHint != null) {
			if (!select.isOrderdSelect())
				request.sort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
			request.size((Integer) scrollHint.getParams()[0]);
			searchRequest.scroll(new TimeValue((Integer) scrollHint.getParams()[1]));
		} else {
			searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
		}

		updateRequestWithIndexAndRoutingOptions(select, searchRequest);
		updateRequestWithHighlight(select, request);
		updateRequestWithCollapse(select, request);
		updateRequestWithPostFilter(select, request);
		searchRequest.source(request);
		SearchResponse searchResponse = client.search(searchRequest);
		return searchResponse;
	}

	/**
	 * Set indices and types to the search request.
	 */
	private void setIndicesAndTypes() {
		searchRequest.indices(query.getIndexArr());

		String[] typeArr = query.getTypeArr();
		if (typeArr != null) {
			searchRequest.types(typeArr);
		}
	}

	/**
	 * Set source filtering on a search request. zhongshu-comment ��es
	 * dsl�е�include��exclude
	 * 
	 * @param fields list of fields to source filter.
	 */
	public void setFields(List<Field> fields) throws SqlParseException {
		 
		if (select.getFields().size() > 0) {
			ArrayList<String> includeFields = new ArrayList<String>();
			ArrayList<String> excludeFields = new ArrayList<String>();

			for (Field field : fields) {
				if (field instanceof MethodField) {  
					MethodField method = (MethodField) field;
					if (method.getName().toLowerCase().equals("script")) {
						handleScriptField(method, request);
					} else if (method.getName().equalsIgnoreCase("include")) {
						for (KVValue kvValue : method.getParams()) {
							includeFields.add(kvValue.value.toString());
						}
					} else if (method.getName().equalsIgnoreCase("exclude")) {
						for (KVValue kvValue : method.getParams()) {
							excludeFields.add(kvValue.value.toString());
						}
					}
				} else if (field instanceof Field) {
					includeFields.add(field.getName());
				}
			}

			request.fetchSource(includeFields.toArray(new String[includeFields.size()]),
					excludeFields.toArray(new String[excludeFields.size()]));
		}
	}

	private static void handleScriptField(MethodField method, SearchSourceBuilder searchSourceBuilder)
			throws SqlParseException {
		List<KVValue> params = method.getParams();
		if (params.size() == 2) {
			searchSourceBuilder.scriptField(params.get(0).value.toString(), new Script(params.get(1).value.toString()));
		} else if (params.size() == 3) {
			searchSourceBuilder.scriptField(params.get(0).value.toString(), new Script(ScriptType.INLINE,
					params.get(1).value.toString(), params.get(2).value.toString(), new HashMap<String, Object>()));
		} else {
			throw new SqlParseException("scripted_field only allows script(name,script) or script(name,lang,script)");
		}
	}

	/**
	 * Create filters or queries based on the Where clause.
	 * 
	 * @param where the 'WHERE' part of the SQL query.
	 * @throws SqlParseException
	 */
	private void setWhere(Where where) throws SqlParseException {
		if (where != null) {
			BoolQueryBuilder boolQuery = QueryMaker.explan(where, this.select.isQuery);
			request.query(boolQuery);
		}
	}

	/**
	 * Add sorts to the elasticsearch query based on the 'ORDER BY' clause.
	 * 
	 * @param orderBys list of Order object
	 */
	private static void setSorts(SearchSourceBuilder searchSourceBuilder, List<Order> orderBys) {

		for (Order order : orderBys) {
			if (order.getNestedPath() != null) {
				searchSourceBuilder
						.sort(SortBuilders.fieldSort(order.getName()).order(SortOrder.valueOf(order.getType()))
								.setNestedSort(new NestedSortBuilder(order.getNestedPath())));
			} else if (order.getName().contains("script(")) {  
//				String scriptStr = order.getName().substring("script(".length(), order.getName().length() - 1);
//				Script script = new Script(scriptStr);
//				ScriptSortBuilder scriptSortBuilder = SortBuilders.scriptSort(script, order.getScriptSortType());
//
//				scriptSortBuilder = scriptSortBuilder.order(SortOrder.valueOf(order.getType()));
//				searchSourceBuilder.sort(scriptSortBuilder);
			} else {
				searchSourceBuilder.sort(order.getName(), SortOrder.valueOf(order.getType()));
			}
		}
	}

	/**
	 * Add from and size to the ES query based on the 'LIMIT' clause
	 * 
	 * @param from starts from document at position from
	 * @param size number of documents to return.
	 */
	private static void setLimit(SearchSourceBuilder searchSourceBuilder, int from, int size, int s, int r) {
		if (r == 0) {
			searchSourceBuilder.from(from);
			searchSourceBuilder.size(size);
		} else {
			searchSourceBuilder.from(s);
			searchSourceBuilder.size(r);
		}
	}

}
