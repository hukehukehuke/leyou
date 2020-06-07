package com.leyou.controller;

import com.leyou.bo.SearchRequest;
import com.leyou.common.pojo.PageResult;
import com.leyou.pojo.Goods;
import com.leyou.service.SearchService;
import com.leyou.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping(value = "page")
    public ResponseEntity<SearchResult> search(SearchRequest searchRequest){
        SearchResult pageResult = this.searchService.search(searchRequest);
        if (pageResult == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }
}
