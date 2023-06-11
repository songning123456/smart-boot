package com.sonin.modules.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 * 多线程执行器配置类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/8/10 13:36
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("asyncRedisExecutor")
    public Executor asyncRedisExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(500);
        threadPoolTaskExecutor.setMaxPoolSize(500);
        threadPoolTaskExecutor.setQueueCapacity(2000);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncRedis_");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        return threadPoolTaskExecutor;
    }

}
