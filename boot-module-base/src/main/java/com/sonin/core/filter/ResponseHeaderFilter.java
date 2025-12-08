package com.sonin.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * 	响应头缺失漏洞过滤器
 * </pre>
 *
 * @author Li Yuanyuan
 * @version V0.1, 2021年3月8日 下午2:06:01
 */
@Slf4j
@Component
@WebFilter(filterName = "responseHeaderFilter", urlPatterns = "/*")
public class ResponseHeaderFilter implements Filter {
    /**
     * 拦截
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /**
         * 处理漏洞扫描中的响应头确实漏洞
         */
        //这个响应头主要是用来定义页面可以加载哪些资源，减少XSS的发生
        response.setHeader("Content-Security-Policy", "script-src * 'unsafe-inline' 'unsafe-eval'");
        //互联网上的资源有各种类型，通常浏览器会根据响应头的Content-Type字段来分辨它们的类型。通过这个响应头可以禁用浏览器的类型猜测行为
        response.setHeader("X-Content-Type-Options", "nosniff");
        //1; mode=block：启用XSS保护，并在检查到XSS攻击时，停止渲染页面
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        response.setHeader("X-Download-Options", "noopen");
        response.setHeader("Strict-Transport-Security", "max-age=15552000");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}