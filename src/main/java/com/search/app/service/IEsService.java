package com.search.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.search.app.model.Search;

public interface IEsService {

	<T> Search save(T obj) throws JsonProcessingException, IOException;

	<T> T getById(String id, Class<T> className) throws JsonParseException, JsonMappingException, IOException;

	<T> Object deleteById(String id) throws JsonProcessingException, IOException;

	<T> Search updateById(String id, T obj) throws IOException;

	public <T> List<T> filterSearchQuery(String field, Object value, Class<Search> classType) throws IOException;

	<T> List<T> searchByText(String text, Class<T> classType) throws IOException;

	
}
