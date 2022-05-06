package com.sonin.modules.logback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 存储sys_log日志
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/5 17:48
 */
@Configuration
public class LogbackTaskConfig {

    @Bean
    public void logbackTaskExecute() {
        Thread thread = new Thread(new LogbackConsumer());
        thread.start();
    }

}
