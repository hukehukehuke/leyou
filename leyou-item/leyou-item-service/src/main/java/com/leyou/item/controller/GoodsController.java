package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.SpuBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据条件分页查询Spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
         @RequestParam(value = "key" ,required = false) String key,
         @RequestParam(value = "saleable" ,required =false )  Boolean saleable ,
         @RequestParam(value = "page" ,defaultValue = "1") Integer page,
         @RequestParam(value = "rows" ,defaultValue = "5") Integer rows

    ){
       PageResult<SpuBo> pageResult = this.goodsService.querySpuByPage(key,saleable,page,rows);
       if(pageResult == null || CollectionUtils.isEmpty(pageResult.getItem())){
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(pageResult);
    }
}
