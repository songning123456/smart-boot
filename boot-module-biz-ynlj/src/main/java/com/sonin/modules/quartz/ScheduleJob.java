package com.sonin.modules.quartz;

import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/19 20:18
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    private Map<String, String> departId2NameMap = new LinkedHashMap<String, String>() {{
        put("c52933a54435435a9eb5eccd70dda63f", "庆云路开文村东口水质站");
        put("84f24f61a04b475a97cacf3688caa147", "东元桥水质站");
        put("476b9962a74fe4815724bf23c701e1", "集云桥水质站");
    }};

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
    public void realTimeDataJob() {
        try {
            String time = DateUtils.date2Str(new Date(), BaseConstant.dateFormat);
            log.info("开始执行 realTimeDataJob:" + time);
            realtimedataFunc();
            log.info("结束执行 realTimeDataJob:" + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void hourDataJob() {
        try {
            String time = DateUtils.date2Str(new Date(), BaseConstant.dateFormat);
            log.info("开始执行 hourDataJob:" + time);
            hourJob(1);
            log.info("结束执行 hourDataJob:" + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 历史数据：每隔5s执行一次
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void insertDataJob() {
        try {
            String time = DateUtils.date2Str(new Date(), BaseConstant.dateFormat);
            log.info("开始执行 insertDataJob:" + time);
            insertJob(1);
            log.info("结束执行 insertDataJob:" + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void realtimedataFunc() {
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        String nm, metricUidTag;
        Date now = new Date();
        String ts = "" + now.getTime() / 1000;
        double min = 0D, max = 0D;
        // 遍历每一个厂站
        for (String departId : departId2NameMap.keySet()) {
            List<Map<String, Object>> queryMapList = masterDB.queryForList("select * from sys_monitor_metric_info where 1=1 and metric_uid_tag in ('GJSS', 'GJCOD', 'GJNH3N') and depart_id = '" + departId + "'");
            for (Map<String, Object> item : queryMapList) {
                nm = "" + item.get("id");
                metricUidTag = "" + item.get("metric_uid_tag");
                switch (metricUidTag) {
                    case "GJSS":
                        min = 23D;
                        max = 27D;
                        break;
                    case "GJCOD":
                        min = 180D;
                        max = 190D;
                        break;
                    case "GJNH3N":
                        min = 3D;
                        max = 5D;
                        break;
                }
                String val = DigitalUtils.nPoint(min + (Math.random() * (max - min + 1)), 3);
                String updateSql = "update realtimedata set v = '" + val + "', ts='" + ts + "' where nm = '" + nm + "'";
                pgDB.execute(updateSql);
            }
        }
    }

    public void hourJob(int type) {
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        String nm, metricUidTag, initTime = "1680328800";
        if (type == 1) {
            initTime = "" + (System.currentTimeMillis() / 1000 - 3600);
        }
        double min = 0D, max = 0D;
        // 遍历每一个厂站
        for (String departId : departId2NameMap.keySet()) {
            List<Map<String, Object>> queryMapList = masterDB.queryForList("select * from sys_monitor_metric_info where 1=1 and metric_uid_tag in ('GJSS', 'GJCOD', 'GJNH3N') and depart_id = '" + departId + "'");
            for (Map<String, Object> item : queryMapList) {
                nm = "" + item.get("id");
                metricUidTag = "" + item.get("metric_uid_tag");
                switch (metricUidTag) {
                    case "GJSS":
                        min = 23D;
                        max = 27D;
                        break;
                    case "GJCOD":
                        min = 180D;
                        max = 190D;
                        break;
                    case "GJNH3N":
                        min = 3D;
                        max = 5D;
                        break;
                }
                String tableName = nm.split("_")[0].toLowerCase() + "_count";
                String selectSql = "select * from " + tableName + " where ts >= '" + initTime + "' and nm = '" + nm + "'";
                List<Map<String, Object>> mapList = pgDB.queryForList(selectSql);
                for (Map<String, Object> map : mapList) {
                    String id = "" + map.get("id");
                    String val = DigitalUtils.nPoint(min + (Math.random() * (max - min + 1)), 2);
                    String updateSql = "update " + tableName + " set v = '" + val + "' where id = '" + id + "'";
                    pgDB.execute(updateSql);
                }
            }
        }
    }

    public void insertJob(int type) {
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        String nm, metricUidTag, initTime = "1680328800";
        if (type == 1) {
            initTime = "" + (System.currentTimeMillis() / 1000 - 60);
        }
        double min = 0D, max = 0D;
        String yearMonth = DateUtils.date2Str(new Date(), "yyyyMM");
        // 遍历每一个厂站
        for (String departId : departId2NameMap.keySet()) {
            List<Map<String, Object>> queryMapList = masterDB.queryForList("select * from sys_monitor_metric_info where 1=1 and metric_uid_tag in ('GJSS', 'GJCOD', 'GJNH3N') and depart_id = '" + departId + "'");
            for (Map<String, Object> item : queryMapList) {
                nm = "" + item.get("id");
                metricUidTag = "" + item.get("metric_uid_tag");
                switch (metricUidTag) {
                    case "GJSS":
                        min = 23D;
                        max = 27D;
                        break;
                    case "GJCOD":
                        min = 180D;
                        max = 190D;
                        break;
                    case "GJNH3N":
                        min = 3D;
                        max = 5D;
                        break;
                }
                for (int i = 1; i <= 31; i++) {
                    try {
                        String suffix = (i < 10) ? ("0" + i) : ("" + i);
                        String tableName = "xsinsert" + yearMonth + suffix;
                        String selectSql = "select * from " + tableName + " where ts >= '" + initTime + "' and nm = '" + nm + "'";
                        List<Map<String, Object>> mapList = pgDB.queryForList(selectSql);
                        for (Map<String, Object> map : mapList) {
                            String id = "" + map.get("id");
                            String val = DigitalUtils.nPoint(min + (Math.random() * (max - min + 1)), 2);
                            String updateSql = "update " + tableName + " set v = '" + val + "' where id = '" + id + "'";
                            pgDB.execute(updateSql);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
