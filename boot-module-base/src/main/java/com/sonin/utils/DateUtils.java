package com.sonin.utils;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * 日期工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:22
 */
public class DateUtils extends PropertyEditorSupport {

    // 各种时间格式
    public static final SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyy-MM");
    // 各种时间格式
    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    // 各种时间格式
    public static final SimpleDateFormat date_sdf_wz = new SimpleDateFormat("yyyy年MM月dd日");
    public static final SimpleDateFormat time_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat short_time_sdf = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 以毫秒表示的时间
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    private static final long SECOND_IN_MILLIS = 1000;

    // 指定模式的时间格式
    private static SimpleDateFormat getSDFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 当前日历，这里用中国时间表示
     *
     * @return 以当地时区表示的系统当前日历
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 根据(开始时间, 结束时间)获取每一天
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static List<String> getEveryday(String startTime, String endTime, String format) {
        List<String> dayList = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            List<Date> lDate = new ArrayList<>();
            lDate.add(startDate);
            Calendar calendarOfStart = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfStart.setTime(startDate);
            Calendar calendarOfEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfEnd.setTime(endDate);
            // 测试此日期是否在指定日期之后
            while (endDate.after(calendarOfStart.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calendarOfStart.add(Calendar.DAY_OF_MONTH, 1);
                lDate.add(calendarOfStart.getTime());
            }
            for (Date item : lDate) {
                dayList.add(sdf.format(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayList;
    }

    /**
     * 指定毫秒数表示的日历
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日历
     */
    public static Calendar getCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis));
        return cal;
    }

    /**
     * 当前日期
     *
     * @return 系统当前时间
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 指定毫秒数表示的日期
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日期
     */
    public static Date getDate(long millis) {
        return new Date(millis);
    }


    /**
     * 字符串转换时间戳
     *
     * @param str
     * @return
     */
    public static Timestamp str2Timestamp(String str) {
        Date date = str2Date(str, date_sdf);
        return new Timestamp(date.getTime());
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @param sdf
     * @return
     */
    public static Date str2Date(String str, SimpleDateFormat sdf) {
        if (null == str || "".equals(str)) {
            return null;
        }
        Date date;
        try {
            date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期转换为字符串
     *
     * @param date_sdf 日期格式
     * @return 字符串
     */
    public static String date2Str(SimpleDateFormat date_sdf) {
        Date date = getDate();
        return date_sdf.format(date);
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateFormat(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date _date = null;
        try {
            _date = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return simpleDateFormat.format(_date);
    }

    /**
     * 日期转换为字符串
     *
     * @param date     日期
     * @param date_sdf 日期格式
     * @return 字符串
     */
    public static String date2Str(Date date, SimpleDateFormat date_sdf) {
        if (null == date) {
            return null;
        }
        return date_sdf.format(date);
    }

    /**
     * 日期转换为字符串
     *
     * @param format 日期格式
     * @return 字符串
     */
    public static String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 指定毫秒数的时间戳
     *
     * @param millis 毫秒数
     * @return 指定毫秒数的时间戳
     */
    public static Timestamp getTimestamp(long millis) {
        return new Timestamp(millis);
    }

    /**
     * 以字符形式表示的时间戳
     *
     * @param time 毫秒数
     * @return 以字符形式表示的时间戳
     */
    public static Timestamp getTimestamp(String time) {
        return new Timestamp(Long.parseLong(time));
    }

    /**
     * 系统当前的时间戳
     *
     * @return 系统当前的时间戳
     */
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return datetimeFormat.format(getCalendar().getTime());
    }

    /**
     * 指定日期的时间戳
     *
     * @param date 指定日期
     * @return 指定日期的时间戳
     */
    public static Timestamp getTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 指定日历的时间戳
     *
     * @param cal 指定日历
     * @return 指定日历的时间戳
     */
    public static Timestamp getCalendarTimestamp(Calendar cal) {
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * 系统时间的毫秒数
     *
     * @return 系统时间的毫秒数
     */
    public static long getMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 指定日历的毫秒数
     *
     * @param cal 指定日历
     * @return 指定日历的毫秒数
     */
    public static long getMillis(Calendar cal) {
        return cal.getTime().getTime();
    }

    /**
     * 指定日期的毫秒数
     *
     * @param date 指定日期
     * @return 指定日期的毫秒数
     */
    public static long getMillis(Date date) {
        return date.getTime();
    }

    /**
     * 指定时间戳的毫秒数
     *
     * @param ts 指定时间戳
     * @return 指定时间戳的毫秒数
     */
    public static long getMillis(Timestamp ts) {
        return ts.getTime();
    }


    /**
     * <pre>
     * 	获取指定月份的天数
     * </pre>
     *
     * @param yearMonth
     * @return
     * @author Li Yuanyuan, 2021年1月7日 下午5:33:59
     */
    public static int getDayCountOfMonth(String yearMonth) {
        Calendar calendar = new GregorianCalendar();
        Date date1 = null;
        try {
            date1 = yyyyMM.parse(yearMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date1); //放入你的日期
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * <pre>
     * 	获取今天日期或月份【yyyy-MM-dd】
     * </pre>
     *
     * @param type day日期，month月份
     * @return
     * @author Li Yuanyuan, 2021年1月7日 下午2:45:15
     */
    public static String getTodayOrMonthDate(String type) {
        if (type.equals("day")) {
            return date_sdf.format(getCalendar().getTime());
        } else {
            return yyyyMM.format(getCalendar().getTime());
        }
    }

    /**
     * <pre>
     * 	获取指定日期的第一天或最后一天
     * </pre>
     *
     * @param date 指定日期，格式为yyyy-MM-dd或yyyy-MM
     * @param type 指定类型，start获取第一天，end获取最后一天
     * @return
     * @author Li Yuanyuan, 2021年1月7日 下午2:34:16
     */
    public static String getMonthFirstOrLastDay(String date, String type) {
        if (type.equals("start")) {
            //获取指定日期的第一天
            return date.substring(0, 7) + "-01";
        } else {
            //获取指定日期的最后一天
            return getLastDayOfMonth(date);
        }
    }

    /**
     * 对指定日期时间进行加减运算
     *
     * @param src          日期时间字符串
     * @param pattern      格式
     * @param calendarType 日期类型
     * @param amount       值
     * @return
     * @throws ParseException
     */
    public static String formatAddTime(String src, String pattern, int calendarType, int amount) throws ParseException {
        Calendar cal;
        cal = parseCalendar(src, pattern);
        cal.add(calendarType, amount);
        return formatDate(cal, pattern);
    }

    /**
     * 格式化日期
     * - yyyy-MM-dd HH:mm:ss
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        return sd.format(date);
    }

    /**
     * 格式化日期
     * - yyyy-MM-dd HH:mm:ss
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return 日期
     * @throws ParseException 解析异常
     */
    public static Date parse(String date, String pattern) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        try {
            Date date1 = sd.parse(date);
            return date1;
        } catch (ParseException e) {
            throw e;
        }
    }

    public static String getDateFirstTime(String dateStr, String type) throws ParseException {

        String format;
        Date date;
        Calendar ca = Calendar.getInstance();
        if ("year".equals(type)) {
            date = parse(dateStr, "yyyy");
            ca.setTime(date);
            ca.set(Calendar.MONTH, 0);
            ca.set(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
        } else if ("month".equals(type)) {
            date = parse(dateStr, "yyyy-MM");
            ca.setTime(date);
            ca.set(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
        } else if ("day".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd");
            ca.setTime(date);
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
        } else if ("hour".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd HH");
            ca.setTime(date);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
        }
        format = format(ca.getTime(), "yyyy-MM-dd HH:mm:ss");
        return format;
    }

    public static String getDateLastTime(String dateStr, String type) throws ParseException {

        String format;
        Date date;
        Calendar ca = Calendar.getInstance();
        if ("year".equals(type)) {
            date = parse(dateStr, "yyyy");
            ca.setTime(date);
            ca.set(Calendar.MONTH, ca.getActualMaximum(Calendar.MONTH));
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            ca.set(Calendar.HOUR_OF_DAY, ca.getActualMaximum(Calendar.HOUR_OF_DAY));
            ca.set(Calendar.MINUTE, ca.getActualMaximum(Calendar.MINUTE));
            ca.set(Calendar.SECOND, ca.getActualMaximum(Calendar.SECOND));
        } else if ("month".equals(type)) {
            date = parse(dateStr, "yyyy-MM");
            ca.setTime(date);
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            ca.set(Calendar.HOUR_OF_DAY, ca.getActualMaximum(Calendar.HOUR_OF_DAY));
            ca.set(Calendar.MINUTE, ca.getActualMaximum(Calendar.MINUTE));
            ca.set(Calendar.SECOND, ca.getActualMaximum(Calendar.SECOND));
        } else if ("day".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd");
            ca.setTime(date);
            ca.set(Calendar.HOUR_OF_DAY, ca.getActualMaximum(Calendar.HOUR_OF_DAY));
            ca.set(Calendar.MINUTE, ca.getActualMaximum(Calendar.MINUTE));
            ca.set(Calendar.SECOND, ca.getActualMaximum(Calendar.SECOND));
        } else if ("hour".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd HH");
            ca.setTime(date);
            ca.set(Calendar.MINUTE, ca.getActualMaximum(Calendar.MINUTE));
            ca.set(Calendar.SECOND, ca.getActualMaximum(Calendar.SECOND));
        }
        format = format(ca.getTime(), "yyyy-MM-dd HH:mm:ss");
        return format;
    }

    /**
     * 日期范围 - 切片
     * <pre>
     * -- eg:
     * 年 ----------------------- sliceUpDateRange("2018", "2020");
     * rs: [2018, 2019, 2020]
     *
     * 月 ----------------------- sliceUpDateRange("2018-06", "2018-08");
     * rs: [2018-06, 2018-07, 2018-08]
     *
     * 日 ----------------------- sliceUpDateRange("2018-06-30", "2018-07-02");
     * rs: [2018-06-30, 2018-07-01, 2018-07-02]
     * </pre>
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 切片日期
     */
    public static List<String> sliceUpDateRange(String startDate, String endDate, String type) {
        List<String> rs = new ArrayList<>();
        try {
            int dt = Calendar.DATE;
            String pattern = "yyyy-MM-dd";
            if ("year".equals(type)) {
                pattern = "yyyy-MM";
                dt = Calendar.MONTH;
            } else if ("month".equals(type)) {
                pattern = "yyyy-MM-dd";
                dt = Calendar.DATE;
            } else if ("day".equals(type)) {
                pattern = "yyyy-MM-dd HH:mm:ss";
                dt = Calendar.HOUR;
            }
            Calendar sc = Calendar.getInstance();
            Calendar ec = Calendar.getInstance();
            sc.setTime(parse(startDate, pattern));
            ec.setTime(parse(endDate, pattern));
            while (sc.compareTo(ec) < 1) {
                rs.add(format(sc.getTime(), pattern));
                sc.add(dt, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author xiechao
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    public static String formatDate() {
        return date_sdf.format(getCalendar().getTime());
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 默认日期按“yyyy-MM-dd HH:mm:ss“格式显示
     */
    public static String formatDateTime() {
        return datetimeFormat.format(getCalendar().getTime());
    }

    /**
     * 获取时间字符串
     */
    public static String getDataString(SimpleDateFormat formatStr) {
        return formatStr.format(getCalendar().getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日
     *
     * @param cal 指定的日期
     * @return 指定日期按“年-月-日“格式显示
     */
    public static String formatDate(Calendar cal) {
        return date_sdf.format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日“格式显示
     */
    public static String formatDate(Date date) {
        return date_sdf.format(date);
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日“格式显示
     */
    public static String formatDate(long millis) {
        return date_sdf.format(new Date(millis));
    }

    /**
     * 默认日期按指定格式显示
     *
     * @param pattern 指定的格式
     * @return 默认日期按指定格式显示
     */
    public static String formatDate(String pattern) {
        return getSDFormat(pattern).format(getCalendar().getTime());
    }

    /**
     * 指定日期按指定格式显示
     *
     * @param cal     指定的日期
     * @param pattern 指定的格式
     * @return 指定日期按指定格式显示
     */
    public static String formatDate(Calendar cal, String pattern) {
        return getSDFormat(pattern).format(cal.getTime());
    }

    /**
     * 指定日期按指定格式显示
     *
     * @param date    指定的日期
     * @param pattern 指定的格式
     * @return 指定日期按指定格式显示
     */
    public static String formatDate(Date date, String pattern) {
        return getSDFormat(pattern).format(date);
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：年-月-日 时：分
     *
     * @return 默认日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime() {
        return time_sdf.format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(long millis) {
        return time_sdf.format(new Date(millis));
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日 时：分：秒
     *
     * @param millis
     * @return
     */
    public static String formatSecondTime(long millis) {
        return datetimeFormat.format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Calendar cal) {
        return time_sdf.format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Date date) {
        return time_sdf.format(date);
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：时：分
     *
     * @return 默认日期按“时：分“格式显示
     */
    public static String formatShortTime() {
        return short_time_sdf.format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“时：分“格式显示
     */
    public static String formatShortTime(long millis) {
        return short_time_sdf.format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Calendar cal) {
        return short_time_sdf.format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Date date) {
        return short_time_sdf.format(date);
    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Date parseDate(String src, String pattern) throws ParseException {
        return getSDFormat(pattern).parse(src);

    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Calendar parseCalendar(String src, String pattern) throws ParseException {
        Date date = parseDate(src, pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String formatAddDate(String src, String pattern, int amount) throws ParseException {
        Calendar cal;
        cal = parseCalendar(src, pattern);
        cal.add(Calendar.DATE, amount);
        return formatDate(cal);
    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的时间戳
     * @throws ParseException
     */
    public static Timestamp parseTimestamp(String src, String pattern) throws ParseException {
        Date date = parseDate(src, pattern);
        return new Timestamp(date.getTime());
    }

    /**
     * 计算两个时间之间的差值，根据标志的不同而不同
     *
     * @param flag   计算标志，表示按照年/月/日/时/分/秒等计算
     * @param calSrc 减数
     * @param calDes 被减数
     * @return 两个日期之间的差值
     */
    public static int dateDiff(char flag, Calendar calSrc, Calendar calDes) {
        long millisDiff = getMillis(calSrc) - getMillis(calDes);
        if (flag == 'y') {
            return (calSrc.get(Calendar.YEAR) - calDes.get(Calendar.YEAR));
        }
        if (flag == 'd') {
            return (int) (millisDiff / DAY_IN_MILLIS);
        }
        if (flag == 'h') {
            return (int) (millisDiff / HOUR_IN_MILLIS);
        }
        if (flag == 'm') {
            return (int) (millisDiff / MINUTE_IN_MILLIS);
        }
        if (flag == 's') {
            return (int) (millisDiff / SECOND_IN_MILLIS);
        }
        return 0;
    }

    /**
     * String类型 转换为Date, 如果参数长度为10 转换格式”yyyy-MM-dd“ 如果参数长度为19 转换格式”yyyy-MM-dd
     * HH:mm:ss“ * @param text String类型的时间值
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            try {
                if (!text.contains(":") && text.length() == 10) {
                    setValue(date_sdf.parse(text));
                } else if (text.indexOf(":") > 0 && text.length() == 19) {
                    setValue(datetimeFormat.parse(text));
                } else {
                    throw new IllegalArgumentException("Could not parse date, date format is error ");
                }
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        } else {
            setValue(null);
        }
    }

    public static int getYear() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(getDate());
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取指定月份的最后一天
     *
     * @param yearMonth
     * @return
     */
    public static String getLastDayOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);// 设置年份
        // 设置月份
        cal.set(Calendar.MONTH, month); //设置当前月的上一个月
        // 获取某月最大天数
        int lastDay = cal.getMinimum(Calendar.DATE); //获取月份中的最小值，即第一天
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //上月的第一天减去1就是当月的最后一天
        return date_sdf.format(cal.getTime());
    }

    /**
     * 获取日期相隔天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long dayDiff(String start, String end) {
        long diff;
        try {
            long d1 = date_sdf.parse(start).getTime();
            long d2 = date_sdf.parse(end).getTime();
            diff = (d2 - d1) / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            return 0L;
        }
        return diff;
    }

    /**
     * 获取日期相隔秒数，添加锁，避免并发时计算错误
     *
     * @param start
     * @param end
     * @return
     */
    public synchronized static long secondDiff(String start, String end) {
        long diff;
        try {
            long d1 = datetimeFormat.parse(start).getTime();
            long d2 = datetimeFormat.parse(end).getTime();
            diff = (d2 - d1) / 1000;
        } catch (Exception e) {
            return 0L;
        }
        return diff;
    }

    /**
     * 获取去年同期月份
     *
     * @param yearMonth
     * @return
     */
    public static String getLastYearMonthOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        String month = yearMonth.split("-")[1]; //月
        int lastYear = year - 1;
        return lastYear + "-" + month;
    }

    /**
     * 获取上月月份
     *
     * @param yearMonth
     * @return
     */
    public static String getLastMonthOfMonth(String yearMonth) {
        try {
            Date currDate = yyyyMM.parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            return yyyyMM.format(calendar.getTime());
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 获取上一年当前月份
     *
     * @param yearMonth
     * @return
     */
    public static String getPreviousYear(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year - 1); // 设置年份上一年
        // 设置月份
        cal.set(Calendar.MONTH, month - 1); //设置当前月
        return yyyyMM.format(cal.getTime());
    }

    /**
     * 获取前一天字符串
     *
     * @return
     * @auther
     */
    public static String getLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        return date_sdf.format(cal.getTime());
    }

    /**
     * <pre>
     * 	获取昨天yyyy-MM-dd
     * </pre>
     *
     * @return
     * @author Li Yuanyuan, 2020年12月27日 下午7:37:22
     */
    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        return date_sdf.format(cal.getTime());
    }

    /**
     * <pre>
     * 	获取前天yyyy-MM-dd
     * </pre>
     *
     * @return
     * @throws ParseException
     * @author Li Yuanyuan, 2020年12月27日 下午7:39:47
     */
    public static String getBeforeYesterday() throws ParseException {
        return formatAddTime(getYesterday(), "yyyy-MM-dd", Calendar.DATE, -1);
    }

}
