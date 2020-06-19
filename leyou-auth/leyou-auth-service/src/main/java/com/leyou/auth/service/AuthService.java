package com.leyou.auth.service;

import auth.entity.UserInfo;
import auth.utils.JwtUtils;
import com.leyou.auth.config.JwtProperties;
import com.leyou.client.UserClient;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;
    public String accredit(String username, String password) {
        //根据用户名和密码查询
        User user = this.userClient.queryUser(username, password);
        //判断uer
        if(user == null){
            return null;
        }
        //jwtUtils生成jwt类型的token
      try {
          UserInfo userInfo = new UserInfo();
          userInfo.setId(user.getId());
          userInfo.setUsername(user.getUsername());
          return JwtUtils.generateToken(userInfo,this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire()*60);
      }catch (Exception e){
          e.printStackTrace();
          return null;
      }
    }
}
