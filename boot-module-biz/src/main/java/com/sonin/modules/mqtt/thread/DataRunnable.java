package com.sonin.modules.mqtt.thread;

import com.alibaba.fastjson.JSON;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * <pre>
 * 处理mqtt数据并发送给redis
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@Slf4j
public class DataRunnable implements Runnable {

    private List<String> dataList;

    public DataRunnable(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void run() {
        // todo 开始处理mqtt数据
        this.dataList.add("test" + System.currentTimeMillis());
        RedisTemplate redisTemplate = (RedisTemplate) SpringContext.getBean("redisTemplate");
        // 存入redis队列，待消费
        redisTemplate.opsForList().leftPush(BusinessConstant.QUEUE_NAME, JSON.toJSONString(this.dataList));
    }

}
