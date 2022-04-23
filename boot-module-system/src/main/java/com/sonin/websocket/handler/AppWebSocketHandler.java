package com.sonin.websocket.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sonin.utils.CustomApplicationContext;
import com.sonin.websocket.service.IWebsocketConstant;
import com.sonin.websocket.service.IWebsocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/18 17:36
 */
@Slf4j
@Component
public class AppWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        log.info("webSocket打开: {}", webSocketSession.getId());
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        try {
            String payloadStr = webSocketMessage.getPayload().toString();
            // 如果前端发送ping, 后端响应pong
            if (IWebsocketConstant.PING.equals(payloadStr)) {
                webSocketSession.sendMessage(new TextMessage(IWebsocketConstant.PONG));
            } else {
                // 解析JSON字符串
                JSONObject jsonObject = JSON.parseObject(payloadStr);
                // 存储 uuid => session
                if (!StrUtil.isEmpty(jsonObject.getString("uuid"))) {
                    IWebsocketConstant.uuid2SessionMap.putIfAbsent(jsonObject.getString("uuid"), webSocketSession);
                }
                // component: 具体的业务处理逻辑
                if (!StrUtil.isEmpty(jsonObject.getString("component"))) {
                    IWebsocketService websocketService = (IWebsocketService) CustomApplicationContext.getBean(jsonObject.getString("component"));
                    websocketService.handle(jsonObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket handleMessage error: {}", e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) throws Exception {
        this.removeUUID(webSocketSession);
        this.remove();
        log.error("websocket handleTransportError error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        this.removeUUID(webSocketSession);
        this.remove();
        log.error("websocket afterConnectionClosed error: {}", closeStatus.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * <pre>
     * 每次发生连接异常或连接关闭时，删除断开连接的UUID
     * </pre>
     *
     * @param webSocketSession
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void removeUUID(WebSocketSession webSocketSession) {
        String uuid = "";
        for (Map.Entry<String, WebSocketSession> item : IWebsocketConstant.uuid2SessionMap.entrySet()) {
            if (webSocketSession.getId().equals(item.getValue().getId())) {
                uuid = item.getKey();
                break;
            }
        }
        if (!"".equals(uuid)) {
            IWebsocketConstant.uuid2SessionMap.remove(uuid);
        }
    }

    /**
     * <pre>
     * 每次发生连接异常或连接关闭时，检查所有连接情况，删除未连接的缓存
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void remove() {
        List<String> uuidList = new ArrayList<>();
        for (Map.Entry<String, WebSocketSession> item : IWebsocketConstant.uuid2SessionMap.entrySet()) {
            if (!item.getValue().isOpen()) {
                uuidList.add(item.getKey());
            }
        }
        uuidList.forEach(IWebsocketConstant.uuid2SessionMap::remove);
    }

}
