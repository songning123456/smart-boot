package com.sonin.modules.xsinsert.quartz.job;

import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.constant.BusinessConstant;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 定时任务
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 18:13
 */
@EnableScheduling
@Service
@Slf4j
public class ScheduleJob {

    @Value("${biz.deleteTableDay:10}")
    private String deleteTableDay;
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String dataBaseUrl;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * <pre>
     * 每天凌晨1点执行
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "${biz.scheduled.createTableCron}")
    public void createTableJob() {
        log.info(">>> 开始执行{}定时任务 <<<", "创建表");
        createMysqlTableFunc();
    }

    @Scheduled(cron = "${biz.scheduled.deleteTableCron}")
    public void deleteTableJob() {
        log.info(">>> 开始执行{}定时任务 <<<", "删除表");
        deleteMysqlTableFunc();
    }

    @Scheduled(cron = "${biz.scheduled.updateRealtimeDataCron}")
    public void updateRealtimeDataJob() {
        log.info(">>> 开始执行{}定时任务 <<<", "更新realtimedata表");
        updateRealtimeDataFunc();
    }

    /**
     * <pre>
     * 创建接下来的一个月表结构
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public void createMysqlTableFunc() {
        Date now = new Date();
        List<String> dayList = DateUtils.intervalByDay(DateUtils.date2Str(now, BaseConstant.dateFormat), DateUtils.date2Str(DateUtils.nextMonth(now), BaseConstant.dateFormat), BaseConstant.dateFormat.split(" ")[0]);
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        for (String yearMonthDay : dayList) {
            yearMonthDay = yearMonthDay.replaceAll("-", "");
            String createTableSql = "CREATE TABLE `xsinsert" + yearMonthDay + "` (\n" +
                    "\t`id` VARCHAR ( 100 ) NOT NULL COMMENT '主键ID',\n" +
                    "\t`nm` VARCHAR ( 200 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`v` VARCHAR ( 40 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`ts` VARCHAR ( 64 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`createtime` VARCHAR ( 64 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`factoryname` VARCHAR ( 255 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`devicename` VARCHAR ( 255 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`type` VARCHAR ( 255 ) DEFAULT NULL COMMENT '',\n" +
                    "\t`gatewaycode` VARCHAR ( 255 ) DEFAULT NULL COMMENT '',\n" +
                    "\tPRIMARY KEY ( `id` ) USING BTREE,\n" +
                    "\tKEY `index_nm` ( `nm` ) USING BTREE,\n" +
                    "\tKEY `index_ts` ( `ts` ) USING BTREE\n" +
                    ") ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '历史表" + yearMonthDay + "'";
            try {
                masterDB.execute(createTableSql);
                log.info(">>> 创建表" + yearMonthDay + "成功 <<<");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <pre>
     * 删除指定日期（包含）之前的表
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public void deleteMysqlTableFunc() {
        try {
            if (!DigitalUtils.isNumeric(deleteTableDay)) {
                deleteTableDay = "10";
            }
            String todayStr = DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT);
            // 今日0点秒
            long todaySecond = DateUtils.strToDate(todayStr, BusinessConstant.DATE_FORMAT).getTime() / 1000;
            // n天换算成秒
            long beforeSecond = 3600 * 24 * new Integer(deleteTableDay);
            JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
            String dataBaseSchema = dataBaseUrl.substring(dataBaseUrl.lastIndexOf("/") + 1, dataBaseUrl.indexOf("?"));
            String dropTableSql = "select distinct table_name from information_schema.tables where table_schema = ? and table_name like concat(?, '%')";
            List<Map<String, Object>> queryMapList = masterDB.queryForList(dropTableSql, new Object[]{dataBaseSchema, BusinessConstant.BASE_TABLE});
            String tableName, tableNameSuffix;
            boolean flag;
            for (Map<String, Object> item : queryMapList) {
                tableName = String.valueOf(item.get("table_name"));
                tableNameSuffix = tableName.replaceFirst(BusinessConstant.BASE_TABLE, "");
                if (DigitalUtils.isNumeric(tableNameSuffix)) {
                    flag = DateUtils.strToDate(tableNameSuffix, BusinessConstant.DATE_FORMAT).getTime() / 1000 + beforeSecond <= todaySecond;
                    if (flag) {
                        masterDB.execute("drop table if exists " + tableName);
                        log.info(">>> 删除表" + tableName + "成功 <<<");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRealtimeDataFunc() {
        try {
            String day = DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT);
            JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
            String sqlPrefix = "INSERT INTO realtimedata ( nm, v, ts, createtime, factoryname, devicename, type, gatewaycode ) ";
            String sqlSuffix = "SELECT t1.nm, t1.v, t1.ts, t1.createtime, t1.factoryname, t1.devicename, t1.type, t1.gatewaycode FROM xsinsert${day} t1 INNER JOIN ( SELECT nm, max( ts ) AS ts FROM xsinsert${day} GROUP BY nm ) t2 ON t1.nm = t2.nm AND t1.ts = t2.ts";
            sqlSuffix = sqlSuffix.replaceAll("\\$\\{day}", day);
            String deleteSql = "delete from realtimedata where nm in (select tmp.nm from (" + sqlSuffix + ") as tmp)";
            String updateSql = sqlPrefix + sqlSuffix;
            transactionTemplate.execute((transactionStatus -> {
                masterDB.execute(deleteSql);
                masterDB.execute(updateSql);
                return 1;
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
