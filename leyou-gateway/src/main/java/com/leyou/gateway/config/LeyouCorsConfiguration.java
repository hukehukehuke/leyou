package com.leyou.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){
        //初始化cors配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许跨域的域名 如果要携带cookie 不允许写*  *代表所有域名
        corsConfiguration.addAllowedOrigin("www.leyou.com");
        corsConfiguration.setAllowCredentials(true); //是否允许携带头信息
        corsConfiguration.addAllowedHeader("*"); //允许携带任何信息
        corsConfiguration.addAllowedMethod("*"); //允许所有的请求


        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        //返回CorsFilter实例、参数】Cors配置对象
        CorsFilter corsFilter = new CorsFilter(corsConfigurationSource);
        return corsFilter;
    }
}
