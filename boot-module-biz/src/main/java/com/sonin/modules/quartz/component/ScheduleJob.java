package com.sonin.modules.quartz.component;

import com.sonin.core.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean("taos-db");
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select ts as time,tag_name as monitorId,tag_value as monitorValue from qxdb.historydata where tag_name in('YBDLT1AH1ZDN') and ts >= '2023-05-18 00:00:00' and ts < '2023-05-18 23:59:59'");
        System.out.println("");
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
