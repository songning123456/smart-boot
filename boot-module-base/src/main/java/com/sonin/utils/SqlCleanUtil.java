package com.sonin.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlCleanUtil {

    // SQL 注入关键字正则
    private static final String SQL_REGEX =
            "(?i)" +                      // 忽略大小写
                    "(?:')|" +                    // 单引号 '
                    "(?:--)|" +                   // 注释 --
                    "(?:/\\*)|" +                 // /* 注释开始
                    "(?:\\*/)|" +                 // */ 注释结束
                    "(select|update|delete|insert|drop|alter|truncate|exec|union|from|where| or | and )|" +
                    "(;)|(%27)|(%23)|(%2D%2D)";   // URL 编码形式

    public static String clean(String value, String url, String key) {
        if (value == null) {
            return null;
        }
        String cleanValue = value.replaceAll(SQL_REGEX, "");
        if (!value.equals(cleanValue)) {
            log.error("【SQL 清洗】URL: {} 参数 [{}] 原值=[{}] → 清洗后=[{}]", url, key, value, cleanValue);
        }
        return cleanValue;
    }
}
