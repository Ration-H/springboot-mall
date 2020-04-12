package com.gdou.mall;

import com.gdou.mall.utils.FastJSONUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.gdou.mall.manage.mapper")
public class MallManageServiceApplication {

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        return new HttpMessageConverters(FastJSONUtils.getConverter());
    }

    public static void main(String[] args) {
        SpringApplication.run(MallManageServiceApplication.class, args);
    }

}
