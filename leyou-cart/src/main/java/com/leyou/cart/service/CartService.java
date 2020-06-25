package com.leyou.cart.service;

import auth.entity.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final String KEY_PREFIX = "user:cart";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsClient goodsClient;


    @Transactional(rollbackFor = Exception.class)
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断当前的商品是否在购物车当中
        if (hashOperations.hasKey(key)) {
            //在更新数量
            String cartJson = hashOperations.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);

        } else {
            //不再新增购物车
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(cart.getUserId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setImage(StringUtils.isBlank(
                    sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
        }
        hashOperations.put(key, JsonUtils.serialize(cart));
    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //获取用户的购物车信息
        //判断用户是否有购物车记录
        if (stringRedisTemplate.hasKey(KEY_PREFIX + userInfo.getId())) {
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        List<Object> cartJsons = hashOperations.values();
        if (CollectionUtils.isEmpty(cartJsons)) {
            return null;
        }
        return cartJsons.stream().map(cartJson -> {
            return JsonUtils.parse(cartJson.toString(), Cart.class);
        }).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if (!this.stringRedisTemplate.hasKey(KEY_PREFIX + userInfo.getId())) {
            return;
        }
        Integer num = cart.getNum();
        BoundHashOperations<String, Object, Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo);
        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();

        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(cart.getNum() + num);

        hashOperations.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCartBySkudId(String skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        BoundHashOperations<String, Object, Object> hashOperations = this.stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        hashOperations.delete(skuId);
    }
}
