package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(value = "list")

    /**
     * 根据父节点ID查询子节点
    */
    public ResponseEntity<List<Category>> queryCategoryById(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        if (pid == null || pid < 0) {
            // return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
            //return new ResponseEntity<>(HttpStatus.MULTI_STATUS);
            return ResponseEntity.badRequest().build();
        }
        List<Category> categoryList = categoryService.queryCategoryById(pid);
        if (CollectionUtils.isEmpty(categoryList)) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
        }
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
    }

    /**
     *  添加商品类型
     */
    public ResponseEntity<Category> addCategory(@RequestBody Category category){
        int i = categoryService.addCategory(category);
        return ResponseEntity.accepted().build();
    }

    /**
     *  修改商品类型
     */
    public ResponseEntity<Category> editionCategory(@RequestBody Category category){
        int i = categoryService.editionCategory(category);
        return ResponseEntity.accepted().build();
    }

    /**
     *  删除商品类型
     */
    public ResponseEntity<Category> deleteCategoryByPid(@RequestParam(value = "pid") Long pid){
        if(pid == null || pid < 0){
            return ResponseEntity.badRequest().build();
        }
        int i = categoryService.deleteCategoryByPid(pid);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<String>>  queryNamesByIds(@RequestParam(value = "ids") List<Long> ids){
        List<String> categoryList = this.categoryService.queryNameByIds(ids);
        if(CollectionUtils.isEmpty(categoryList)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(categoryList);
    }
}
