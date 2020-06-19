package com.leyou.auth.controller;

import auth.entity.UserInfo;
import auth.utils.JwtUtils;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = this.authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getExpire() * 60);
        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue(value = "LY_TOKEN") String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            //用jwt工具类通过公钥解析jwt
            UserInfo user = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //刷新jwt中的有效时间
            token = JwtUtils.generateToken(user, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
            //刷新token的时间
            CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), token, this.jwtProperties.getExpire());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
