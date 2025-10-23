package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

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
@ActiveProfiles("chishui")
public class ChishuiBootApplicationTest {

    @Autowired
    private IMPPService mppService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void exportCountDataTest() {
        // 机构信息 赤水市污水处理厂
        String departId = "7613b4ccf8af4ab096543e3fd5d50a5d";
        String startTime = "2022-01-01 00:00:00";
        String endTime = "2026-01-01 00:00:00";
        // 查询所有点位数据
        QueryWrapper<?> metricInfoQueryWrapper = new QueryWrapper<>();
        metricInfoQueryWrapper.eq("fac_code", departId)
                .inSql("id", "select key_index from report_header where report_id = '078560f8a9331eb356626f8500eb5a7e'");
        List<Map<String, Object>> metricInfoMapList = mppService.queryForList("select * from sys_monitor_metric_info", metricInfoQueryWrapper);
        Map<String, String> columnDescMap = metricInfoMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("id")), item -> ConvertUtils.getString(item.get("metric_name")), (v1, v2) -> v1, LinkedHashMap::new));
        columnDescMap.put("time", "时间");
        // 根据departId创建count数据表
        String targetTableName = "count_" + departId;
        String createTableSql = createTableFunc(targetTableName, columnDescMap);
        JdbcTemplate demoDB = (JdbcTemplate) SpringContext.getBean("demo");
        demoDB.execute(createTableSql);
        // 查询count表所有数据
        Map<String, Object> factoryDeviceMap = mppService.queryForMap("select * from sys_factory_device", new QueryWrapper<>().eq("factory_id", departId));
        String srcCountName = ConvertUtils.getString(factoryDeviceMap.get("device_id")).toLowerCase() + "_count";
        // 按月分组
        List<String> monthList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 7));
        for (String month : monthList) {
            String tmpStartTime = month + "-01" + BusinessConstant.START_TIME_SUFFIX;
            String[] timeArr = month.split("-");
            int dayLength = DateUtils.lengthOfSomeMonth(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]));
            String tmpEndTime = month + "-" + dayLength + BusinessConstant.END_TIME_SUFFIX;
            String tmpStartTs = DateUtils.dateStr2Sec(tmpStartTime, BusinessConstant.DATE_FORMAT).toString();
            String tmpEndTs = DateUtils.dateStr2Sec(tmpEndTime, BusinessConstant.DATE_FORMAT).toString();
            QueryWrapper<?> pgQueryWrapper = new QueryWrapper<>();
            pgQueryWrapper.in("nm", columnDescMap.keySet())
                    .ge("ts", tmpStartTs)
                    .le("ts", tmpEndTs);
            List<Map<String, Object>> pgQueryMapList = DataSourceTemplate.execute("pg-db", () -> mppService.queryForList("select * from " + srcCountName, pgQueryWrapper));
            // 按照时间组装并插入
            Map<String, Map<String, Object>> time2EntityMap = new HashMap<>();
            for (Map<String, Object> item : pgQueryMapList) {
                String ts = ConvertUtils.getString(item.get("ts"));
                String time = DateUtils.sec2DateStr(Long.parseLong(ts), BusinessConstant.DATE_FORMAT);
                String nm = ConvertUtils.getString(item.get("nm"));
                String v = ConvertUtils.getString(item.get("v"));
                time2EntityMap.putIfAbsent(time, new HashMap<String, Object>() {{
                    put("id", time);
                    put("time", time);
                }});
                time2EntityMap.get(time).put(nm.toLowerCase(), v);
            }
            // 待插入数据
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(new ArrayList<>(time2EntityMap.values()), 100);
            DataSourceTemplate.execute("demo", () -> {
                transactionTemplate.execute(transactionStatus -> {
                    partitionList.forEach(partition -> mppService.insertBatch(targetTableName, partition, MPPConstant.INSERT_IGNORE));
                    return 1;
                });
                return 1;
            });
        }
    }

    @Test
    public void exportItemvDataTest() {
        // 机构信息 赤水市污水处理厂
        String departId = "7613b4ccf8af4ab096543e3fd5d50a5d";
        String startTime = "2022-01-01 00:00:00";
        String endTime = "2026-01-01 00:00:00";
        // 查询化验数据
        String codes = "cyrq9840,jcwcrq9010,tqqk6e1a,wd472b," +
                "JSBOD,JSSS,JSCOD,JSAD,JSTN,JSTP,JSPH,JSSW," +
                "CSBOD,CSSS,CSCOD,CSAD,CSTN,CSTP,CSPH,CSSW," +
                "FDCGJ,sftne1ae,TNHWNHSL,tbref02,BZ,fj12cb";
        QueryWrapper<?> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("report_id", "5b542809fa0821a5e0ec6c3b378fbb23")
                .in("item_code", Arrays.asList(codes.split(",")));
        List<Map<String, Object>> itemMapList = mppService.queryForList("select * from f_report_item", itemQueryWrapper);
        Map<String, String> columnDescMap = itemMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("item_code")), item -> ConvertUtils.getString(item.get("item_alias")), (v1, v2) -> v1, LinkedHashMap::new));
        Map<String, String> id2CodeDictMap = itemMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("id")), item -> ConvertUtils.getString(item.get("item_code")), (v1, v2) -> v1, LinkedHashMap::new));
        columnDescMap.put("time", "时间");
        // 根据departId创建count数据表
        String targetTableName = "itemv_" + departId;
        String createTableSql = createTableFunc(targetTableName, columnDescMap);
        JdbcTemplate demoDB = (JdbcTemplate) SpringContext.getBean("demo");
        demoDB.execute(createTableSql);
        // 按月分组
        List<String> monthList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 7));
        for (String month : monthList) {
            String tmpStartTime = month + "-01" + BusinessConstant.START_TIME_SUFFIX;
            String[] timeArr = month.split("-");
            int dayLength = DateUtils.lengthOfSomeMonth(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]));
            String tmpEndTime = month + "-" + dayLength + BusinessConstant.END_TIME_SUFFIX;
            // 查询所有itemv数据
            QueryWrapper<?> itemvQueryWrapper = new QueryWrapper<>();
            itemvQueryWrapper.in("reit_id", id2CodeDictMap.keySet())
                    .eq("factory_id", departId)
                    .ge("substring(data_time, 1, 7)", tmpStartTime.substring(0, 7))
                    .le("substring(data_time, 1, 7)", tmpEndTime.substring(0, 7));
            List<Map<String, Object>> itemvQueryMapList = mppService.queryForList("select * from f_report_itemv", itemvQueryWrapper);
            // 按照时间组装并插入
            Map<String, Map<String, Object>> time2EntityMap = new HashMap<>();
            for (Map<String, Object> item : itemvQueryMapList) {
                String reitId = ConvertUtils.getString(item.get("reit_id"));
                String itemCode = id2CodeDictMap.get(reitId);
                String dataTime = ConvertUtils.getString(item.get("data_time"));
                String itemValue = ConvertUtils.getString(item.get("item_value"));
                time2EntityMap.putIfAbsent(dataTime, new HashMap<String, Object>() {{
                    put("id", dataTime);
                    put("time", dataTime);
                }});
                time2EntityMap.get(dataTime).put(itemCode.toLowerCase(), itemValue);
            }
            // 待插入数据
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(new ArrayList<>(time2EntityMap.values()), 100);
            DataSourceTemplate.execute("demo", () -> {
                transactionTemplate.execute(transactionStatus -> {
                    partitionList.forEach(partition -> mppService.insertBatch(targetTableName, partition, MPPConstant.INSERT_IGNORE));
                    return 1;
                });
                return 1;
            });
        }
    }

    private String createTableFunc(String tableName, Map<String, String> columnDescMap) {
        String tableStr = "CREATE TABLE IF NOT EXISTS `{tableName}` (\n";
        tableStr += "`id` varchar(64) NOT NULL COMMENT '主键',\n";
        for (Map.Entry<String, String> entry : columnDescMap.entrySet()) {
            String tmpStr = "`{var0}` varchar(40) DEFAULT NULL COMMENT '{var1}',\n";
            tmpStr = tmpStr.replaceAll("\\{var0}", entry.getKey().toLowerCase()).replaceAll("\\{var1}", entry.getKey() + ";" + entry.getValue());
            tableStr += tmpStr;
        }
        tableStr += "PRIMARY KEY (`id`) USING BTREE\n" +
                ")  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC ROW_FORMAT=DYNAMIC COMMENT='{tableName}';";
        tableStr = tableStr.replaceAll("\\{tableName}", tableName);
        return tableStr;
    }
}
