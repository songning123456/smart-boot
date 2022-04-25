package com.sonin.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 字符串工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 13:25
 */
public class StrUtils {

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return (true);
        }
        if ("".equals(object)) {
            return (true);
        }
        return "null".equals(object);
    }

    public static boolean isNotEmpty(Object object) {
        return object != null && !object.equals("") && !object.equals("null");
    }

    public static String decode(String strIn, String sourceCode, String targetCode) {
        return code2code(strIn, sourceCode, targetCode);
    }

    public static String StrToUTF(String strIn, String sourceCode, String targetCode) {
        strIn = "";
        try {
            strIn = new String(strIn.getBytes(StandardCharsets.ISO_8859_1), "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strIn;
    }

    private static String code2code(String strIn, String sourceCode, String targetCode) {
        String strOut;
        if (strIn == null || (strIn.trim()).equals("")) {
            return strIn;
        }
        try {
            byte[] b = strIn.getBytes(sourceCode);
            for (byte value : b) {
                System.out.print(value + "  ");
            }
            strOut = new String(b, targetCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strOut;
    }

    public static int getInt(String s, int defaultVal) {
        if (s == null || s.equals("")) {
            return (defaultVal);
        }
        try {
            return (Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return (defaultVal);
        }
    }

    public static int getInt(String s) {
        if (s == null || s.equals("")) {
            return 0;
        }
        try {
            return (Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int getInt(String s, Integer df) {
        if (s == null || s.equals("")) {
            return df;
        }
        try {
            return (Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer[] getInts(String[] s) {
        if (s == null) {
            return null;
        }
        Integer[] integer = new Integer[s.length];
        for (int i = 0; i < s.length; i++) {
            integer[i] = Integer.parseInt(s[i]);
        }
        return integer;
    }

    public static double getDouble(Object s, double defaultVal) {
        if (isEmpty(s)) {
            return (defaultVal);
        }
        try {
            return (Double.parseDouble(s.toString()));
        } catch (NumberFormatException e) {
            return (defaultVal);
        }
    }

    public static double getDouble(String s, double defaultVal) {
        if (s == null || s.equals("")) {
            return (defaultVal);
        }
        try {
            return (Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return (defaultVal);
        }
    }

    public static double getDouble(Double s, double defaultVal) {
        if (s == null) {
            return (defaultVal);
        }
        return s;
    }

    public static int getInt(Object object, int defaultVal) {
        if (isEmpty(object)) {
            return (defaultVal);
        }
        try {
            return (Integer.parseInt(object.toString()));
        } catch (NumberFormatException e) {
            return (defaultVal);
        }
    }

    public static Integer getInt(Object object) {
        if (isEmpty(object)) {
            return null;
        }
        try {
            return (Integer.parseInt(object.toString()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int getInt(BigDecimal s, int defaultVal) {
        if (s == null) {
            return (defaultVal);
        }
        return s.intValue();
    }

    public static Integer[] getIntegerArray(String[] object) {
        int len = object.length;
        Integer[] result = new Integer[len];
        try {
            for (int i = 0; i < len; i++) {
                result[i] = new Integer(object[i].trim());
            }
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getString(String s) {
        return (getString(s, ""));
    }

    /**
     * 转义成Unicode编码
     */
    public static String getString(Object object) {
        if (isEmpty(object)) {
            return "";
        }
        String result = object.toString().trim();
        return (result.equals("null") ? "" : result);
    }

    public static String getString(int i) {
        return (String.valueOf(i));
    }

    public static String getString(float i) {
        return (String.valueOf(i));
    }

    public static String getString(String s, String defval) {
        if (isEmpty(s)) {
            return (defval);
        }
        return (s.trim());
    }

    public static String getString(Object s, String defval) {
        if (isEmpty(s)) {
            return (defval);
        }
        return (s.toString().trim());
    }

    public static long string2Long(String str) {
        long test = 0L;
        try {
            test = Long.parseLong(str);
        } catch (Exception ignored) {
        }
        return test;
    }

    /**
     * java去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;

    }

    /**
     * 判断元素是否在数组内
     *
     * @param substring
     * @param source
     * @return
     */
    public static boolean isIn(String substring, String[] source) {
        if (source == null || source.length == 0) {
            return false;
        }
        for (String aSource : source) {
            if (aSource.equals(substring)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <pre>
     * 	判断字符串是否是数字
     * </pre>
     *
     * @param str
     * @return
     * @author Li Yuanyuan, 2020年5月19日 下午5:18:35
     */
    public static boolean isNumeric(String str) {
        // Pattern pattern = Pattern.compile("^-?[0-9]+"); //这个也行
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");//这个也行
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * <pre>
     * 保留指定位数有效数字
     * </pre>
     *
     * @param val
     * @param digit
     * @return
     * @author Li Yuanyuan, 2021年5月14日 下午5:00:35
     */
    public static String getEffectiveVal(String val, int digit) {
        BigDecimal b = new BigDecimal(val);
        BigDecimal divisor = BigDecimal.ONE;
        MathContext mc = new MathContext(digit);
        String handleVal = String.valueOf(b.divide(divisor, mc));
        // 情况1，只有整数
        if (!handleVal.contains(".") && handleVal.length() < digit) {
            StringBuilder zeroStr = new StringBuilder(".");
            for (int i = handleVal.length(); i < digit; i++) {
                zeroStr.append("0");
            }
            handleVal = handleVal + zeroStr;
        } else if (!handleVal.startsWith("0.") && handleVal.contains(".") && handleVal.length() <= digit) {
            // 情况2，有小数点
            StringBuilder zeroStr = new StringBuilder();
            for (int i = handleVal.length(); i <= digit; i++) {
                zeroStr.append("0");
            }
            handleVal = handleVal + zeroStr;
        } else if (handleVal.startsWith("0.") && (handleVal.length() - 2) < digit) {
            // 情况2，有小数点
            StringBuilder zeroStr = new StringBuilder();
            for (int i = handleVal.length() - 2; i < digit; i++) {
                zeroStr.append("0");
            }
            handleVal = handleVal + zeroStr;
        }
        return handleVal;
    }

    /**
     * <pre>
     * 将double格式化为指定小数位的String，不足小数位用0补全
     * </pre>
     *
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     * @return
     * @author Li Yuanyuan, 2021年11月15日 上午10:50:42
     */
    public static String roundByScale(double v, int scale) {
        if ("NaN".equals(String.valueOf(v))) {
            v = 0;
        }
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        StringBuilder formatStr = new StringBuilder("0.");
        for (int i = 0; i < scale; i++) {
            formatStr.append("0");
        }
        return new DecimalFormat(formatStr.toString()).format(v);
    }

}
