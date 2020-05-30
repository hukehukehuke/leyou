package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;
    /**
     * 增加商品规格组
     *
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup) {
        specificationService.saveSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除商品规格组
     *
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Long id) {
        specificationService.deleteSpecGroup(id);
        return ResponseEntity.ok().build();

    }

    /**
     * 更新商品规格组
     *
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup) {
        specificationService.updateSpecGroup(specGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * 增加商品规格参数
     *
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam) {
        specificationService.saveSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除商品规格参数
     *
     * @param id
     * @return
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id") Long id) {
        specificationService.deleteSpecParam(id);
        return ResponseEntity.ok().build();

    }

    /**
     * 更新商品规格参数
     *
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam) {
        specificationService.updateSpecParam(specParam);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据分类id查询参数组
     */
    @GetMapping(value = "group/{cid}")
    public ResponseEntity<List<SpecGroup>>  querySpecGroupByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = this.specificationService.querySpecGroupByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @GetMapping(value = "params")
    public ResponseEntity<List<SpecParam>>  queryparams(@RequestParam(value = "gid") Long gid){
        List<SpecParam> list = this.specificationService.queryparams(gid);
        if(CollectionUtils.isEmpty(list)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }
}
