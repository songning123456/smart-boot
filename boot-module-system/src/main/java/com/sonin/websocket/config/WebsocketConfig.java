package com.sonin.websocket.config;

import com.sonin.websocket.handler.AppWebSocketHandler;
import com.sonin.websocket.interceptor.AppHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * <pre>
 * websocket基础配置
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/18 17:34
 */
public class WebsocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Autowired
    private AppWebSocketHandler appWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(appWebSocketHandler, "/websocket/app").addInterceptors(new AppHandshakeInterceptor()).setAllowedOrigins("*");
    }

}
