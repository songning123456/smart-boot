package com.sonin.config.datasource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.sonin.core.context.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author sonin
 * @date 2021/12/6 14:58
 */
@Lazy(false)
public class JdbcTemplateConfig {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplateConfig.class);

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    public JdbcTemplateConfig() {
    }

    @Bean(name = {"initJdbcTemplate"})
    public void initJdbcTemplate() {
        log.info(">>> JdbcTemplate实例化开始 <<<");
        Map<String, DataSource> dataSourceMap = this.dynamicRoutingDataSource.getCurrentDataSources();
        for (String key : dataSourceMap.keySet()) {
            SpringContext.setJdbcTemplateBean(key, JdbcTemplate.class, this.dynamicRoutingDataSource.getDataSource(key));
            log.info(">>> " + key + " JdbcTemplateBean实例化完成 <<<");
        }
        log.info(">>> JdbcTemplateBean实例化结束 <<<");
    }

}
