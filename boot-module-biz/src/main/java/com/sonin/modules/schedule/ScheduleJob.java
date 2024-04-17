package com.sonin.modules.schedule;

import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.schedule.service.IScheduleService;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author：sonin
 * @Date：2024/4/17 10:46
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    @Autowired
    private IScheduleService scheduleService;

    /**
     * 每个小时1min执行
     */
    @Scheduled(cron = "${biz.scheduled.hourCron}")
    public void hourJob() {
        log.info("~~~ 开始执行{}定时任务 ~~~", "hour");
        Date nowDate = DateUtils.prevHour(new Date());
        String nowDateStr = DateUtils.date2Str(nowDate, BaseConstant.dateFormat);
        String startTime = nowDateStr.substring(0, 14) + "00:00";
        String endTime = nowDateStr.substring(0, 14) + "59:59";
        scheduleService.generateDataFunc(startTime, endTime);
    }

    @Scheduled(cron = "${biz.scheduled.dayCron}")
    public void dayJob() {
        log.info("~~~ 开始执行{}定时任务 ~~~", "day");
        Date nowDate = DateUtils.prevDay(new Date());
        String nowDateStr = DateUtils.date2Str(nowDate, BaseConstant.dateFormat);
        String startTime = nowDateStr.substring(0, 11) + "00:00:00";
        String endTime = nowDateStr.substring(0, 11) + "23:59:59";
        scheduleService.generateDataFunc(startTime, endTime);
    }

    @Scheduled(cron = "${biz.scheduled.viewCron}")
    public void viewJob() {
        log.info("~~~ 开始执行{}定时任务 ~~~", "view");
        String nowDateStr = DateUtils.date2Str(new Date(), BaseConstant.dateFormat);
        String startTime = nowDateStr.substring(0, 11) + "00:00:00";
        scheduleService.generateViewFunc(startTime);
    }

}
