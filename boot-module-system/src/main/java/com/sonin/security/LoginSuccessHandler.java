package com.sonin.security;

import cn.hutool.json.JSONUtil;
import com.sonin.api.vo.Result;
import com.sonin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        // 生成jwt，并放置到请求头中
        String jwt = jwtUtil.generateToken(authentication.getName());
        response.setHeader(jwtUtil.getHeader(), jwt);
        outputStream.write(JSONUtil.toJsonStr(Result.ok()).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

}
