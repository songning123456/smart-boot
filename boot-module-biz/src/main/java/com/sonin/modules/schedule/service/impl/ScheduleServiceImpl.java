package com.sonin.modules.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.schedule.service.IScheduleService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：sonin
 * @Date：2024/4/17 10:52
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements IScheduleService {

    @Autowired
    private IBaseService baseService;

    @Value("${biz.view.days:30}")
    private String viewDays;

    @Value("${biz.view.name:view_dcs_history_info}")
    private String viewName;

    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String masterUrl;

    @Override
    public void generateViewFunc(String endTime) {
        String schema = masterUrl.split("/")[3].split("\\?")[0];
        String tablePrefix = "dcs_history_info_";
        // 判断视图是否存在
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("TABLE_SCHEMA", schema).eq("TABLE_NAME", viewName);
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("SELECT * FROM information_schema.VIEWS", queryWrapper0);
        String viewPrefix = "create";
        if (!queryMapList0.isEmpty()) {
            viewPrefix = "alter";
        }
        // 获取dcs_history_info_所有表
        QueryWrapper<?> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("TABLE_SCHEMA", schema).like("TABLE_NAME", tablePrefix);
        List<Map<String, Object>> queryMapList1 = baseService.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES", queryWrapper1);
        List<String> tableNameList = queryMapList1.stream().map(item -> ConvertUtils.getString(item.get("TABLE_NAME"))).collect(Collectors.toList());
        // 往前推days
        List<String> dayList = DateUtils.decreaseDay(endTime, Integer.parseInt(viewDays));
        StringBuilder viewSB = new StringBuilder();
        viewSB.append(viewPrefix).append(" view ").append(viewName).append(" as");
        for (String day : dayList) {
            String tableSuffix = day.substring(0, 10).replaceAll("-", "");
            String tableName = tablePrefix + tableSuffix;
            if (tableNameList.contains(tableName)) {
                viewSB.append(" union all select * from ").append(tableName);
            }
        }
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        masterDB.execute(viewSB.toString().replaceFirst("union all", ""));
    }

    @Override
    public void generateDataFunc(String startTime, String endTime) {
        long startTs = DateUtils.dateStr2Sec(startTime, BaseConstant.dateFormat).longValue();
        long endTs = DateUtils.dateStr2Sec(endTime, BaseConstant.dateFormat).longValue();
        // 默认小时格式
        String dateFormat = "hour";
        if (endTs - startTs == 3600 * 24 - 1) {
            // 日格式
            dateFormat = "day";
        }
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        // 点位类型，如果设定diff，则求差值；否则取平均值。
        queryWrapper0.eq("sys_dict.dict_code", "point_type").eq("sys_dict_item.description", "diff");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select sys_dict_item.item_value from sys_dict inner join sys_dict_item on sys_dict.id = sys_dict_item.dict_id", queryWrapper0);
        List<String> diffPointList = queryMapList0.stream().map(item -> ConvertUtils.getString(item.get("item_value"))).collect(Collectors.toList());
        // 1. diff：查询当前时间最后一条数据
        String tableCur = "dcs_history_info_" + endTime.substring(0, 10).replaceAll("-", "");
        String sqlCur = diffSqlFunc(tableCur, startTime, endTime);
        QueryWrapper<?> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.orderByDesc("t1.create_time");
        List<Map<String, Object>> queryMapList1 = baseService.queryForList(sqlCur, queryWrapper1);
        if (queryMapList1.isEmpty()) {
            return;
        }
        Map<String, Map<String, Object>> eqmNo2EntityMap1 = queryMapList1.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("eqm_no")), item -> item, (v1, v2) -> v1));
        // 2. diff：查询前一段时间最后一条数据
        String sqlPrev = diffSqlFunc(viewName, DateUtils.sec2DateStr(DateUtils.strToDate(startTime, BaseConstant.dateFormat).getTime() / 1000 - 3600 * 24 * 32, BaseConstant.dateFormat), DateUtils.sec2DateStr(DateUtils.strToDate(startTime, BaseConstant.dateFormat).getTime() / 1000 - 1, BaseConstant.dateFormat));
        QueryWrapper<?> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("t1.eqm_no", eqmNo2EntityMap1.keySet()).orderByDesc("t1.create_time");
        List<Map<String, Object>> queryMapList2 = baseService.queryForList(sqlPrev, queryWrapper2);
        Map<String, Map<String, Object>> eqmNo2EntityMap2 = queryMapList2.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("eqm_no")), item -> item, (v1, v2) -> v1));
        // 3. 求均值
        QueryWrapper<?> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.ge("create_time", startTime).le("create_time", endTime).groupBy("eqm_no");
        List<Map<String, Object>> queryMapList3 = baseService.queryForList(avgSqlFunc(tableCur), queryWrapper3);
        Map<String, Map<String, Object>> eqmNo2EntityMap3 = queryMapList3.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("eqm_no")), item -> item, (v1, v2) -> v1));
        // 合并 diff 和 avg 结果
        String dataTypeVar, dataValueVar;
        Map<String, Object> insertMap;
        List<Map<String, Object>> insertMapList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : eqmNo2EntityMap1.entrySet()) {
            insertMap = new HashMap<>(entry.getValue());
            for (int i = 1; i <= 8; i++) {
                dataTypeVar = "datatype" + i;
                dataValueVar = "datavalue" + i;
                // 对所有数据值null处理
                insertMap.put(dataValueVar, null);
                // type=null，则忽略处理
                if (entry.getValue().get(dataTypeVar) == null) {
                    continue;
                }
                // 求差值
                if (diffPointList.contains(ConvertUtils.getString(entry.getValue().get(dataTypeVar)))) {
                    if (entry.getValue().get(dataValueVar) == null || eqmNo2EntityMap2.getOrDefault(entry.getKey(), new HashMap<>()).get(dataValueVar) == null) {
                        continue;
                    }
                    insertMap.put(dataValueVar, ConvertUtils.getDouble(entry.getValue().get(dataValueVar), 0D) - ConvertUtils.getDouble(eqmNo2EntityMap2.getOrDefault(entry.getKey(), new HashMap<>()).get(dataValueVar), 0D));
                } else {
                    // 求均值
                    if (eqmNo2EntityMap3.get(entry.getKey()).get(dataValueVar) == null) {
                        continue;
                    }
                    insertMap.put(dataValueVar, eqmNo2EntityMap3.get(entry.getKey()).get(dataValueVar));
                }
            }
            // 修改时间
            String createTime = DateUtils.date2Str((Date) entry.getValue().get("create_time"), BaseConstant.dateFormat);
            if ("hour".equals(dateFormat)) {
                createTime = createTime.substring(0, 14) + "00:00";
            } else {
                createTime = createTime.substring(0, 11) + " 00:00:00";
            }
            insertMap.put("create_time", DateUtils.strToDate(createTime, BaseConstant.dateFormat));
            insertMap.put("update_time", DateUtils.strToDate(createTime, BaseConstant.dateFormat));
            insertMapList.add(insertMap);
        }
        if (!insertMapList.isEmpty()) {
            baseService.insertBatch("dcs_history_info_" + dateFormat, insertMapList, com.sonin.modules.base.constant.BaseConstant.REPLACE);
        }
    }

    private String diffSqlFunc(String tableName, String startTime, String endTime) {
        String t1 = "(select * from " + tableName + " where create_time >= '" + startTime + "' and create_time <= '" + endTime + "') as t1";
        String t2 = "(SELECT eqm_no, MAX(create_time) as max_date FROM " + tableName + " where create_time >= '" + startTime + "' and create_time <= '" + endTime + "' GROUP BY eqm_no) as t2";
        return "select t1.* from " + t1 + " inner join " + t2 + " ON t1.eqm_no = t2.eqm_no AND t1.create_time = t2.max_date";
    }

    private String avgSqlFunc(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            stringBuilder.append(",sum(datavalue").append(i).append(") / count(*) as datavalue").append(i);
        }
        return "select eqm_no" + stringBuilder + " from " + tableName;
    }

}
