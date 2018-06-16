package com.search.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.app.model.Search;

@Component
public class EsServiceImpl implements IEsService {

	private final String INDEX = "notedata";
	private final String TYPE = "notes";

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Override
	public <T> Search save(T obj) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(obj);
		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE);
		indexRequest.source(json, XContentType.JSON);
		IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
		return null;
	}

	@Override
	public <T> T getById(String id, Class<T> className) throws JsonParseException, JsonMappingException, IOException {

		GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
		GetResponse response = restHighLevelClient.get(getRequest);
		if (response.isExists()) {
			ObjectMapper mapper = new ObjectMapper();

			T object = mapper.readValue(response.getSourceAsString(), className);
			return object;
		}
		return null;
	}

	@Override
	public <T> Search updateById(String id, T obj) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(obj);
		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id);
		updateRequest.doc(json, XContentType.JSON);
		UpdateResponse response = restHighLevelClient.update(updateRequest);

		return null;
	}

	@Override
	public <T> Result deleteById(String id) throws JsonProcessingException, IOException {
		DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
		DeleteResponse response = restHighLevelClient.delete(deleteRequest);
		return response.getResult();

	}

	public <T> List<T> filterSearchQuery(String field, Object value, Class<Search> classType) throws IOException {
		QueryBuilder queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery(field, value));

		// QueryBuilder queryBuilder =
		// QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery(name, text));

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(queryBuilder);
		SearchRequest searchRequest = new SearchRequest(INDEX).types(TYPE).source(sourceBuilder);
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
		List<T> searchResults = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		for (SearchHit hit : searchResponse.getHits()) {
			searchResults.add((T) mapper.readValue(hit.getSourceAsString(), classType));
		}

		return searchResults;

	}

	@Override
	public <T> List<T> searchByText(String text, Class<T> classType) throws IOException {

		QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(text);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(builder);
		SearchRequest request = new SearchRequest(INDEX).types(TYPE).source(sourceBuilder);
		SearchResponse response = restHighLevelClient.search(request);
		List<T> arrayList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		for (SearchHit hit : response.getHits()) {
			arrayList.add(mapper.readValue(hit.getSourceAsString(), classType));
			return arrayList;
		}

		return null;

	}

}
