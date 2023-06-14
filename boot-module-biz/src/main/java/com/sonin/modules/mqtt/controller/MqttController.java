package com.sonin.modules.mqtt.controller;

import com.alibaba.fastjson.JSONObject;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.modules.config.MqttConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    /**
     * <pre>
     * 下发指令
     * </pre>
     *
     * @param topic
     * @param tag
     * @param value
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @GetMapping("/publishOrder")
    @CustomExceptionAnno(description = "下发指令")
    public void publishOrderCtrl(String topic, String tag, String value) {
        String topicName = topic + "/_dnbody";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tag", tag);
        jsonObject.put("value", value);
        mqttConfig.publish(topicName, jsonObject.toJSONString());
    }

}
