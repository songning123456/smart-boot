package com.sonin.jedis.service;

/**
 * <pre>
 * Jedis订阅处理消息
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 13:21
 */
public interface JedisSubscribeService {

    void handle(String message);

}
