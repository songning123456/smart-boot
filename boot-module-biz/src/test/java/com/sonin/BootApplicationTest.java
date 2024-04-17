package com.sonin;

import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.schedule.service.IScheduleService;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class BootApplicationTest {

    @Autowired
    private IBaseService baseService;

    @Autowired
    private IScheduleService scheduleService;

    @Test
    public void jobTest() {
        String jobType = "day";
        if ("hour".equals(jobType)) {
            // Date nowDate = DateUtils.prevHour(new Date());
            Date nowDate = DateUtils.strToDate("2024-04-17 11:18:02", BaseConstant.dateFormat);
            String nowDateStr = DateUtils.date2Str(nowDate, BaseConstant.dateFormat);
            String startTime = nowDateStr.substring(0, 14) + "00:00";
            String endTime = nowDateStr.substring(0, 14) + "59:59";
            scheduleService.generateDataFunc(startTime, endTime);
        } else if ("day".equals(jobType)) {
            Date nowDate = DateUtils.prevDay(new Date());
            String nowDateStr = DateUtils.date2Str(nowDate, BaseConstant.dateFormat);
            String startTime = nowDateStr.substring(0, 11) + "00:00:00";
            String endTime = nowDateStr.substring(0, 11) + "23:59:59";
            scheduleService.generateDataFunc(startTime, endTime);
        } else if ("view".equals(jobType)) {
            String nowDateStr = DateUtils.date2Str(new Date(), BaseConstant.dateFormat);
            String startTime = nowDateStr.substring(0, 11) + "00:00:00";
            scheduleService.generateViewFunc(startTime);
        }
    }

}
