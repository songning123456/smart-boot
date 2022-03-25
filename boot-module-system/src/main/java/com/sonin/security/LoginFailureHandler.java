package com.sonin.security;

import cn.hutool.json.JSONUtil;
import com.sonin.api.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		ServletOutputStream outputStream = response.getOutputStream();
		Result result = Result.error(exception.getMessage());
		result.setCode(400);
		outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
		outputStream.flush();
		outputStream.close();
	}

}
