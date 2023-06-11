package com.sonin.modules.xsinsert.quartz.job;

import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.constant.BusinessConstant;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Value("${biz.deleteTable.enable:}")
    private String enable;
    @Value("${biz.deleteTable.beforeDay:10}")
    private String beforeDay;
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String dataBaseUrl;

    /**
     * <pre>
     * 每天凌晨1点执行
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void createTableJob() {
        log.info(">>> 开始执行定时任务 <<<");
        createTableFunc();
        deleteTableFunc();
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
    public void createTableFunc() {
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
    public void deleteTableFunc() {
        try {
            if ("true".equals(enable)) {
                if (!DigitalUtils.isNumeric(beforeDay)) {
                    beforeDay = "10";
                }
                String todayStr = DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT);
                JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
                String dataBaseSchema = dataBaseUrl.substring(dataBaseUrl.lastIndexOf("/") + 1, dataBaseUrl.indexOf("?"));
                String dropTableSql = "select distinct table_name from information_schema.tables where table_schema = ? and table_name like concat(?, '%')";
                List<Map<String, Object>> queryMapList = masterDB.queryForList(dropTableSql, new Object[]{dataBaseSchema, BusinessConstant.BASE_TABLE});
                for (Map<String, Object> item : queryMapList) {
                    String tableName = String.valueOf(item.get("table_name"));
                    String tableNameSuffix = tableName.replaceFirst(BusinessConstant.BASE_TABLE, "");
                    if (DigitalUtils.isNumeric(tableNameSuffix)) {
                        boolean flag = DateUtils.strToDate(tableNameSuffix, BusinessConstant.DATE_FORMAT).getTime() / 1000 + 3600 * 24 * new Integer(beforeDay) <= DateUtils.strToDate(todayStr, BusinessConstant.DATE_FORMAT).getTime() / 1000;
                        if (flag) {
                            masterDB.execute("drop table if exists " + tableName);
                            log.info(">>> 删除表" + tableName + "成功 <<<");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
