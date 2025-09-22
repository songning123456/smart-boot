package com.sonin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <pre>
 * 定时任务配置
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/12 10:29
 */
@Slf4j
@ComponentScan(basePackages = "com.sonin.modules")
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "biz.scheduled", name = "enable", havingValue = "true")
public class SchedulingConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置线程池大小
        scheduler.setPoolSize(100);
        scheduler.setThreadNamePrefix("scheduled-task-sonin-");
        scheduler.initialize();
        return scheduler;
    }

}
