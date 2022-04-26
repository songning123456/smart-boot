package com.sonin.init;

import com.sonin.init.service.JedisSubscribeInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 项目启动初始化
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 10:44
 */
@Slf4j
@Component
public class BootApplicationRunner implements ApplicationRunner {

    @Autowired
    private JedisSubscribeInit jedisSubscribeInit;

    @Override
    public void run(ApplicationArguments args) {
        // 初始化时jedis订阅websocket主题
        log.info("~初始化时jedis订阅websocket主题~");
        jedisSubscribeInit.subscribe("websocket");
    }

}
