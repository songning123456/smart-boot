package com.sonin.modules.thirddata.service;

import java.util.Map;

/**
 * @Author：sonin
 * @Date：2025/5/16 13:54
 */
public interface IThirdDataService {

    void handleRealtimeDataFunc(Map<String, Object> paramMap);

    void handleCountDataFunc(Map<String, Object> paramMap) throws Exception;

    void handleCommonCountDataFunc(Map<String, Object> paramMap) throws Exception;

}
