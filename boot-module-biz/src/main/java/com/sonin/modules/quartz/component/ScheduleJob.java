package com.sonin.modules.quartz.component;

import com.sonin.modules.historydata.service.IHistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/22 11:04
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    @Autowired
    private IHistoryDataService historyDataService;

    /**
     * <pre>
     * 今日数据，每隔3min执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "*/30 * * * * ?")
    public void todayDataJob() {
        double val = historyDataService.queryDiffForDay("YBDLT1AH1ZDN", "2023-05-18");
        System.out.println(val);
    }

    /**
     * <pre>
     * 昨日数据，每天凌晨执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    // @Scheduled(cron = "*/180 * * * * ?")
    public void yesterdayDataJob() {

    }

}
