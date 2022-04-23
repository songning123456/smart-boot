package com.sonin.websocket.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * Websocket具体处理逻辑
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/23 11:07
 */
public interface IWebsocketService {

    void handle(JSONObject jsonObject);

}
