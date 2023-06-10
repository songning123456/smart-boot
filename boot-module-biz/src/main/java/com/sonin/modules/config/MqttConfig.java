package com.sonin.modules.config;

import com.sonin.modules.mqtt.callback.OnMessageCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <pre>
 * MQTT配置类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@Component
@Configuration
public class MqttConfig {

    @Value("${biz.mqtt.username:}")
    private String username;

    @Value("${biz.mqtt.password:}")
    private String password;

    @Value("${biz.mqtt.url:}")
    private String hostUrl;

    @Value("${biz.mqtt.clientId:}")
    private String clientId;

    @Value("${biz.mqtt.timeout:}")
    private Integer timeout;

    @Value("${biz.mqtt.keepAlive:}")
    private Integer keepAlive;

    @Value("${biz.mqtt.qos:}")
    private Integer qos;

    @Value("${biz.mqtt.defaultTopic:}")
    private String defaultTopic;

    private MqttClient client;

    /**
     * 项目启动时自动连接 MQTT
     */
    @PostConstruct
    public void init() {
        connect();
    }

    /**
     * 连接 MQTT
     */
    public void connect() {
        try {
            client = new MqttClient(hostUrl, clientId, new MemoryPersistence());
            // MQTT连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);
            // 设置超时时间，单位秒
            connOpts.setConnectionTimeout(timeout);
            // 设置心跳时间，单位秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            connOpts.setKeepAliveInterval(keepAlive);
            // 设置回调
            client.setCallback(new OnMessageCallback());
            // 建立连接
            client.connect(connOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅
     *
     * @param topic 主题
     */
    public void subscribe(String topic) {
        try {
            client.subscribe(topic, qos);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    /**
     * 消息发布
     *
     * @param topic 主题
     * @param data  消息
     */
    public void publish(String topic, String data) {
        try {
            MqttMessage message = new MqttMessage(data.getBytes());
            // 消息服务质量等级
            message.setQos(qos);
            // 保留消息
            message.setRetained(true);
            client.publish(topic, message);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            client.disconnect();
            client.close();
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}