package com.sonin.modules.logback;

import com.google.common.collect.Queues;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.sys.entity.SysLog;
import com.sonin.modules.sys.service.SysLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 消费日志
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/6 11:30
 */
@Slf4j
public class LogbackConsumer implements ILogback, Runnable {

    @Override
    public void run() {
        List<SysLog> sysLogList;
        while (true) {
            try {
                // 每次取出的数据存放到sysLogList里
                sysLogList = new ArrayList<>();
                // 每次到1000条数据才进行入库，等待10s，没达到1000条也继续入库
                Queues.drain(sysLogQueue, sysLogList, 1000, 10, TimeUnit.SECONDS);
                SysLogService sysLogService = SpringContext.getBean(SysLogService.class);
                sysLogService.saveBatch(sysLogList);
            } catch (Exception e) {
                log.error("LogbackConsumer drain queues error: {}", e.getMessage());
            }
        }
    }

}
