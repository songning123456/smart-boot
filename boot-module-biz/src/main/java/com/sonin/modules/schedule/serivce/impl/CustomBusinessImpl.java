package com.sonin.modules.schedule.serivce.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.schedule.serivce.ICustomBusiness;
import com.sonin.utils.DateUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/12/18 14:40
 */
@Slf4j
@Service
public class CustomBusinessImpl implements ICustomBusiness {

    @Autowired
    private IBaseService baseService;

    /**
     * <pre>
     * 生成小时数据
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Override
    public void generateHourDataFunc(String startTime, String endTime) {
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from sys_monitor_metric_info_data", new QueryWrapper<>());
        if (queryMapList0.isEmpty()) {
            return;
        }
        Map<String, Map<String, Object>> nm2EntityMap0 = queryMapList0.stream().collect(Collectors.toMap(item -> StrUtils.getString(item.get("id")), item -> item, (v1, v2) -> v2));
        List<String> timeList = DateUtils.intervalByHour(startTime, endTime, BaseConstant.dateFormat.substring(0, 14));
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        for (String time : timeList) {
            String tableSuffix = time.substring(0, 10).replaceAll("-", "");
            String tmpStartTime = time + "00:00";
            String tmpStartTs = StrUtils.getString(DateUtils.dateStr2Sec(tmpStartTime, BaseConstant.dateFormat));
            String tmpEndTime = time + "59:59";
            String tmpEndTs = StrUtils.getString(DateUtils.dateStr2Sec(tmpEndTime, BaseConstant.dateFormat));
            Map<String, List<String>> nm2ValListMap = new LinkedHashMap<>();
            // 获取nm的集合
            StringBuilder nmSB = new StringBuilder();
            for (String nm : nm2EntityMap0.keySet()) {
                nmSB.append(",'").append(nm).append("'");
            }
            String nmCondition = " nm in (" + nmSB.toString().replaceFirst(",", "") + ")";
            // 查询这一个小时的所有数据(按照时间升序)
            List<Map<String, Object>> queryMapList = pgDB.queryForList("select nm, v, ts, factoryname, type from xsinsert" + tableSuffix + " where " + nmCondition + " and ts >= ? and ts <= ? order by ts asc", tmpStartTs, tmpEndTs);
            for (Map<String, Object> item : queryMapList) {
                String nm = StrUtils.getString(item.get("nm"));
                String v = StrUtils.getString(item.get("v"));
                nm2ValListMap.putIfAbsent(nm, new ArrayList<>());
                nm2ValListMap.get(nm).add(v);
            }
            // 插入数据(挨个插入nm)
            for (String nm : nm2EntityMap0.keySet()) {
                String tableNamePrefix = StrUtils.getString(nm2EntityMap0.get(nm).get("table_prefix"));
                String calculateType = StrUtils.getString(nm2EntityMap0.get(nm).get("calculate_type"));
                // 不存在则直接跳过
                if (StringUtils.isEmpty(tableNamePrefix)) {
                    continue;
                }
                String tableName = tableNamePrefix.toLowerCase() + "_count";
                List<String> valList = nm2ValListMap.get(nm);
                Double val = null;
                if ("3".equals(calculateType) && valList.size() > 1) {
                    // 求差
                    val = StrUtils.getDouble(valList.get(valList.size() - 1), 0D) - StrUtils.getDouble(valList.get(0), 0D);
                } else if ("4".equals(calculateType)) {
                    // 求平均
                    val = valList.stream().mapToDouble(item -> StrUtils.getDouble(item, 0D)).average().getAsDouble();
                }
                if (val != null) {
                    // 先查询此nm, ts是否有值，无值才补录
                    Map<String, Object> countQueryMap = pgDB.queryForMap("select count(*) as total from " + tableName + " where nm = ? and ts = ?", nm, tmpStartTs);
                    double total = StrUtils.getDouble(countQueryMap.get("total"), 0D);
                    if (total == 0) {
                        try {
                            pgDB.update("insert into " + tableName + "(nm, v, ts, createtime, factoryname, devicename, type, gatewaycode) values(?, ?, ?, ?, ?, ?, ?, ?)", nm, val, tmpStartTs, Long.parseLong(tmpStartTs), tableNamePrefix, "custom", calculateType, tableNamePrefix);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
