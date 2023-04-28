package com.sonin.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 14:23
 */
@Slf4j
@Component
@EnableScheduling
public class QuartzJob {

    @Autowired
    private IBaseService baseService;

    /**
     * <pre>
     * kafka数据：每隔5s执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void mysqlJob() {

    }

    /**
     * <pre>
     * 小时数据：每隔1hour执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "0 */60 * * * ?")
    public void pgCountJob() {
        // 表名
        String tableName = "xxx_count";
        // 2小时前
        long startTime = System.currentTimeMillis() / 1000 - 3600 * 2;
        List<Map<String, Object>> queryMapList = (List<Map<String, Object>>) DataSourceTemplate.execute("pg-src", () -> baseService.queryForList("select * from " + tableName, new QueryWrapper<>().ge("ts", startTime)));
        DataSourceTemplate.execute("pg-target", () -> {
            for (Map<String, Object> item : queryMapList) {
                try {
                    baseService.insert(tableName, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    /**
     * <pre>
     * 实时数据：每隔5s执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void pgInsertJob() {
        // 表名
        String tableName = "equipment_info";
        List<Map<String, Object>> queryMapList = DataSourceTemplate.execute("slave", () -> baseService.queryForList("select * from " + tableName, new QueryWrapper<>()));
        System.out.println("");
    }

    /**
     * <pre>
     * 实时数据：每隔5s执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void pgRealtimeJob() {

    }

}
