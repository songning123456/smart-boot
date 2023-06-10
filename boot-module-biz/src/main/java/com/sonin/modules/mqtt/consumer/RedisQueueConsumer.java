package com.sonin.modules.mqtt.consumer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.constant.BusinessConstant;
import com.sonin.modules.xsinsert.entity.Xsinsert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 消费Redis队列，入库
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@Slf4j
@Component
public class RedisQueueConsumer implements Runnable {

    @Value("${biz.redisConsumerFlag:}")
    private String redisConsumerFlag;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IBaseService baseService;

    @Override
    public void run() {
        if ("true".equals(redisConsumerFlag)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Object object = redisTemplate.opsForList().rightPop(BusinessConstant.QUEUE_NAME);
                    if (object == null) {
                        // 睡眠时间
                        TimeUnit.SECONDS.sleep(3);
                        continue;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(String.valueOf(object));
                    String day = jsonObject.getString("day");
                    JSONArray dataJSONArray =jsonObject.getJSONArray("data");
                    List<Xsinsert> dataList = JSONArray.parseArray(dataJSONArray.toJSONString(), Xsinsert.class);
                    baseService.saveBatch(BusinessConstant.BASE_TABLE + day, dataList);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
