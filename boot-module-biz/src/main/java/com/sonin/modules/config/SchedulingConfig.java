package com.sonin.modules.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/12 10:29
 */
@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "biz.scheduled", name = "enable", havingValue = "true")
public class SchedulingConfig {
}
