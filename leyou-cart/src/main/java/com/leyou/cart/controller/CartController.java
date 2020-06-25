package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private  CartService cartService;

    @PostMapping
    public ResponseEntity<Void>  addCart(Cart cart){

        this.cartService.addCart(cart);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Cart>> queryCarts(){
        List<Cart> cartList = this.cartService.queryCarts();
        if(CollectionUtils.isEmpty(cartList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartList);
    }

    @PutMapping(value = "update")
    public  ResponseEntity<Void> updateNum(Cart cart){
        this.cartService.updateNum(cart);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "delete/{skuId}")
    public ResponseEntity<Void> deleteCartBySkudId(@PathVariable(value = "skuId")String skuId){
        this.cartService.deleteCartBySkudId(skuId);
        return ResponseEntity.noContent().build();
    }
}
