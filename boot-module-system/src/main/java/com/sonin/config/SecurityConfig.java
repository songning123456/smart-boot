package com.sonin.config;

import com.sonin.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    CaptchaFilter captchaFilter;
    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    UserDetailServiceImpl userDetailService;
    @Autowired
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] HTTP_WHITELIST = {
            "/login",
            "/logout",
    };

    private static final String[] WEB_WHITELIST = {
            "/auth/captcha",
            "/websocket/app",
            "/file/excel/import", // todo 待删除
    };


    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                // 关闭csrf
                .and()
                .csrf()
                .disable()
                // 登录配置
                .formLogin()
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                // 退出配置
                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                // 禁用session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 配置拦截规则
                .and()
                .authorizeRequests()
                .antMatchers(HTTP_WHITELIST)
                .permitAll()
                .anyRequest()
                .authenticated()
                // 异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // 配置自定义的过滤器
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers(WEB_WHITELIST);
    }

}
