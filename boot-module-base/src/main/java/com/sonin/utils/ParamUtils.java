package com.sonin.utils;

import com.sonin.core.constant.BusinessConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 请求参数工具类
 *
 * @Author：sonin
 * @Date：2025/3/6 15:05
 */
public class ParamUtils {

    /**
     * 时间范围参数处理
     *
     * @param startTime
     * @param endTime
     * @param timeType
     * @return
     */
    public static List<String> timeRangeParamFunc(String startTime, String endTime, String timeType) {
        List<String> timeList = new ArrayList<>(), intervalTimeList;
        if ("day".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByDay(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 10));
            for (String intervalTime : intervalTimeList) {
                timeList.add(intervalTime + BusinessConstant.START_TIME_SUFFIX + "~" + intervalTime + BusinessConstant.END_TIME_SUFFIX);
            }
        } else if ("month".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 7));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01" + BusinessConstant.START_TIME_SUFFIX;
                int days = DateUtils.lengthOfSomeMonth(Integer.parseInt(intervalTime.split("-")[0]), Integer.parseInt(intervalTime.split("-")[1]));
                String tmpEndTime = intervalTime + "-" + days + BusinessConstant.END_TIME_SUFFIX;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByYear(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 4));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01-01" + BusinessConstant.START_TIME_SUFFIX;
                String tmpEndTime = intervalTime + "-12-31" + BusinessConstant.END_TIME_SUFFIX;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        }
        return timeList;
    }

    /**
     * 统一保留小数位数
     *
     * @param paramMap
     * @param nPoint
     */
    public static void retainDecimalFunc(Map<String, Object> paramMap, int nPoint) {
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getValue() instanceof List) {
                ((List) entry.getValue()).forEach(item -> {
                    if (item instanceof Map) {
                        retainDecimalFunc((Map<String, Object>) item, nPoint);
                    } else {
                        if (ConvertUtils.isNumeric(ConvertUtils.getString(item))) {
                            paramMap.put(entry.getKey(), ConvertUtils.nPoint(item, nPoint));
                        }
                    }
                });
            } else {
                if (ConvertUtils.isNumeric(ConvertUtils.getString(entry.getValue()))) {
                    paramMap.put(entry.getKey(), ConvertUtils.nPoint(entry.getValue(), nPoint));
                }
            }
        }
    }

}
