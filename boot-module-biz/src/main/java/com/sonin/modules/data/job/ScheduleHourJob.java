package com.sonin.modules.data.job;

import com.sonin.core.constant.BusinessConstant;
import com.sonin.modules.data.service.IDataBusinessService;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/9/18 11:30
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "biz.scheduled", name = "enable", havingValue = "true")
public class ScheduleHourJob {

    @Autowired
    private IDataBusinessService dataBusinessService;

    /**
     * 每小时同步数据
     */
    @Scheduled(cron = "${biz.scheduled.task1hour10min}")
    public void hourJob() {
        String[] timeArr = this.timeRangeByDateFunc(new Date());
        Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("startTime", timeArr[0]);
        paramsMap.put("endTime", timeArr[1]);
        String jobName000 = "PG小时数据转换";
        try {
            log.info(">>> 执行小时任务 {} 开始<<<", jobName000);
            dataBusinessService.handleAggDataFunc(paramsMap);
            log.info(">>> 执行小时任务 {} 结束<<<", jobName000);
        } catch (Exception e) {
            log.info(">>> 执行小时任务 {} 异常<<<", jobName000);
            e.printStackTrace();
        }
    }

    /**
     * 根据 当前时间获取前一个小时数据
     *
     * @param date
     * @return
     */
    private String[] timeRangeByDateFunc(Date date) {
        Date prevDate = DateUtils.prevHour(date);
        String prevTimePrefix = DateUtils.date2Str(prevDate, BusinessConstant.DATE_FORMAT).substring(0, 13);
        String startTime = prevTimePrefix + ":00:00";
        String endTime = prevTimePrefix + ":59:59";
        return new String[]{startTime, endTime};
    }

}
