package com.sonin.modules.historydata.service.impl;

import com.sonin.core.context.SpringContext;
import com.sonin.modules.historydata.service.IHistoryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/22 11:27
 */
@Service
@Slf4j
public class HistoryDataServiceImpl implements IHistoryDataService {

    @Override
    public Double queryDiffForDay(String code, String yearMonthDay) {
        double val = 0;
        String startTime = yearMonthDay + " 00:00:00";
        String endTime = yearMonthDay + " 23:59:59";
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean("taos-db");
        String sql = "select ts, tag_name, tag_value from qxdb.historydata where tag_name in('" + code + "')" + " and ts >= '" + startTime + "' and ts < '" + endTime + "' order by ts asc";
        try {
            List<Map<String, Object>> queryMapList = jdbcTemplate.queryForList(sql);
            if (queryMapList.size() >= 2) {
                val = (Float) queryMapList.get(queryMapList.size() - 1).get("tag_value") - (Float) queryMapList.get(0).get("tag_value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

}
