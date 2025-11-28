package com.sonin.modules.data.service;

import java.util.List;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/11/28 10:21
 */
public interface IDataBusinessService {

    String handleRealtimeDataFunc(List<Map<String, Object>> dataMapList);

    void handleAggDataFunc(Map<String, Object> paramsMap);

}
