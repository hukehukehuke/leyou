package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Times;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user.verify";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Boolean checkUser(String data, Integer type) {
        User user = new User();
//        if(type == 1){
//            user.setPhone(data);
//        }else if(type == 2){
//            user.setPassword(data);
//        }else{
//            return null;
//        }
        switch (type) {
            case 1:
                user.setPassword(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //生成验证码
        String code = NumberUtils.generateCode(6);
        //发送消息到rabbitMQ
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        this.amqpTemplate.convertAndSend("leyou.sms.exchange", "verifycode.sms", msg);

        //把验证码保存到redis中
        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {

        //查询Redis中的验证码
        String redisCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        //校验验证码
        if (StringUtils.equals(code, redisCode)) {
            return;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insert(user);
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断用户是否为空
        if (user == null) {
            return null;
        }
        //获取盐 对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, user.getSalt());
        if (StringUtils.equals(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
