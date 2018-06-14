package com.ims.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ims.dto.ResponseDto;
import com.ims.service.ElasticSearchService;

@RestController
@RequestMapping("/es")
public class ElasticSearchController {
	
	private static final Logger LOG = Logger.getLogger(ElasticSearchController.class);
	
	@Autowired
	private ElasticSearchService elasticSearchService;
	
	@GetMapping(value = "/elasticsearch/{searchtext}")
	public ResponseDto elasticsearch(@PathVariable String searchtext) {
		return elasticSearchService.elasticsearch(searchtext);
	}

}
