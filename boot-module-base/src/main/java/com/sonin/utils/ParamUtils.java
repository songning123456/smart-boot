package com.sonin.utils;

import com.sonin.core.constant.BusinessConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求参数工具类
 *
 * @Author：sonin
 * @Date：2025/3/6 15:05
 */
public class ParamUtils {

    public static List<String> timeRangeParamFunc(String startTime, String endTime, String timeType) {
        List<String> timeList = new ArrayList<>(), intervalTimeList;
        if ("day".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByDay(startTime, endTime, BusinessConstant.dateFormat.substring(0, 10));
            for (String intervalTime : intervalTimeList) {
                timeList.add(intervalTime + BusinessConstant.startTimeSuffix + "~" + intervalTime + BusinessConstant.endTimeSuffix);
            }
        } else if ("month".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.dateFormat.substring(0, 7));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01" + BusinessConstant.startTimeSuffix;
                int days = DateUtils.lengthOfSomeMonth(Integer.parseInt(intervalTime.split("-")[0]), Integer.parseInt(intervalTime.split("-")[1]));
                String tmpEndTime = intervalTime + "-" + days + BusinessConstant.endTimeSuffix;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByYear(startTime, endTime, BusinessConstant.dateFormat.substring(0, 4));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01-01" + BusinessConstant.startTimeSuffix;
                String tmpEndTime = intervalTime + "-12-31" + BusinessConstant.endTimeSuffix;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        }
        return timeList;
    }

}
