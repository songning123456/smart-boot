package com.sonin.modules.sse.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sonin.modules.sse.service.ISSEService;
import com.sonin.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/10/13 14:17
 */
@Slf4j
@Service("baoxie")
public class BaoxieSSEServiceImpl implements ISSEService {

    @Override
    public HttpURLConnection beforeCreateConnection(Map<String, Object> paramsMap) {
        // 请求参数
        String applicationId = ConvertUtils.getString(paramsMap.getOrDefault("applicationId", "4688778e-691b-11f0-b784-0242ac110002"));
        String authorization = ConvertUtils.getString(paramsMap.getOrDefault("authorization", "application-b920429f791c3f7a3db658797dde47dd"));
        // 获取chatId
        String resStr0 = HttpUtil.createGet("http://shukuang.cn:9080/api/application/" + applicationId + "/chat/open")
                .header("AUTHORIZATION", authorization)
                .execute()
                .body();
        String chatId = JSONUtil.parseObj(resStr0).getStr("data");
        // 3. 发送请求并处理流式响应
        String chatUrl = "http://shukuang.cn:9080/api/application/chat_message/" + chatId;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(chatUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("AUTHORIZATION", authorization);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public String combineParam(Map<String, Object> paramsMap) {
        paramsMap.remove("applicationId");
        paramsMap.remove("authorization");
        return JSONUtil.toJsonStr(paramsMap);
    }

}
