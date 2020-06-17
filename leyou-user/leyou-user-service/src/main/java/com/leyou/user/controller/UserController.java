package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable(value = "data") String data, @PathVariable(value = "type") Integer type) {
        Boolean b = this.userService.checkUser(data, type);
        if (b == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(b);
    }
    @PostMapping(value = "code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam(value = "phone")String phone){
        this.userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).build();
    }

    @PostMapping(value = "register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam(value = "code") String code){
        this.userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping(value = "query")
    public  ResponseEntity<User> queryUser(@RequestParam(value = "username")String username,
                                           @RequestParam(value = "password")String password){
        User user = this.userService.queryUser(username,password);
        if(user == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}
