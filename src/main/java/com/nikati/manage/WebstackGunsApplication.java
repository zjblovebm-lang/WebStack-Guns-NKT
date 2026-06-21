package com.nikati.manage;

import cn.stylefeng.roses.core.config.WebAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.cache.CacheMetricsAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 *@Title WebstackGunsApplication.java
 *@description SpringBoot方式启动类
 *@time 2019年12月22日 下午8:47:10
 *@author Nikati
 *@version 1.0
*
 */
@SpringBootApplication(exclude = {WebAutoConfiguration.class, CacheMetricsAutoConfiguration.class})
@EnableSwagger2
public class WebstackGunsApplication {

    private final static Logger logger = LoggerFactory.getLogger(WebstackGunsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WebstackGunsApplication.class, args);
        logger.info("Application is success!");
    }
}
