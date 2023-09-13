package com.sonin.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * Sql工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/9/13 10:16
 */
public class SqlUtils {

    private static final String EMPTY = "";

    private static final String SINGLE_QUOTES = "'";

    private static final String SPACE = " ";

    /**
     * <pre>
     * 解析QueryWrapper后缀where查询条件
     * </pre>
     *
     * @param queryWrapper
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static String wrapperSuffixSql(QueryWrapper<?> queryWrapper) {
        String suffixSql = queryWrapper.getCustomSqlSegment();
        Map<String, Object> paramNameValuePairs = queryWrapper.getParamNameValuePairs();
        Object value;
        for (Map.Entry<String, Object> item : paramNameValuePairs.entrySet()) {
            value = item.getValue();
            try {
                sqlInject(EMPTY + value);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (value instanceof String) {
                value = SINGLE_QUOTES + value + SINGLE_QUOTES;
            }
            suffixSql = suffixSql.replaceFirst("#\\{ew\\.paramNameValuePairs\\." + item.getKey() + "}", EMPTY + value);
        }
        return SPACE + suffixSql;
    }

    /**
     * 判断SQL注入
     *
     * @param param
     * @throws Exception
     */
    public static void sqlInject(String param) throws Exception {
        Pattern pattern = Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|')");
        Matcher matcher = pattern.matcher(param.toLowerCase());
        if (matcher.find()) {
            throw new Exception("SQL注入: " + param);
        }
    }

}
