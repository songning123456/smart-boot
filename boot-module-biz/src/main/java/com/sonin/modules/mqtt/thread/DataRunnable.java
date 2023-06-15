package com.sonin.modules.mqtt.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.constant.BusinessConstant;
import com.sonin.modules.utils.AesUtils;
import com.sonin.modules.xsinsert.entity.Xsinsert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

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

    private List<byte[]> dataList;

    public DataRunnable(List<byte[]> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void run() {
        Map<String, List<Xsinsert>> day2dataMap = new HashMap<>();
        byte[] newByteArray;
        JSONObject jsonObject;
        Xsinsert xsinsert;
        String day;
        String createTime = String.valueOf(new Date().getTime() / 1000);
        for (byte[] byteArray : dataList) {
            try {
                newByteArray = AesUtils.decode(Arrays.copyOfRange(byteArray, 7, byteArray.length), AesUtils.DECRYPT_PASSWORD);
                String dataStr = AesUtils.decompressStr(Arrays.copyOfRange(newByteArray, 4, newByteArray.length));
                JSONArray dataJSONArray = JSONObject.parseObject(dataStr).getJSONArray("datas");
                for (int i = 0; i < dataJSONArray.size(); i++) {
                    jsonObject = dataJSONArray.getJSONObject(i);
                    xsinsert = new Xsinsert();
                    xsinsert.setNm(jsonObject.getString("nm"));
                    xsinsert.setTs(jsonObject.getString("ts"));
                    xsinsert.setTsformat(new SimpleDateFormat(BaseConstant.dateFormat).format(new java.util.Date(Long.parseLong(xsinsert.getTs()) * 1000)));
                    xsinsert.setV(jsonObject.getString("v"));
                    xsinsert.setCreatetime(createTime);
                    day = new SimpleDateFormat(BusinessConstant.DATE_FORMAT).format(new java.util.Date(Long.parseLong(xsinsert.getTs()) * 1000));
                    day2dataMap.putIfAbsent(day, new ArrayList<>());
                    day2dataMap.get(day).add(xsinsert);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!day2dataMap.isEmpty()) {
            RedisTemplate redisTemplate = (RedisTemplate) SpringContext.getBean("redisTemplate");
            Map<String, Object> queueMap;
            for (Map.Entry<String, List<Xsinsert>> item : day2dataMap.entrySet()) {
                queueMap = new HashMap<>();
                queueMap.put("day", item.getKey());
                queueMap.put("data", item.getValue());
                // 存入redis队列，待消费
                redisTemplate.opsForList().leftPush(BusinessConstant.QUEUE_NAME, JSON.toJSONString(queueMap));
            }
        }
    }

}
