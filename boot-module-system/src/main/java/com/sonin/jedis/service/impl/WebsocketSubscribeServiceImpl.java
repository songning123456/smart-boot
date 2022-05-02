package com.sonin.jedis.service.impl;

import com.alibaba.fastjson.JSON;
import com.sonin.jedis.service.JedisSubscribeService;
import com.sonin.modules.websocket.dto.WebsocketDTO;
import com.sonin.websocket.service.IWebsocketConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Jedis订阅websocket
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 13:23
 */
@Service("websocket")
@Slf4j
public class WebsocketSubscribeServiceImpl implements JedisSubscribeService {

    @Override
    public void handle(String message) {
        log.info("开始处理websocket信息: {}", message);
        try {
            WebsocketDTO websocketDTO = JSON.parseObject(message, WebsocketDTO.class);
            String pushType = websocketDTO.getPushType();
            if ("me".equals(pushType)) {
                // one: 推送给我
                String key = websocketDTO.getUsername() + ":" + websocketDTO.getUuid() + ":" + websocketDTO.getTime();
                WebSocketSession webSocketSession = IWebsocketConstant.unique2SessionMap.get(key);
                if (webSocketSession != null && webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(message));
                }
            } else if ("some".equals(pushType)) {
                // some: 推送给指定username
                List<String> usernameList = Arrays.asList(websocketDTO.getUsername().split(","));
                for (Map.Entry<String, WebSocketSession> item : IWebsocketConstant.unique2SessionMap.entrySet()) {
                    String[] keys = item.getKey().split(":");
                    WebSocketSession webSocketSession = item.getValue();
                    if (keys.length == 3 && usernameList.contains(keys[0]) && webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(message));
                    }
                }
            } else if ("all".equals(pushType)) {
                // all: 推送给所有人
                for (WebSocketSession webSocketSession : IWebsocketConstant.unique2SessionMap.values()) {
                    if (webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(message));
                    }
                }
            }
        } catch (Exception e) {
            log.error("websocket订阅 error: {}", e.getMessage());
        }
    }

}
