package com.sonin.modules.sse.controller;

import cn.hutool.json.JSONUtil;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.context.SpringContext;
import com.sonin.core.vo.Result;
import com.sonin.modules.sse.constant.SSEConstant;
import com.sonin.modules.sse.service.ISSEService;
import com.sonin.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/10/13 13:59
 */
@Slf4j
@RestController
@RequestMapping("/sse")
public class SSEController {

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CustomExceptionAnno(description = "SSE通用请求接口")
    public void streamCtrl(@RequestBody Map<String, Object> paramsMap, HttpServletResponse response) {
        // 设置响应属性
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
        try {
            // 获取service实现类
            String beanName = ConvertUtils.getString(paramsMap.get("beanName"));
            ISSEService isseService = (ISSEService) SpringContext.getBean(beanName);
            paramsMap.remove("beanName");
            // 建立连接前置操作
            HttpURLConnection connection = isseService.beforeCreateConnection(new HashMap<>(paramsMap));
            // 组装请求参数
            String requestParamStr = isseService.combineParam(new HashMap<>(paramsMap));
            // 设置连接条件
            if (StringUtils.isEmpty(connection.getRequestMethod())) {
                connection.setRequestMethod("POST");
            }
            if (StringUtils.isEmpty(connection.getRequestProperty("Content-Type"))) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            }
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            // 流式响应不设置读取超时
            connection.setReadTimeout(0);
            // 发送请求体
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestParamStr.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            // 处理响应流
            try (InputStream inputStream = connection.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); OutputStream outputStream = response.getOutputStream()) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    try {
                        // 过滤空行
                        if (StringUtils.isEmpty(line.replaceAll(SSEConstant.SSE_DATA_PREFIX, "").replaceAll(SSEConstant.SSE_LINE_SEPARATOR, ""))) {
                            continue;
                        }
                        // 发送SSE格式数据
                        // 判断line是否已包含data: 前缀
                        String sseDataStr;
                        if (line.trim().startsWith(SSEConstant.SSE_DATA_PREFIX)) {
                            // 已包含data:，直接添加结尾分隔符
                            sseDataStr = line + "\n\n";
                        } else {
                            // 未包含data:，手动添加前缀和结尾分隔符
                            sseDataStr = SSEConstant.SSE_DATA_PREFIX + line + SSEConstant.SSE_LINE_SEPARATOR;
                        }
                        outputStream.write(sseDataStr.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (IOException e) {
                        break;
                    }
                    // 检查线程中断
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "模型请求失败: " + e.getMessage());
        }
    }

    /**
     * 发送错误响应
     *
     * @param response
     * @param message
     */
    private void sendErrorResponse(HttpServletResponse response, String message) {
        try (OutputStream os = response.getOutputStream()) {
            String errorData = SSEConstant.SSE_DATA_PREFIX + JSONUtil.toJsonStr(Result.error(message)) + SSEConstant.SSE_LINE_SEPARATOR;
            os.write(errorData.getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException e) {
            // 静默处理
        }
    }

}
