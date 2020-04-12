package com.gdou.mall.item;

import com.gdou.mall.utils.FastJSONUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MallItemWebApplication {
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        return new HttpMessageConverters(FastJSONUtils.getConverter());
    }

    public static void main(String[] args) {
        SpringApplication.run(MallItemWebApplication.class, args);
    }

}
