package com.sonin.modules.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
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
        long workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr()) % 32;
        long dataCenterId = 1;
        return new Snowflake(workerId, dataCenterId) {
            private long lastTimestamp = -1L;
            // 最大重试次数
            private static final int MAX_RETRIES = 10;
            // 每次等待的时间（毫秒）
            private static final long SLEEP_TIME = 1L;

            @Override
            public long nextId() {
                int retries = 0;
                while (true) {
                    long timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        // 发生时钟回拨
                        if (retries >= MAX_RETRIES) {
                            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
                        }
                        try {
                            // 等待一段时间
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        retries++;
                        continue;
                    }
                    lastTimestamp = timestamp;
                    return super.nextId();
                }
            }

            /**
             * 获取当前时间戳
             * @return 当前时间戳
             */
            private long timeGen() {
                return System.currentTimeMillis();
            }
        };
    }

}
