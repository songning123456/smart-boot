package com.sonin.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 数字工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:27
 */
public class DigitalUtils {

    /**
     * 计算保留n位小数
     *
     * @return
     */
    public static String nPoint(Object src, int n) {
        if (src instanceof Double) {
            return String.format("%." + n + "f", src);
        } else if (src instanceof String) {
            if (isNumeric(src.toString())) {
                return String.format("%." + n + "f", Double.parseDouble("" + src));
            } else {
                return String.format("%." + n + "f", 0D);
            }
        }
        return "" + src;
    }

    /**
     * 判断字符串是否是数字类型
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        if (StringUtils.isEmpty(str)) {
            return false;
        } else {
            Matcher isNum = pattern.matcher(str);
            return isNum.matches();
        }
    }

}
