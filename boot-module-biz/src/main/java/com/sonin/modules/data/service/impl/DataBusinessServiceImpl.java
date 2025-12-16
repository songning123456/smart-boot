package com.sonin.modules.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.data.service.IDataBusinessService;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author sonin
 * @Date 2025/11/28 10:22
 */
@Service
public class DataBusinessServiceImpl implements IDataBusinessService {

    @Autowired
    private IMPPService mppService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public String handleRealtimeDataFunc(List<Map<String, Object>> dataMapList) {
        // 消息集
        List<String> messageList = new ArrayList<>();
        String delimiter = "\n";
        if (dataMapList.isEmpty()) {
            messageList.add("无推送数据！");
            return String.join(delimiter, messageList);
        }
        // 数据按照日分组
        Map<String, List<Map<String, Object>>> day2DataMap = new LinkedHashMap<>();
        for (Map<String, Object> dataMap : dataMapList) {
            String tmpTime = ConvertUtils.getString(dataMap.get("time"));
            if (!isValidDateTimeFormat(tmpTime)) {
                continue;
            }
            String tmpDay = tmpTime.substring(0, 10);
            day2DataMap.putIfAbsent(tmpDay, new ArrayList<>());
            day2DataMap.get(tmpDay).add(dataMap);
        }
        long createTime = DateUtils.dateStr2Sec(DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT), BusinessConstant.DATE_FORMAT);
        Set<String> nmSet = new HashSet<>();
        // 遍历插入
        for (Map.Entry<String, List<Map<String, Object>>> entry : day2DataMap.entrySet()) {
            String tmpDay = entry.getKey();
            List<Map<String, Object>> tmpMapList = entry.getValue();
            String tableNameSuffix = tmpDay.replaceAll("-", "");
            String tmpTable = "xsinsert" + tableNameSuffix;
            List<Map<String, Object>> insertMapList = new ArrayList<>();
            for (Map<String, Object> item : tmpMapList) {
                String tmpTime = ConvertUtils.getString(item.get("time"));
                Long ts = DateUtils.dateStr2Sec(tmpTime, BusinessConstant.DATE_FORMAT);
                String tmpTs = String.valueOf(ts);
                String tmpNm = ConvertUtils.getString(item.get("nm"));
                nmSet.add(tmpNm);
                Map<String, Object> insertMap = new HashMap<>(6);
                // insertMap.put("id", null);
                insertMap.put("nm", tmpNm);
                insertMap.put("v", ConvertUtils.getString(item.get("v")));
                insertMap.put("ts", tmpTs);
                insertMap.put("createtime", createTime);
                insertMap.put("factoryname", ConvertUtils.getString(item.get("departId")));
                insertMap.put("devicename", "API");
                insertMap.put("type", Integer.parseInt(ConvertUtils.getString(item.get("type"))));
                insertMap.put("gatewaycode", ConvertUtils.UUID(tmpNm + tmpTs));
                insertMapList.add(insertMap);
            }
            if (insertMapList.isEmpty()) {
                continue;
            }
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(insertMapList, 100);
            Integer res = DataSourceTemplate.execute("pg-db", () -> {
                transactionTemplate.execute(transactionStatus -> {
                    // 分批插入历史数据
                    partitionList.forEach(partition -> {
                        List<String> uniqueIdList = partition.stream().map(item -> ConvertUtils.getString(item.get("gatewaycode"))).collect(Collectors.toList());
                        mppService.delete(tmpTable, new QueryWrapper<>().in("gatewaycode", uniqueIdList));
                        mppService.insertBatch(tmpTable, partition);
                    });
                    return 1;
                });
                messageList.add(tmpDay + "数据推送成功！");
                return 1;
            });
            if (res == null || res != 1) {
                messageList.add(tmpDay + "数据推送失败！");
            }
        }
        // 更新实时表数据
        if (!nmSet.isEmpty()) {
            JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
            String maxTimeTableName = "xsinsert" + DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT.substring(0, 10)).replaceAll("-", "");
            String nmStr = nmSet.stream().map(item -> "'" + item + "'").collect(Collectors.joining(","));
            // 每个nm最大ts时间的值
            String sql = "WITH ranked_data AS ( SELECT *, ROW_NUMBER() OVER ( PARTITION BY nm ORDER BY CASE WHEN ts ~ '^[0-9]+$' THEN CAST(ts AS bigint) ELSE -1 END DESC ) AS rn FROM {tableName} where 1=1 and {condition}) SELECT * FROM ranked_data WHERE rn = 1;";
            sql = sql.replaceAll("\\{tableName}", maxTimeTableName).replaceAll("\\{condition}", " nm in (" + nmStr + ")");
            List<Map<String, Object>> maxTimeDataList = pgDB.queryForList(sql);
            if (!maxTimeDataList.isEmpty()) {
                maxTimeDataList.forEach(item -> {
                    item.remove("id");
                    item.remove("rn");
                });
                List<List<Map<String, Object>>> partitionList = ListUtils.partition(maxTimeDataList, 100);
                String tmpTableName = "realtimedata";
                Integer res = DataSourceTemplate.execute("pg-db", () -> {
                    transactionTemplate.execute(transactionStatus -> {
                        // 分批插入实时数据
                        partitionList.forEach(partition -> {
                            List<String> uniqueIdList = partition.stream().map(item -> ConvertUtils.getString(item.get("nm"))).collect(Collectors.toList());
                            mppService.delete(tmpTableName, new QueryWrapper<>().in("nm", uniqueIdList));
                            mppService.insertBatch(tmpTableName, partition);
                        });
                        return 1;
                    });
                    messageList.add("实时数据更新成功！");
                    return 1;
                });
                if (res == null || res != 1) {
                    messageList.add("实时数据更新失败！");
                }
            } else {
                messageList.add("无实时数据更新！");
            }
        }
        return String.join(delimiter, messageList);
    }

    @Override
    public void handleAggDataFunc(Map<String, Object> paramsMap) {
        // 请求参数
        String startTimeParam = ConvertUtils.getString(paramsMap.get("startTime"));
        String endTimeParam = ConvertUtils.getString(paramsMap.get("endTime"));
        List<String> dayList = DateUtils.intervalByDay(startTimeParam, endTimeParam, BusinessConstant.DATE_FORMAT.substring(0, 10));
        // 查询所有需要聚合的数据
        QueryWrapper<?> realtimeQueryWrapper = new QueryWrapper<>();
        // 可根据实际情况修改条件
        realtimeQueryWrapper.eq("devicename", "API");
        List<Map<String, Object>> realtimeMapList = DataSourceTemplate.execute("pg-db", () -> mppService.queryForList("select * from realtimedata", realtimeQueryWrapper));
        // 获取这些nm对应的count表
        if (!realtimeMapList.isEmpty()) {
            // 待插入对象集合
            Map<String, List<Map<String, Object>>> tableNamePrefix2AggListMap = new HashMap<>();
            Map<String, String> nm2TypeDictMap = realtimeMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("nm")), item -> ConvertUtils.getString(item.get("type")), (v1, v2) -> v1));
            Map<String, String> nm2FactoryNameDictMap = realtimeMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("nm")), item -> ConvertUtils.getString(item.get("factoryname")), (v1, v2) -> v1));
            // 查询点位对应的count表
            List<String> metricInfoColumnList = new ArrayList<String>() {{
                add("sys_monitor_metric_info.id as nm");
                add("ifnull(sys_factory_device.device_id, 'default') as tableName");
            }};
            List<Map<String, Object>> metricInfoMapList = mppService.queryForList("select " + String.join(",", metricInfoColumnList) + " from sys_monitor_metric_info left join sys_factory_device on sys_monitor_metric_info.depart_id = sys_factory_device.depart_id", new QueryWrapper<>().in("sys_monitor_metric_info.id", nm2TypeDictMap.keySet()));
            if (!metricInfoMapList.isEmpty()) {
                Map<String, String> nm2TableNameMap = metricInfoMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("nm")), item -> ConvertUtils.getString(item.get("tableName"))));
                String tmpGroupByTime = "to_char(to_timestamp(cast(ts as bigint)), 'yyyy-MM-dd HH24')";
                String tmpStartTs = ConvertUtils.getString(DateUtils.dateStr2Sec(startTimeParam, BusinessConstant.DATE_FORMAT));
                String tmpEndTs = ConvertUtils.getString(DateUtils.dateStr2Sec(endTimeParam, BusinessConstant.DATE_FORMAT));
                String inMetricIdStr = nm2TableNameMap.keySet().stream().map(item -> "'" + item + "'").collect(Collectors.joining(","));
                String pgTable = dayList.stream().map(it -> "select * from " + "xsinsert" + it.replaceAll("-", "") + " where nm in (" + inMetricIdStr + ")").collect(Collectors.joining(" union all "));
                // 此方法查询较慢，>10min。
                QueryWrapper<?> pgQueryWrapper = new QueryWrapper<>();
                pgQueryWrapper.ge("ts", tmpStartTs)
                        .le("ts", tmpEndTs)
                        .in("nm", nm2TableNameMap.keySet())
                        .apply("v is not null and v != ''")
                        .groupBy("nm", tmpGroupByTime);
                List<String> pgColumnList = new ArrayList<String>() {{
                    add("nm");
                    add(tmpGroupByTime + " as time");
                    add("coalesce((select v from (" + pgTable + ") as bbb where nm = aaa.nm and ts = min(aaa.ts) limit 1), '0') as \"minTimeValue\"");
                    add("coalesce((select v from (" + pgTable + ") as bbb where nm = aaa.nm and ts = max(aaa.ts) limit 1), '0') as \"maxTimeValue\"");
                    add("avg(cast(v as numeric)) as \"avgValue\"");
                }};
                List<Map<String, Object>> pgQueryMapList = DataSourceTemplate.execute("pg-db", () -> mppService.queryForList("select " + String.join(",", pgColumnList) + " from (" + pgTable + ") as aaa", pgQueryWrapper));
                long createTime = DateUtils.dateStr2Sec(DateUtils.date2Str(new Date(), BusinessConstant.DATE_FORMAT), BusinessConstant.DATE_FORMAT);
                for (Map<String, Object> item : pgQueryMapList) {
                    String tmpTime = ConvertUtils.getString(item.get("time"));
                    String tmpNm = ConvertUtils.getString(item.get("nm"));
                    Double minTimeValue = ConvertUtils.getDouble(item.get("minTimeValue"), 0D);
                    Double maxTimeValue = ConvertUtils.getDouble(item.get("maxTimeValue"), 0D);
                    String avgValue = ConvertUtils.getString(item.get("avgValue"));
                    if (nm2TableNameMap.containsKey(tmpNm)) {
                        Map<String, Object> aggMap = new HashMap<>(6);
                        aggMap.put("nm", tmpNm);
                        String tmpType = nm2TypeDictMap.getOrDefault(tmpNm, "4");
                        String tmpValue;
                        // 求平均
                        if ("4".equals(tmpType)) {
                            tmpValue = avgValue;
                        } else {
                            tmpValue = String.valueOf(maxTimeValue - minTimeValue);
                        }
                        aggMap.put("v", tmpValue);
                        String tmpTs = DateUtils.dateStr2Sec(tmpTime + ":00:00", BusinessConstant.DATE_FORMAT).toString();
                        aggMap.put("ts", tmpTs);
                        aggMap.put("createtime", createTime);
                        aggMap.put("factoryname", nm2FactoryNameDictMap.get(tmpNm));
                        aggMap.put("devicename", "API");
                        aggMap.put("type", tmpType);
                        aggMap.put("gatewaycode", ConvertUtils.UUID(tmpNm + tmpTs));
                        String tmpTableNamePrefix = nm2TableNameMap.get(tmpNm);
                        tableNamePrefix2AggListMap.putIfAbsent(tmpTableNamePrefix, new ArrayList<>());
                        tableNamePrefix2AggListMap.get(tmpTableNamePrefix).add(aggMap);
                    }
                }
            }
            for (String tmpTablePrefix : tableNamePrefix2AggListMap.keySet()) {
                if (StringUtils.isEmpty(tmpTablePrefix)) {
                    continue;
                }
                List<List<Map<String, Object>>> partitionList = ListUtils.partition(tableNamePrefix2AggListMap.get(tmpTablePrefix), 100);
                String tmpTable = tmpTablePrefix.toLowerCase() + "_count";
                DataSourceTemplate.execute("pg-db", () -> {
                    transactionTemplate.execute(transactionStatus -> {
                        // 分批插入历史数据
                        partitionList.forEach(partition -> {
                            List<String> uniqueIdList = partition.stream().map(item -> ConvertUtils.getString(item.get("gatewaycode"))).collect(Collectors.toList());
                            mppService.delete(tmpTable, new QueryWrapper<>().in("gatewaycode", uniqueIdList));
                            mppService.insertBatch(tmpTable, partition);
                        });
                        return 1;
                    });
                    return 1;
                });
            }

        }
    }

    /**
     * 判断字符串是否为 yyyy-MM-dd HH:mm:ss 格式（Java 8+ 推荐）
     *
     * @param dateStr 待校验的日期字符串
     * @return true = 格式正确，false = 格式错误
     */
    private boolean isValidDateTimeFormat(String dateStr) {
        // 1. 提前过滤 null 或空字符串
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        // 2. 定义日期格式（严格匹配 yyyy-MM-dd HH:mm:ss）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BusinessConstant.DATE_FORMAT);
        try {
            // 3. 尝试解析字符串为 LocalDateTime（解析失败会抛出异常）
            LocalDateTime.parse(dateStr.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            // 解析失败：格式错误或日期无效（如 2024-02-30）
            e.printStackTrace();
            return false;
        }
    }

}
