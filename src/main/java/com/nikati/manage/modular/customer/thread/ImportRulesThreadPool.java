package com.nikati.manage.modular.customer.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:22
 * @version：V1.0
 */
@Configuration
public class ImportRulesThreadPool {

    /**
     * 创建线程池处理任务
     * @return
     */
    @Bean
    public ExecutorService xuiExecutor() {

        return new ThreadPoolExecutor(
                20,
                100,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}