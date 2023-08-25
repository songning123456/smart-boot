package com.sonin.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;
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
        if (src == null) {
            return String.format("%." + n + "f", 0D);
        } else if (src instanceof Double) {
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
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*");
        // 科学计数法
        Pattern pattern2 = Pattern.compile("^[+-]?[\\d]+([.][\\d]*)?([Ee][+-]?[\\d]+)?$");
        if (StringUtils.isEmpty(str)) {
            return false;
        } else {
            Matcher isNum = pattern.matcher(str);
            Matcher isNum2 = pattern2.matcher(str);
            return isNum.matches() || isNum2.matches();
        }
    }

    /**
    * <pre>
    * 生成int类型的随机数 [min, max)
    * </pre>
     * @param min
     * @param max
    * @author sonin
    * @Description: TODO(这里描述这个方法的需求变更情况)
    */
    public static int intRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

}
