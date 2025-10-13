package com.sonin.modules.sse.service;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/10/13 14:15
 */
public interface ISSEService {

    HttpURLConnection beforeCreateConnection(Map<String, Object> paramsMap);

    String combineParam(Map<String, Object> paramsMap);

}
