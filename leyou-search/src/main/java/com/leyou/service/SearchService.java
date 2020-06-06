package com.leyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsClient goodsClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        Goods goods = new Goods();
        //根据分类的id查询分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid2(), spu.getCid1(), spu.getCid3()));

        //根据品牌Id查询品牌
        Brand brand = this.brandClient.queryBrandByid(spu.getBrandId());

        //根据spuId查询所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        //初始化一个价格集合，搜集所有sku的价格
        List<Long> prices = new ArrayList<>();
        //搜集sku的必要字段信息
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("price",sku.getPrice());
            map.put("title",sku.getTitle());
            map.put("image",StringUtils.isBlank(sku.getImages()) ? "" :StringUtils.split(sku.getImages(),","));
            skuMapList.add(map);
        });
        //根据spu中的cid3查询出所有的规格参数
        List<SpecParam> queryparams = this.specificationClient.queryparams(null, spu.getCid1(), null, true);
        //根据spuId查询spuDeatail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的参数值反序列化
        Map<String, Object> genObjectMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>(){});
        //把特殊的参数值反序列化
        Map<String, List<Object>> specObjectMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>(){});

        Map<String,Object> specs = new HashMap<>();

        queryparams.forEach(queryparam ->{
            //判断规格参数是否通用
            if(queryparam.getGeneric()){
                String value = genObjectMap.get(queryparam.getId().toString()).toString();
                if(queryparam.getNumeric()){ //判断是否是数值类型   数值类型返回一个区间
                    value= "一个区间";
                }
                specs.put(queryparam.getName(),value);
            }else{
                List<Object> value = specObjectMap.get(queryparam.getId().toString());
                specs.put(queryparam.getName(),value);
            }
        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段儿需要分类名称以及品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ") +" "+brand.getName());
        //获取spu下的所有spu价格
        goods.setPrice(prices);
        //获取spu下的所有sku价格并转换成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数
        goods.setSpecs(null);
        return goods;
    }
}
