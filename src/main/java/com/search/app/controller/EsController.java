package com.search.app.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.search.app.model.Search;
import com.search.app.service.IEsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author vikas gaikwad
 *
 */
@RestController
@RequestMapping("/es")

@Api(value = "elasticSearching", description = "CRUD operations for elastic search")
public class EsController<T> {

	@Autowired
	IEsService esService;

	public EsController() {

	}

	public EsController(IEsService esService) {
		this.esService = esService;
	}

	/* ======================================================================= */
	@PostMapping("/save")
	@ApiOperation(value = "Find student by id", notes = "Also returns a link to retrieve all notes with rel - all-notes")
	ResponseEntity<?> save(@RequestBody T obj) throws Exception {

		esService.save(obj);
		return new ResponseEntity<T>(HttpStatus.CREATED);

	}
	/* ======================================================================= */

	@GetMapping("/get/{id}")

	ResponseEntity<?> getById(@PathVariable String id) throws JsonParseException, JsonMappingException, IOException {
		Object obj = esService.getById(id, Search.class);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	// =======================================================================

	@PutMapping("update/{id}")
	ResponseEntity<T> update(@RequestBody T obj, @PathVariable String id) throws IOException {
		esService.updateById(id, obj);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// =======================================================================

	@DeleteMapping("delete/{id}")
	ResponseEntity<T> deleteById(@PathVariable String id) throws JsonProcessingException, IOException {
		esService.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// =======================================================================
	@PostMapping("/filterSearch")
	ResponseEntity<T> filterSearch(@RequestBody Search value, String field) throws Exception {
		esService.filterSearchQuery(field, value, Search.class);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// =======================================================================
	
	@PostMapping("/searchByText/{text}")
	ResponseEntity<?> searchByText(@PathVariable String text) throws IOException {
		Object obj = esService.searchByText(text,Search.class);
		return new ResponseEntity<> (obj,HttpStatus.OK);
	}
}
