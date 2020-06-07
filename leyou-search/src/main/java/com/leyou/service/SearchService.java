package com.leyou.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.bo.SearchRequest;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import com.leyou.repostory.GoodsRepository;
import com.leyou.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private GoodsRepository goodsRepository;

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
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("price", sku.getPrice());
            map.put("title", sku.getTitle());
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ","));
            skuMapList.add(map);
        });
        //根据spu中的cid3查询出所有的规格参数
        List<SpecParam> queryparams = this.specificationClient.queryparams(null, spu.getCid1(), null, true);
        //根据spuId查询spuDeatail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的参数值反序列化
        Map<String, Object> genObjectMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //把特殊的参数值反序列化
        Map<String, List<Object>> specObjectMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String, Object> specs = new HashMap<>();

        queryparams.forEach(queryparam -> {
            //判断规格参数是否通用
            if (queryparam.getGeneric()) {
                String value = genObjectMap.get(queryparam.getId().toString()).toString();
                if (queryparam.getNumeric()) { //判断是否是数值类型   数值类型返回一个区间
                    value = "一个区间";
                }
                specs.put(queryparam.getName(), value);
            } else {
                List<Object> value = specObjectMap.get(queryparam.getId().toString());
                specs.put(queryparam.getName(), value);
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
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " ") + " " + brand.getName());
        //获取spu下的所有spu价格
        goods.setPrice(prices);
        //获取spu下的所有sku价格并转换成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数
        goods.setSpecs(null);
        return goods;
    }

    public SearchResult search(SearchRequest searchRequest) {
        if (StringUtils.isBlank(searchRequest.getKey())) {
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);
        queryBuilder.withQuery(basicQuery);
        //添加分页,分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage() - 1, searchRequest.getDefaultSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "sku", "skuTitle"}, null));

        //提娜姬分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";

        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        //执行查询
        AggregatedPage<Goods> search = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //获取聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(search.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(search.getAggregation(brandAggName));
        //判断是否是一个分类，只有一个分类时才做规格参数聚合
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            //对规格参数聚合
            List<Map<String, Object>> specs = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }
        return new SearchResult(search.getTotalElements(), search.getTotalPages(), search.getContent(), categories, brands, null);
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryparams(null, cid, null, true);
        params.forEach(param ->{
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+"keyword"));
        });
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> goodPage = (AggregatedPage)this.goodsRepository.search(queryBuilder.build());

        //解析聚合结果集
        List<Map<String,Object>> specs = null;
        Map<String, Aggregation> aggregationMap = goodPage.getAggregations().asMap();
        for(Map.Entry<String,Aggregation> entry : aggregationMap.entrySet()){
            Map<String,Object> map = new HashMap<>();
            map.put("k",entry.getKey());
            List<String> options = new ArrayList<>();
            //获取桶
            StringTerms terms = (StringTerms)entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket ->{
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms longTerms = (LongTerms) aggregation;
        //获取集合中的桶

        return longTerms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandByid(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
        //等价
//        List<Brand> brandList = new ArrayList<>();
//        longTerms.getBuckets().forEach(bucket -> {
//            Brand brand = this.brandClient.queryBrandByid(bucket.getKeyAsNumber().longValue());
//            brandList.add(brand);
//        }).collect(Collectors.toList());
//        return  brandList;
    }

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms longTerms = (LongTerms) aggregation;
        //获取桶的集合，转换成List<Map<String, Object>>
        return longTerms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            Long id = bucket.getKeyAsNumber().longValue();
            List<Category> categoryList = this.categoryClient.queryCategoryByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", categoryList.get(0));
            return map;
        }).collect(Collectors.toList());
    }
}
