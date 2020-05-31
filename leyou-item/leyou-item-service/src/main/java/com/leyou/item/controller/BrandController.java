package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据条件分页查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "key", defaultValue = "1") Integer page,
            @RequestParam(value = "key", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String sortBy,
            @RequestParam(value = "key", required = false) Boolean desc) {
        PageResult<Brand> result = this.brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if (result == null || CollectionUtils.isEmpty(result.getItem())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cid") List<Long> cid) {
        this.brandService.saveBrand(brand, cid);
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
    }


    /**
     * 根据分类Id查询品牌列表
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable(value = "cid") Long cid) {
        List<Brand> brandList = this.brandService.queryBrandsByCid(cid);
        if (CollectionUtils.isEmpty(brandList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brandList);
    }
}
