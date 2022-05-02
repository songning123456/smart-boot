package com.sonin.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sonin.exception.CaptchaException;
import com.sonin.jedis.template.JedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private JedisTemplate jedisTemplate;
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();
        if ("/boot/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {
            try {
                // 校验验证码
                validate(httpServletRequest);
            } catch (CaptchaException e) {
                // 交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // 校验验证码逻辑
    private void validate(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter("code");
        String token = httpServletRequest.getParameter("token");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(token) || !code.equals(jedisTemplate.hget("captcha", token))) {
            throw new CaptchaException("验证码错误");
        }
        jedisTemplate.hdel("captcha", token);
    }

}
