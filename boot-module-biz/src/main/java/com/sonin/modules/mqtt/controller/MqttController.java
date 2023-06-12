package com.sonin.modules.mqtt.controller;

import com.alibaba.fastjson.JSONObject;
import com.sonin.modules.config.MqttConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <pre>
 * mqtt发布订阅ctrl
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Value("${biz.mqtt.topic:}")
    private String mqttTopic;
    @Resource
    private MqttConfig mqttConfig;

    @GetMapping("/publishStr")
    public void publishStrCtrl(String dataStr) {
        mqttConfig.publish(mqttTopic, dataStr);
    }

    @PostMapping("/publish")
    public void publishCtrl(Map<String, Object> paramMap) {
        mqttConfig.publish(mqttTopic, JSONObject.toJSONString(paramMap));
    }

}
