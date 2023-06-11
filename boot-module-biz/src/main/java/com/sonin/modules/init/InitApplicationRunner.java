package com.sonin.modules.init;

import com.sonin.core.javassist.JavassistFactory;
import com.sonin.modules.config.MqttConfig;
import com.sonin.modules.mqtt.consumer.DataQueueConsumer;
import com.sonin.modules.mqtt.consumer.RedisQueueConsumer;
import com.sonin.modules.xsinsert.entity.Xsinsert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <pre>
 * 启动初始化
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 18:26
 */
@Slf4j
@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Resource
    private MqttConfig mqttConfig;
    @Value("${biz.mqtt.enable:}")
    private String mqttEnable;
    @Value("${biz.mqtt.topic:}")
    private String mqttTopic;
    @Autowired
    private RedisQueueConsumer redisQueueConsumer;
    @Autowired
    private DataQueueConsumer dataQueueConsumer;

    @Override
    public void run(ApplicationArguments args) {
        initClassFunc();
    }

    private void initClassFunc() {
        try {
            JavassistFactory.create(Xsinsert.class);
            log.info(">>> 初始化基础类 <<<");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread thread = new Thread(redisQueueConsumer);
            thread.start();
            log.info(">>> 异步启动RedisQueue <<<");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread thread = new Thread(dataQueueConsumer);
            thread.start();
            log.info(">>> 异步启动DataQueueConsumer <<<");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("true".equals(mqttEnable)) {
            try {
                mqttConfig.subscribe(mqttTopic);
                log.info(">>> 订阅mqtt主题{} <<<", mqttTopic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
