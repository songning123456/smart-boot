package com.sonin.modules.mqtt.consumer;

import com.google.common.collect.Queues;
import com.sonin.modules.constant.BusinessConstant;
import com.sonin.modules.constant.IDataQueue;
import com.sonin.modules.mqtt.thread.DataRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
@Slf4j
@Component
public class DataQueueConsumer implements Runnable, IDataQueue {

    @Resource(name = "asyncRedisExecutor")
    private Executor asyncRedisExecutor;

    @Override
    public void run() {
        List<String> dataList;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 每次取出的数据存放到dataList里
                dataList = new ArrayList<>();
                // 每次到1000条数据才进行入库，等待10s，没达到1000条也继续入库
                Queues.drain(DATA_QUEUE, dataList, BusinessConstant.PAGE_SIZE, BusinessConstant.TIMEOUT, TimeUnit.SECONDS);
                if (!dataList.isEmpty()) {
                    asyncRedisExecutor.execute(new DataRunnable(dataList));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
