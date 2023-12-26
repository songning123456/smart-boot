package com.sonin.modules.model.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sonin.core.constant.BaseConstant;
import com.sonin.modules.model.service.IModelService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

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
        List<String> includeList = Arrays.asList("00", "30");
        if (folderFile.exists() && folderFile.isDirectory()) {
            File[] files = folderFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        String[] fileNameArray = fileName.replaceAll("\\.csv", "").split("_");
                        String type = fileNameArray[fileNameArray.length - 1];
                        try {
                            Reader reader = new FileReader(file);
                            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
                            int i = 0;
                            Map<Integer, String> index2CodeMap = new LinkedHashMap<>();
                            for (CSVRecord csvRecord : csvParser) {
                                if (i == 0) {
                                    // 记录索引
                                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                                        index2CodeMap.put(colIndex, csvRecord.get(colIndex).trim());
                                    }
                                } else {
                                    String time = timePrefix + " " + StrUtils.getString(csvRecord.get(0)).split(" ")[1];
                                    if (includeList.contains(time.substring(14, 16))) {
                                        // 半小时记录一次
                                        for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                                            code2Time2Type2ValMap.putIfAbsent(index2CodeMap.get(colIndex), new LinkedHashMap<>());
                                            code2Time2Type2ValMap.get(index2CodeMap.get(colIndex)).putIfAbsent(time, new LinkedHashMap<>());
                                            code2Time2Type2ValMap.get(index2CodeMap.get(colIndex)).get(time).putIfAbsent(type, csvRecord.get(colIndex).trim());
                                        }
                                    }
                                }
                                i++;
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
            String line;
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
                for (Map.Entry<String, Map<String, String>> entry1 : time2Type2ValMap.entrySet()) {
                    tmpObject.put(entry1.getKey(), entry1.getValue());
                }
            }
            // 输出内容到文件
            FileWriter fileWriter = new FileWriter(outputFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String outStr = JSONObject.toJSONString(jsonObject);
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
