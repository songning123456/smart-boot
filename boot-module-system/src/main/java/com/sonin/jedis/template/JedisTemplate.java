package com.sonin.jedis.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

/**
 * <pre>
 * Jedis工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 10:20
 */
@Slf4j
@Component
public class JedisTemplate {

    @Autowired
    private JedisPool jedisPool;

    public String select(int index) {
        String result = "";
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.select(index);
        } catch (Exception e) {
            log.error("Jedis异常", e);
        }
        return result;
    }

    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            log.error("Jedis异常", e);
            return "0";
        }
    }

    public Long hset(String key, String field, String value, int seconds) {
        long result = -1L;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.hset(key, field, value);
            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            log.error("Jedis异常", e);
        }
        return result;
    }

    public String get(String key) {
        String value = "0";
        try (Jedis jedis = jedisPool.getResource()) {
            value = jedis.get(key);
        } catch (Exception e) {
            log.error("Jedis异常", e);
        }
        return value;
    }

    public String hget(String key, String field) {
        String value = "0";
        try (Jedis jedis = jedisPool.getResource()) {
            value = jedis.hget(key, field);
        } catch (Exception e) {
            log.error("Jedis异常", e);
        }
        return value;
    }

    public Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("Jedis异常", e);
            return false;
        }
    }

    /**
     * 删除指定Key-Value
     */
    public Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        } catch (Exception e) {
            log.error("Jedis异常", e);
            return 0L;
        }
    }

    public Long hdel(String key, String... fields) {
        long result = -1L;
        try (Jedis jedis = jedisPool.getResource()) {
            result = jedis.hdel(key, fields);
        } catch (Exception e) {
            log.error("Jedis异常", e);
        }
        return result;
    }

    /**
     * 分布式锁
     *
     * @param key
     * @param value
     * @param secondsToExpire 锁的超时时间，单位：秒
     * @return 获取锁成功返回"OK"，失败返回null
     */
    public String getDistributedLock(String key, String value, int secondsToExpire) {
        String ret = "";
        try (Jedis jedis = jedisPool.getResource()) {
            ret = jedis.set(key, value, new SetParams().nx().ex(secondsToExpire));
            return ret;
        } catch (Exception e) {
            log.error("Jedis异常", e);
            return null;
        }
    }

    /**
     * 订阅频道
     *
     * @param jedisPubSub
     * @param channel
     */
    public void subscribe(JedisPubSub jedisPubSub, String channel) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(jedisPubSub, channel);
        }
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 消息
     */
    public void publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        }
    }

}
