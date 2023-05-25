package com.sonin.utils;

import com.sonin.core.callback.IPageQueryCallback;
import com.sonin.core.context.SpringContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 分页工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:22
 */
public class PageUtils {

    public static <T> List<T> page(int pageNo, int pageSize, List<T> totalList) {
        List<T> pageList = new ArrayList<>();
        for (int i = (pageNo - 1) * pageSize; i < totalList.size() && i < pageNo * pageSize; i++) {
            pageList.add(totalList.get(i));
        }
        return pageList;
    }

    public static void query(String DBName, String querySql, IPageQueryCallback iPageQueryCallback) {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean(StringUtils.isEmpty(DBName) ? "master" : DBName);
        String countSql = "select count(*) as total from (" + querySql + ") as tmp";
        Map<String, Object> countMap = jdbcTemplate.queryForMap(countSql);
        // 总数
        int total = Integer.parseInt(String.valueOf(countMap.get("total")));
        // 分页大小
        int pageSize = 1000;
        // 总页数
        long pageCount = (long) Math.ceil(1.0 * total / pageSize);
        for (int i = 1; i <= pageCount; i++) {
            String lastSql = " limit " + (i - 1) * pageSize + " , " + pageSize;
            List<Map<String, Object>> queryMapList = jdbcTemplate.queryForList(querySql + lastSql);
            iPageQueryCallback.execute(queryMapList);
        }
    }

}
