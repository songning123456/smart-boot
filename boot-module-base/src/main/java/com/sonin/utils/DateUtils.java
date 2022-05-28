package com.sonin.utils;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
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

    // 以ms表示的时间
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    // 以hour表示的时间
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    // 以minute表示的时间
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    // 以second表示的时间
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
     * 根据(开始时间, 结束时间)获取每一个时间间隔的数据, 主要用于eCharts图标xData
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static List<String> interval(String startTime, String endTime, String format, int field, int amount) {
        List<String> intervalList = new ArrayList<>();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);
            List<Date> dateList = new ArrayList<>();
            dateList.add(startDate);
            Calendar calendarOfStart = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfStart.setTime(startDate);
            Calendar calendarOfEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfEnd.setTime(endDate);
            // 测试此日期是否在指定日期之后
            while (endDate.compareTo(calendarOfStart.getTime()) >= 0) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calendarOfStart.add(field, amount);
                if (endDate.compareTo(calendarOfStart.getTime()) >= 0 && !dateList.contains(calendarOfStart.getTime())) {
                    dateList.add(calendarOfStart.getTime());
                }
            }
            for (Date item : dateList) {
                intervalList.add(simpleDateFormat.format(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intervalList;
    }

    public static List<String> intervalByHour(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.HOUR_OF_DAY, 1);
    }

    public static List<String> intervalByDay(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.DAY_OF_MONTH, 1);
    }

    public static List<String> intervalByMonth(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.MONTH, 1);
    }

    public static List<String> intervalByYear(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.YEAR, 1);
    }

    /**
     * 根据某个时间获取前多少天，或者后多少天
     *
     * @param dateTime yyyy-MM-dd
     * @param limit    天数
     * @return
     */
    public static List<String> increaseDay(String dateTime, int limit) {
        return stepDay(dateTime, limit, true);
    }

    /**
     * 根据某个时间获取前多少天，或者后多少天
     *
     * @param dateTime yyyy-MM-dd
     * @param limit    天数
     * @return
     */
    public static List<String> decreaseDay(String dateTime, int limit) {
        return stepDay(dateTime, limit, false);
    }

    private static List<String> stepDay(String dateTime, int limit, boolean flag) {
        LinkedList<String> dateList = new LinkedList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int factor = 1;
        if (!flag) {
            factor = -1;
        }
        for (int i = 0; i < limit; i++) {
            Date date = simpleDateFormat.parse(dateTime, new ParsePosition(0));
            calendar.setTime(date);
            calendar.add(Calendar.DATE, factor * i);
            if (flag) {
                dateList.addLast(simpleDateFormat.format(calendar.getTime()));
            } else {
                dateList.addFirst(simpleDateFormat.format(calendar.getTime()));
            }
        }
        return dateList;
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
        Date date = str2Date(str, new SimpleDateFormat("yyyy-MM-dd"));
        return new Timestamp(date.getTime());
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @param simpleDateFormat
     * @return
     */
    public static Date str2Date(String str, SimpleDateFormat simpleDateFormat) {
        if (null == str || "".equals(str)) {
            return null;
        }
        Date date;
        try {
            date = simpleDateFormat.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期转换为字符串
     *
     * @param simpleDateFormat 日期格式
     * @return 字符串
     */
    public static String date2Str(SimpleDateFormat simpleDateFormat) {
        Date date = getDate();
        return simpleDateFormat.format(date);
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
     * @param date             日期
     * @param simpleDateFormat 日期格式
     * @return 字符串
     */
    public static String date2Str(Date date, SimpleDateFormat simpleDateFormat) {
        if (null == date) {
            return null;
        }
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转换为字符串
     *
     * @param format 日期格式
     * @return 字符串
     */
    public static String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
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
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getCalendar().getTime());
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
     * @param calendar 指定日历
     * @return 指定日历的时间戳
     */
    public static Timestamp getCalendarTimestamp(Calendar calendar) {
        return new Timestamp(calendar.getTime().getTime());
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
     * @param calendar 指定日历
     * @return 指定日历的毫秒数
     */
    public static long getMillis(Calendar calendar) {
        return calendar.getTime().getTime();
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
     * @param timestamp 指定时间戳
     * @return 指定时间戳的毫秒数
     */
    public static long getMillis(Timestamp timestamp) {
        return timestamp.getTime();
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
            date1 = new SimpleDateFormat("yyyy-MM").parse(yearMonth);
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
            return new SimpleDateFormat("yyyy-MM-dd").format(getCalendar().getTime());
        } else {
            return new SimpleDateFormat("yyyy-MM").format(getCalendar().getTime());
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
        Calendar calendar;
        calendar = parseCalendar(src, pattern);
        calendar.add(calendarType, amount);
        return formatDate(calendar, pattern);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw e;
        }
    }

    public static String getDateFirstTime(String dateStr, String type) throws ParseException {

        String format;
        Date date;
        Calendar calendar = Calendar.getInstance();
        if ("year".equals(type)) {
            date = parse(dateStr, "yyyy");
            calendar.setTime(date);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else if ("month".equals(type)) {
            date = parse(dateStr, "yyyy-MM");
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else if ("day".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd");
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else if ("hour".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd HH");
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        format = format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
        return format;
    }

    public static String getDateLastTime(String dateStr, String type) throws ParseException {
        String format;
        Date date;
        Calendar calendar = Calendar.getInstance();
        if ("year".equals(type)) {
            date = parse(dateStr, "yyyy");
            calendar.setTime(date);
            calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        } else if ("month".equals(type)) {
            date = parse(dateStr, "yyyy-MM");
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        } else if ("day".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd");
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        } else if ("hour".equals(type)) {
            date = parse(dateStr, "yyyy-MM-dd HH");
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        }
        format = format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
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
        if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
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
        return new SimpleDateFormat("yyyy-MM-dd").format(getCalendar().getTime());
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 默认日期按“yyyy-MM-dd HH:mm:ss“格式显示
     */
    public static String formatDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getCalendar().getTime());
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
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日“格式显示
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日“格式显示
     */
    public static String formatDate(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(millis));
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
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(millis));
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：年-月-日 时：分：秒
     *
     * @param millis
     * @return
     */
    public static String formatSecondTime(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：年-月-日 时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“年-月-日 时：分“格式显示
     */
    public static String formatTime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * 默认方式表示的系统当前日期，具体格式：时：分
     *
     * @return 默认日期按“时：分“格式显示
     */
    public static String formatShortTime() {
        return new SimpleDateFormat("HH:mm").format(getCalendar().getTime());
    }

    /**
     * 指定毫秒数表示日期的默认显示，具体格式：时：分
     *
     * @param millis 指定的毫秒数
     * @return 指定毫秒数表示日期按“时：分“格式显示
     */
    public static String formatShortTime(long millis) {
        return new SimpleDateFormat("HH:mm").format(new Date(millis));
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param cal 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Calendar cal) {
        return new SimpleDateFormat("HH:mm").format(cal.getTime());
    }

    /**
     * 指定日期的默认显示，具体格式：时：分
     *
     * @param date 指定的日期
     * @return 指定日期按“时：分“格式显示
     */
    public static String formatShortTime(Date date) {
        return new SimpleDateFormat("HH:mm").format(date);
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String formatAddDate(String src, String pattern, int amount) throws ParseException {
        Calendar calendar;
        calendar = parseCalendar(src, pattern);
        calendar.add(Calendar.DATE, amount);
        return formatDate(calendar);
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
                    setValue(new SimpleDateFormat("yyyy-MM-dd").parse(text));
                } else if (text.indexOf(":") > 0 && text.length() == 19) {
                    setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text));
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);// 设置年份
        // 设置月份
        calendar.set(Calendar.MONTH, month); //设置当前月的上一个月
        // 获取某月最大天数
        int lastDay = calendar.getMinimum(Calendar.DATE); //获取月份中的最小值，即第一天
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay - 1); //上月的第一天减去1就是当月的最后一天
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
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
            long d1 = new SimpleDateFormat("yyyy-MM-dd").parse(start).getTime();
            long d2 = new SimpleDateFormat("yyyy-MM-dd").parse(end).getTime();
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long d1 = simpleDateFormat.parse(start).getTime();
            long d2 = simpleDateFormat.parse(end).getTime();
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
            Date currDate = new SimpleDateFormat("yyyy-MM").parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            return new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
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
        return new SimpleDateFormat("yyyy-MM").format(cal.getTime());
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
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
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
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
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
