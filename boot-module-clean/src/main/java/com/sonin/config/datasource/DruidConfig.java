package com.sonin.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.sonin.config.datasource.DataSourceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Druid连接池监控页面配置
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 15:58
 */
@Lazy(false)
@ConditionalOnClass({DataSourceConfig.class})
@AutoConfigureOrder(102)
public class DruidConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }

    /**
     * 配置一个druid的监控
     * 1. 配置一个druid的后台, 管理servlet
     * 2. 配置一个druid的filter
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        // 注意，请求时/druid/*
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initMap = new HashMap<>();
        // 登陆页面账户与密码
        // initMap.put(ResourceServlet.PARAM_NAME_USERNAME, "root");
        // initMap.put(ResourceServlet.PARAM_NAME_PASSWORD, "123456");
        // 监控后台允许ip
        initMap.put(ResourceServlet.PARAM_NAME_ALLOW, "");
        // 黑名单
        // initMap.put(ResourceServlet.PARAM_NAME_DENY, "192.168.0.1");
        bean.setInitParameters(initMap);
        return bean;
    }

    /**
     * <pre>
     * 配置一个druid的filter
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        Map<String, String> initMap = new HashMap<>();
        initMap.put(WebStatFilter.PARAM_NAME_EXCLUSIONS, "*.js,*.css,/druid/*");
        bean.setInitParameters(initMap);
        // 设置拦截器请求
        bean.setUrlPatterns(Collections.singletonList("/"));
        return bean;
    }

}
