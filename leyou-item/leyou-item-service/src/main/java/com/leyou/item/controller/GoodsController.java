package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping(value = "goods")
    public ResponseEntity<Void> saveGoods(SpuBo spuBo){
        this.goodsService.saveGoods(spuBo);
        return  ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
    }


    /**
     * 根据spuId查询SpuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable(value = "spuId") Long spuId){
        SpuDetail spuDetail = this.goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail == null){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping(value = "sku/list")
    public ResponseEntity<List<Sku>> querySkusBySkuId(@RequestParam(value = "id") Long spuId){
        List<Sku> skus = this.goodsService.querySkusBySkuId(spuId);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    @PutMapping(value = "goods")
    public ResponseEntity<Void> UpdateGoods(SpuBo spuBo){
        this.goodsService.UpdateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
    }

    @GetMapping(value = "{id}")
    public  ResponseEntity<Spu> querySpuById(@PathVariable(value = "id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if(spu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

}
