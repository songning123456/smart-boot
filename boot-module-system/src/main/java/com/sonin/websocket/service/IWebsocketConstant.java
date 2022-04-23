package com.sonin.websocket.service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 心跳
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/23 10:55
 */
public interface IWebsocketConstant {

    String PING = "ping";

    String PONG = "pong";

    // uuid命名规则: username:uuid
    Map<String, WebSocketSession> uuid2SessionMap = new ConcurrentHashMap<>();

}
