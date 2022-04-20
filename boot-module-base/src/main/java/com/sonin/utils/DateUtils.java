package com.sonin.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 日期工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:22
 */
public class DateUtils {

    /**
     * 根据(开始时间, 结束时间)获取每一天
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static List<String> everyDay(String startTime, String endTime, String format) {
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

}
