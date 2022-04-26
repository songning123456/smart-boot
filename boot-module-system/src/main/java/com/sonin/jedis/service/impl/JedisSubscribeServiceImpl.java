package com.sonin.jedis.service.impl;

import com.sonin.jedis.service.JedisSubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class JedisSubscribeServiceImpl implements JedisSubscribeService {

    @Override
    public void handle(String message) {
        log.info("处理websocket信息: {}", message);
    }

}
