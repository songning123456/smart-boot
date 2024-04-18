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
        String schema = masterUrl.substring(masterUrl.lastIndexOf("/") + 1, masterUrl.indexOf("?"));
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
            dateFormat = "day";
        }
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        // 点位类型，如果设定diff，则求差值；否则就直接取最新一条数据。
        queryWrapper0.eq("sys_dict.dict_code", "point_type").eq("sys_dict_item.description", "diff");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select sys_dict_item.item_value from sys_dict inner join sys_dict_item on sys_dict.id = sys_dict_item.dict_id", queryWrapper0);
        List<String> diffPointList = queryMapList0.stream().map(item -> ConvertUtils.getString(item.get("item_value"))).collect(Collectors.toList());
        // 查询当前时间最后一条数据
        String tableCur = "dcs_history_info_" + endTime.substring(0, 10).replaceAll("-", "");
        String sqlCur = sqlFunc(tableCur);
        QueryWrapper<?> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.ge("t1.create_time", startTime).le("t1.create_time", endTime);
        List<Map<String, Object>> queryMapList1 = baseService.queryForList(sqlCur, queryWrapper1);
        if (queryMapList1.isEmpty()) {
            return;
        }
        List<String> eqmNoList = queryMapList1.stream().map(item -> ConvertUtils.getString(item.get("eqm_no"))).collect(Collectors.toList());
        // 查询前一段时间最后一条数据
        String sqlPrev = sqlFunc(viewName);
        QueryWrapper<?> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lt("t1.create_time", startTime).in("t1.eqm_no", eqmNoList).orderByDesc("t1.create_time");
        List<Map<String, Object>> queryMapList2 = baseService.queryForList(sqlPrev, queryWrapper2);
        Map<String, Map<String, Object>> eqmNo2EntityMap2 = queryMapList2.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("eqm_no")), item -> item, (v1, v2) -> v1));
        String eqmNo, dataTypeVar, dataType, dataValueVar;
        double dataValueCur, dataValuePrev;
        Map<String, Object> insertMap;
        List<Map<String, Object>> insertMapList = new ArrayList<>();
        for (Map<String, Object> item : queryMapList1) {
            insertMap = new HashMap<>(item);
            // 求diff
            eqmNo = ConvertUtils.getString(item.get("eqm_no"));
            for (int i = 1; i <= 8; i++) {
                dataTypeVar = "datatype" + i;
                dataType = ConvertUtils.getString(item.get(dataTypeVar));
                if (diffPointList.contains(dataType)) {
                    dataValueVar = "datavalue" + i;
                    dataValueCur = ConvertUtils.getDouble(item.get(dataValueVar), 0D);
                    dataValuePrev = ConvertUtils.getDouble(eqmNo2EntityMap2.getOrDefault(eqmNo, new HashMap<>()).getOrDefault(dataValueVar, "0"), 0D);
                    insertMap.put(dataType, dataValueCur - dataValuePrev);
                }
            }
            // 修改时间
            String createTime = DateUtils.date2Str((Date) item.get("create_time"), BaseConstant.dateFormat);
            if ("hour".equals(dateFormat)) {
                createTime = createTime.substring(0, 15) + "00:00";
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

    private String sqlFunc(String tableName) {
        return "SELECT t1.* FROM " + tableName + " t1 INNER JOIN ( SELECT eqm_no, MAX(create_time) as max_date FROM " + tableName + " GROUP BY eqm_no ) t2 ON t1.eqm_no = t2.eqm_no AND t1.create_time = t2.max_date";
    }

}
