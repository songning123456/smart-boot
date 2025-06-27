package com.sonin.modules.thirddata.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.javassist.JavassistFactory;
import com.sonin.core.mpp.BaseFactory;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.thirddata.config.BeanListConfig;
import com.sonin.modules.thirddata.entity.ThirdCode;
import com.sonin.modules.thirddata.service.IThirdDataService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import com.sonin.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：sonin
 * @Date：2025/5/16 13:54
 */
@Service
public class ThirdDataServiceImpl implements IThirdDataService {

    @Value("${third.enable:}")
    private String enable;
    @Value("${third.url:}")
    private String url;
    @Autowired
    private BeanListConfig beanListConfig;
    @Autowired
    private IBaseService baseService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void handleRealtimeDataFunc(Map<String, Object> paramMap) {
        // 请求参数
        String startTime = ConvertUtils.getString(paramMap.get("startTime"));
        String endTime = ConvertUtils.getString(paramMap.get("endTime"));
        // 业务
        for (ThirdCode thirdCode : beanListConfig.getValue()) {
            String nm = thirdCode.getTargetCode();
            // 请求最近1hour数据
            String fullUrl = url + "?startTime=" + startTime + "&endTime=" + endTime + "&equSensorId=" + thirdCode.getSrcCode();
            String httpResultStr = HttpUtils.doGet(fullUrl);
            if (StringUtils.isNotEmpty(httpResultStr)) {
                JSONObject jsonObject = JSON.parseObject(httpResultStr);
                JSONObject pageJsonObject = jsonObject.getJSONObject("page");
                if (pageJsonObject != null) {
                    JSONArray timeArray = pageJsonObject.getJSONArray("timeList");
                    JSONArray valueArray = pageJsonObject.getJSONArray("valueList");
                    if (timeArray != null && valueArray != null && !timeArray.isEmpty() && !valueArray.isEmpty() && timeArray.size() == valueArray.size()) {
                        String time = "", v = "";
                        // 更新xsinsert
                        for (int i = 0; i < timeArray.size(); i++) {
                            time = timeArray.getString(i);
                            v = valueArray.getString(i);
                            Long ts = DateUtils.dateStr2Sec(time, BusinessConstant.dateFormat);
                            String yearMonthDay = time.split(" ")[0].replaceAll("-", "");
                            UpdateWrapper<?> updateWrapper = new UpdateWrapper<>().set("v", v).eq("ts", String.valueOf(ts)).eq("nm", nm);
                            try {
                                String finalV1 = v;
                                DataSourceTemplate.execute("pg-db", () -> {
                                    transactionTemplate.execute(transactionStatus -> {
                                        int successTotal = baseService.update("xsinsert" + yearMonthDay, updateWrapper);
                                        if (successTotal == 0) {
                                            baseService.insert("xsinsert" + yearMonthDay, new HashMap<String, Object>() {{
                                                put("nm", nm);
                                                put("v", finalV1);
                                                put("ts", ts);
                                                put("createtime", ts);
                                                put("factoryname", nm.split("_")[0]);
                                                put("devicename", "third");
                                                put("type", 99);
                                                put("gatewaycode", nm.split("_")[0]);
                                            }});
                                        }
                                        return 1;
                                    });
                                    return 1;
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // 更新realtimedata
                        Long ts = DateUtils.dateStr2Sec(time, BusinessConstant.dateFormat);
                        UpdateWrapper<?> updateWrapper = new UpdateWrapper<>().set("v", v).set("ts", ts).eq("nm", nm);
                        try {
                            String finalV2 = v;
                            DataSourceTemplate.execute("pg-db", () -> {
                                transactionTemplate.execute(transactionStatus -> {
                                    int successTotal = baseService.update("realtimedata", updateWrapper);
                                    if (successTotal == 0) {
                                        baseService.insert("realtimedata", new HashMap<String, Object>() {{
                                            put("nm", nm);
                                            put("v", finalV2);
                                            put("ts", ts);
                                            put("createtime", ts);
                                            put("factoryname", nm.split("_")[0]);
                                            put("devicename", "third");
                                            put("type", 99);
                                            put("gatewaycode", nm.split("_")[0]);
                                        }});
                                    }
                                    return 1;
                                });
                                return 1;
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleCountDataFunc(Map<String, Object> paramMap) throws Exception {
        // 请求参数
        String startTime = ConvertUtils.getString(paramMap.get("startTime"));
        String endTime = ConvertUtils.getString(paramMap.get("endTime"));
        // 业务
        if (beanListConfig != null && beanListConfig.getValue() != null && !beanListConfig.getValue().isEmpty()) {
            List<String> hourList = DateUtils.intervalByHour(startTime, endTime, BusinessConstant.dateFormat.replaceAll(":mm:ss", ""));
            for (int i = 0; i < hourList.size(); i++) {
                startTime = hourList.get(i) + ":00:00";
                endTime = hourList.get(i) + ":59:59";
                Long startTs = DateUtils.dateStr2Sec(startTime, BusinessConstant.dateFormat);
                Long endTs = DateUtils.dateStr2Sec(endTime, BusinessConstant.dateFormat);
                String yearMonthDay = startTime.split(" ")[0].replaceAll("-", "");
                Class clazz = JavassistFactory.create().similarClassName("Xsinsert").className("Xsinsert" + yearMonthDay).buildClass();
                for (ThirdCode thirdCode : beanListConfig.getValue()) {
                    String nm = thirdCode.getTargetCode();
                    String tableName = nm.split("_")[0].toLowerCase() + "_count";
                    List<Map<String, Object>> queryMapList = BaseFactory.JOIN().select("ts", "v").from(clazz).where().ge(true, "ts", String.valueOf(startTs)).le(true, "ts", String.valueOf(endTs)).eq(true, "nm", nm).orderBy(true, true, "ts").queryForList("pg-db");
                    Double v = null;
                    if ("avg".equals(thirdCode.getType()) && !queryMapList.isEmpty()) {
                        v = queryMapList.stream().mapToDouble(item -> Double.parseDouble(ConvertUtils.getString(item.get("v")))).average().getAsDouble();
                    } else if ("diff".equals(thirdCode.getType()) && queryMapList.size() >= 2) {
                        v = ConvertUtils.getDouble(ConvertUtils.getString(queryMapList.get(queryMapList.size() - 1).get("v")), 0D) - ConvertUtils.getDouble(ConvertUtils.getString(queryMapList.get(0).get("v")), 0D);
                    }
                    if (v != null) {
                        UpdateWrapper<?> updateWrapper = new UpdateWrapper<>().set("v", v).eq("ts", String.valueOf(startTs)).eq("nm", nm);
                        Double finalV = v;
                        DataSourceTemplate.execute("pg-db", () -> {
                            transactionTemplate.execute(transactionStatus -> {
                                int successTotal = baseService.update(tableName, updateWrapper);
                                if (successTotal == 0) {
                                    baseService.insert(tableName, new HashMap<String, Object>() {{
                                        put("nm", nm);
                                        put("v", finalV);
                                        put("ts", startTs);
                                        put("createtime", startTs);
                                        put("factoryname", nm.split("_")[0]);
                                        put("devicename", "third");
                                        put("type", "avg");
                                        put("gatewaycode", nm.split("_")[0]);
                                    }});
                                }
                                return 1;
                            });
                            return 1;
                        });
                    }
                }
            }
        }
    }

    @Override
    public void handleCommonCountDataFunc(Map<String, Object> paramMap) throws Exception {
        // 请求参数
        String startTime = ConvertUtils.getString(paramMap.get("startTime"));
        String endTime = ConvertUtils.getString(paramMap.get("endTime"));
        String nm = ConvertUtils.getString(paramMap.get("nm"));
        String type = ConvertUtils.getString(paramMap.get("type"));
        List<String> hourList = DateUtils.intervalByHour(startTime, endTime, BusinessConstant.dateFormat.replaceAll(":mm:ss", ""));
        // 遍历时间
        for (int i = 0; i < hourList.size() - 1; i++) {
            startTime = hourList.get(i) + ":00:00";
            endTime = hourList.get(i + 1) + ":00:00";
            Long startTs = DateUtils.dateStr2Sec(startTime, BusinessConstant.dateFormat);
            Long endTs = DateUtils.dateStr2Sec(endTime, BusinessConstant.dateFormat);
            String yearMonthDay = startTime.split(" ")[0].replaceAll("-", "");
            Class clazz = JavassistFactory.create().similarClassName("Xsinsert").className("Xsinsert" + yearMonthDay).buildClass();
            String tableName = nm.split("_")[0].toLowerCase() + "_count";
            List<Map<String, Object>> queryMapList = BaseFactory.JOIN().select("ts", "v").from(clazz).where().ge(true, "ts", String.valueOf(startTs)).lt(true, "ts", String.valueOf(endTs)).eq(true, "nm", nm).orderBy(true, true, "ts").queryForList("pg-db");
            Double v = null;
            if ("avg".equals(type) && !queryMapList.isEmpty()) {
                v = queryMapList.stream().mapToDouble(item -> Double.parseDouble(ConvertUtils.getString(item.get("v")))).average().getAsDouble();
            } else if ("diff".equals(type) && queryMapList.size() >= 2) {
                v = ConvertUtils.getDouble(ConvertUtils.getString(queryMapList.get(queryMapList.size() - 1).get("v")), 0D) - ConvertUtils.getDouble(ConvertUtils.getString(queryMapList.get(0).get("v")), 0D);
            }
            if (v != null) {
                UpdateWrapper<?> updateWrapper = new UpdateWrapper<>().set("v", v).eq("ts", String.valueOf(startTs)).eq("nm", nm);
                Double finalV = v;
                DataSourceTemplate.execute("pg-db", () -> {
                    transactionTemplate.execute(transactionStatus -> {
                        int successTotal = baseService.update(tableName, updateWrapper);
                        if (successTotal == 0) {
                            baseService.insert(tableName, new HashMap<String, Object>() {{
                                put("nm", nm);
                                put("v", finalV);
                                put("ts", startTs);
                                put("createtime", startTs);
                                put("factoryname", nm.split("_")[0]);
                                put("devicename", "third");
                                put("type", "avg");
                                put("gatewaycode", nm.split("_")[0]);
                            }});
                        }
                        return 1;
                    });
                    return 1;
                });
            }
        }
    }

}
