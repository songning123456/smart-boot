package com.sonin.modules.model.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.model.service.IModelService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/12/26 10:58
 */
@Slf4j
@Service
public class ModelServiceImpl implements IModelService {

    @Override
    public void explainCSVFunc(String folderPath) {
        File directory = new File(folderPath);
        // 检查文件夹是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        // 获取文件夹下的所有文件和文件夹
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && Arrays.asList("管道", "检查井", "污水厂").contains(file.getName())) {
                    CSV2JsonFunc(file);
                } else {
                    explainCSVFunc(file.getAbsolutePath());
                }
            }
        }
    }

    private void CSV2JsonFunc(File folderFile) {
        Map<String, Map<String, Map<String, String>>> code2Time2Type2ValMap = new LinkedHashMap<>();
        String timePrefix = DateUtils.date2Str(new Date(), BaseConstant.dateFormat).substring(0, 10);
        int startCol = 2;
        String line, cvsSplitBy = ",";
        // todo 暂时先算1小时一条
        List<String> includeList = Arrays.asList("00");
        if (folderFile.exists() && folderFile.isDirectory()) {
            File[] files = folderFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        String[] fileNameArray = fileName.replaceAll("\\.csv", "").split("_");
                        String type = fileNameArray[fileNameArray.length - 1];
                        try {
                            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"))) {
                                int rowIndex = 0;
                                Map<Integer, String> index2CodeMap = new LinkedHashMap<>();
                                while ((line = bufferedReader.readLine()) != null) {
                                    String[] columnArray = line.split(cvsSplitBy);
                                    if (rowIndex == 0) {
                                        // 记录索引
                                        for (int colIndex = startCol; colIndex < columnArray.length; colIndex++) {
                                            index2CodeMap.put(colIndex, columnArray[colIndex].trim());
                                        }
                                    } else {
                                        String time = timePrefix + " " + StrUtils.getString(columnArray[0]).split(" ")[1];
                                        if (time.length() >= 16 && includeList.contains(time.substring(14, 16))) {
                                            // 半小时记录一次
                                            for (int colIndex = startCol; colIndex < columnArray.length; colIndex++) {
                                                code2Time2Type2ValMap.putIfAbsent(index2CodeMap.get(colIndex), new LinkedHashMap<>());
                                                code2Time2Type2ValMap.get(index2CodeMap.get(colIndex)).putIfAbsent(time, new LinkedHashMap<>());
                                                code2Time2Type2ValMap.get(index2CodeMap.get(colIndex)).get(time).putIfAbsent(type, columnArray[colIndex].trim());
                                            }
                                        }
                                    }
                                    rowIndex++;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        String primaryId, jsonName;
        if ("管道".equals(folderFile.getName())) {
            primaryId = "id";
            jsonName = "conduit";
        } else if ("检查井".equals(folderFile.getName())) {
            primaryId = "node_id";
            jsonName = "node";
        } else if ("污水厂".equals(folderFile.getName())) {
            primaryId = "ID";
            jsonName = "orifice_points";
        } else {
            return;
        }
        String outputFileName = folderFile.getAbsolutePath() + File.separator + jsonName + ".json";
        String inputFileName = "E:\\Company\\kingtrol\\007-云南丽江\\模型\\原始json" + File.separator + jsonName + ".json";
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("features");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tmpObject = jsonArray.getJSONObject(i).getJSONObject("properties");
                String code = tmpObject.getString(primaryId);
                Map<String, Map<String, String>> time2Type2ValMap = code2Time2Type2ValMap.get(code);
                if (time2Type2ValMap == null) {
                    continue;
                }
                // 计算 排水能力
                String capacityVal = tmpObject.getString("capacity");
                if (!StringUtils.isEmpty(capacityVal)) {
                    double maxFlowVal = 0D;
                    for (String time : time2Type2ValMap.keySet()) {
                        String flowVal = time2Type2ValMap.getOrDefault(time, new HashMap<>()).get("flow");
                        if (StrUtils.getDouble(flowVal, 0D) > maxFlowVal) {
                            maxFlowVal = StrUtils.getDouble(flowVal, 0D);
                        }
                    }
                    tmpObject.put("psnl", DigitalUtils.nPoint(maxFlowVal / StrUtils.getDouble(capacityVal, 0D), 2));
                } else {
                    tmpObject.put("psnl", "");
                }
                // 计算 淤积风险
                int yjfxCount = 0;
                for (String time : time2Type2ValMap.keySet()) {
                    String velVal = time2Type2ValMap.getOrDefault(time, new HashMap<>()).get("vel");
                    if (StrUtils.getDouble(velVal, 0D) < 0.6) {
                        yjfxCount++;
                    }
                }
                if (yjfxCount == time2Type2ValMap.size()) {
                    tmpObject.put("yjfx", 1);
                } else if (yjfxCount > time2Type2ValMap.size() / 2) {
                    tmpObject.put("yjfx", 2);
                } else if (yjfxCount > 0 && yjfxCount <= time2Type2ValMap.size() / 2) {
                    tmpObject.put("yjfx", 3);
                } else if (yjfxCount == 0) {
                    tmpObject.put("yjfx", 4);
                }
                for (Map.Entry<String, Map<String, String>> entry1 : time2Type2ValMap.entrySet()) {
                    String time = entry1.getKey();
                    for (Map.Entry<String, String> entry2 : entry1.getValue().entrySet()) {
                        tmpObject.put(entry2.getKey() + " " + time, entry2.getValue());
                    }
                }
            }
            // 输出内容到文件
            FileWriter fileWriter = new FileWriter(outputFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String outStr = JSON.toJSONString(jsonObject, JSONWriter.Feature.LargeObject);
            bufferedWriter.write(outStr);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCSVFunc(String folderPath) {
        File directory = new File(folderPath);
        // 检查文件夹是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        // 获取文件夹下的所有文件和文件夹
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，则递归调用删除CSV文件的方法
                    deleteCSVFunc(file.getAbsolutePath());
                } else {
                    // 如果是CSV文件，则删除
                    if (file.getName().toLowerCase().endsWith(".csv")) {
                        file.delete();
                    }
                }
            }
        }
    }

}
