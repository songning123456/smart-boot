package com.sonin.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author sonin
 * @date 2021/12/6 14:54
 */
@Lazy(false)
@ConditionalOnClass({DynamicRoutingDataSource.class})
@AutoConfigureOrder(100)
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.dynamic.druid.initial-size:}")
    private int initialSize;
    @Value("${spring.datasource.dynamic.druid.min-idle:}")
    private int minIdle;
    @Value("${spring.datasource.dynamic.druid.maxActive:}")
    private int maxActive;
    @Value("${spring.datasource.dynamic.druid.maxWait:}")
    private long maxWait;
    @Value("${spring.datasource.dynamic.druid.timeBetweenEvictionRunsMillis:}")
    private long timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.dynamic.druid.minEvictableIdleTimeMillis:}")
    private long minEvictableIdleTimeMillis;
    @Value("${spring.datasource.dynamic.druid.validationQuery:}")
    private String validationQuery;
    @Value("${spring.datasource.dynamic.druid.testWhileIdle:}")
    private boolean testWhileIdle;
    @Value("${spring.datasource.dynamic.druid.testOnBorrow:}")
    private boolean testOnBorrow;
    @Value("${spring.datasource.dynamic.druid.testOnReturn:}")
    private boolean testOnReturn;
    @Value("${spring.datasource.dynamic.druid.poolPreparedStatements:}")
    private boolean poolPreparedStatements;
    @Value("${spring.datasource.dynamic.druid.maxPoolPreparedStatementPerConnectionSize:}")
    private int maxPoolPreparedStatementPerConnectionSize;
    @Value("${spring.datasource.dynamic.druid.filters:}")
    private String filters;

    public DataSourceConfig() {
    }

    @Bean(name = {"initDataSource"})
    public void initDataSource() {
        log.info(">>> 开始加载分库动态数据源 <<<");
        DataSource masterDataSource = this.dynamicRoutingDataSource.getDataSource("master");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = masterDataSource.getConnection();
            ps = con.prepareStatement("select DISTINCT datasource, username, password, driver_class_name as driverClassName, url from sys_datasource");
            rs = ps.executeQuery();
            int databaseCount = 0;
            if (rs != null) {
                while (rs.next()) {
                    ++databaseCount;
                    String datasource = rs.getString("datasource");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String driverClassName = rs.getString("driverClassName");
                    String url = rs.getString("url");
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setInitialSize(this.initialSize);
                    druidDataSource.setMinIdle(this.minIdle);
                    druidDataSource.setMaxActive(this.maxActive);
                    druidDataSource.setMaxWait(this.maxWait);
                    druidDataSource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
                    druidDataSource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
                    druidDataSource.setValidationQuery(this.validationQuery);
                    druidDataSource.setTestWhileIdle(this.testWhileIdle);
                    druidDataSource.setTestOnBorrow(this.testOnBorrow);
                    druidDataSource.setTestOnReturn(this.testOnReturn);
                    druidDataSource.setPoolPreparedStatements(this.poolPreparedStatements);
                    druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(this.maxPoolPreparedStatementPerConnectionSize);
                    druidDataSource.setFilters(this.filters);
                    druidDataSource.setUsername(username);
                    druidDataSource.setPassword(password);
                    druidDataSource.setDriverClassName(driverClassName);
                    druidDataSource.setUrl(url);
                    this.dynamicRoutingDataSource.addDataSource(datasource, druidDataSource);
                }
                log.info(">>> 分库动态数据源全部加载完成, 共加载 " + databaseCount + " 个分库数据源 <<<");
            }
        } catch (SQLException var28) {
            var28.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException var27) {
                var27.printStackTrace();
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException var26) {
                var26.printStackTrace();
            }
            try {
                if (ps != null) {
                    con.close();
                }
            } catch (SQLException var25) {
                var25.printStackTrace();
            }
        }
    }

}
