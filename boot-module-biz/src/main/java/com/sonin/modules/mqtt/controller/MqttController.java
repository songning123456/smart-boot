package com.sonin.modules.mqtt.controller;

import com.sonin.modules.config.MqttConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {
    @Resource
    private MqttConfig mqttConfig;

    @GetMapping("/send/{message}")
    public void sendMessageCtrl(@PathVariable("message") String message) {
        String subTopic = "testtopic/#";
        String pubTopic = "testtopic/1";
        String topicTest="zmtest";
        String data = "hello MQTT test";
        mqttConfig.publish(topicTest, message);

      // 订阅
//        mqttConfig.subscribe(subTopic);
      // 发布消息
//        mqttConfig.publish(pubTopic, data);
        // 断开连接
        // mqttConfig.disconnect();
    }

    @GetMapping("/receive")
    public void receiveMessageCtrl() {
        String subTopic = "testtopic/#";
        String pubTopic = "testtopic/1";
        String topicTest="zmtest";
        String data = "hello MQTT test";
//        mqttConfig.publish(topicTest, data);

//        // 订阅
        mqttConfig.subscribe(topicTest);
//        // 发布消息
//        mqttConfig.publish(pubTopic, data);
        // 断开连接
        // mqttConfig.disconnect();
    }
}
