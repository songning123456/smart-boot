package com.sonin.modules.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：sonin
 * @Date：2025/4/2 9:42
 */
@Configuration
public class SnowflakeConfig {

    @Bean
    public Snowflake snowflake() {
        // 工作机器ID 和 数据中心ID，范围都是0-31
        long workerId = 1;
        long dataCenterId = 1;
        return new Snowflake(workerId, dataCenterId);
    }

}
