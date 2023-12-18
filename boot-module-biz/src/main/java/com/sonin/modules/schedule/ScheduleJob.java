package com.sonin.modules.schedule;

import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.schedule.serivce.ICustomBusiness;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/12/18 14:31
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    @Autowired
    private ICustomBusiness customBusiness;

    /**
     * <pre>
     * 每个小时10分执行
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "${biz.scheduled.hourCron}")
    public void hourJob() {
        log.info(">>> 开始执行{}定时任务 <<<", "生成小时数据");
        String startTime = DateUtils.date2Str(DateUtils.prevHour(new Date()), BaseConstant.dateFormat);
        customBusiness.generateHourDataFunc(startTime, startTime);
    }

}
