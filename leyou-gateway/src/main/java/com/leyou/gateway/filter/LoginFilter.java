package com.leyou.gateway.filter;

import auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties(value = {JwtProperties.class,FilterProperties.class})

public class LoginFilter extends ZuulFilter {

    @Autowired
    private  JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        //获取到白名单路径
        List<String> allowPaths = filterProperties.getAllowPaths();
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String url = request.getRequestURL().toString();
        for(String  alllowPath : allowPaths){
            if(StringUtils.contains(url,alllowPath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //初始化运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if(StringUtils.isBlank(token)){
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        try {
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
        }catch (Exception e){
            e.printStackTrace();
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
