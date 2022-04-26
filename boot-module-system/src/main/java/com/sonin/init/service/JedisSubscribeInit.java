package com.sonin.init.service;

import com.sonin.core.context.SpringContext;
import com.sonin.jedis.service.JedisSubscribeService;
import com.sonin.jedis.template.JedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * <pre>
 * Jedis订阅
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 11:07
 */
@Slf4j
@Component
public class JedisSubscribeInit {

    @Autowired
    private JedisTemplate jedisTemplate;

    public void subscribe(String channel) {
        try {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    super.onMessage(channel, message);
                    log.info("jedis订阅成功 => channel: {}, message: {}", channel, message);
                    JedisSubscribeService jedisSubscribeService = (JedisSubscribeService) SpringContext.getBean(channel);
                    jedisSubscribeService.handle(message);
                }
            };
            jedisTemplate.subscribe(jedisPubSub, channel);
        } catch (Exception e) {
            log.error("jedis subscribe error: {}", e.getMessage());
        }
    }

}
