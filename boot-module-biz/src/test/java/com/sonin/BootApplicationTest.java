package com.sonin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.model.service.IModelService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class BootApplicationTest {

    @Autowired
    private IBaseService baseService;
    @Autowired
    private IModelService modelService;

    @Test
    public void readCSVTest() throws Exception {
        String parentDirName = "E:\\Company\\kingtrol\\007-云南丽江\\模型\\结果输出文件01";
        modelService.explainCSVFunc(parentDirName);
    }

    @Test
    public void readCSV2Test() {
        String parentDirName = "E:\\Company\\kingtrol\\007-云南丽江\\模型\\结果输出文件01";
        File folderFile = new File(parentDirName + File.separator + "管道统计结果");
        Map<String, Map<String, String>> code2ZhName2ValMap = new LinkedHashMap<>();
        int startCol = 1;
        String line, cvsSplitBy = ",";
        if (folderFile.exists() && folderFile.isDirectory()) {
            File[] files = folderFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"))) {
                            int rowIndex = 0;
                            Map<Integer, String> index2ZhNameMap = new LinkedHashMap<>();
                            while ((line = bufferedReader.readLine()) != null) {
                                String[] columnArray = line.split(cvsSplitBy);
                                if (rowIndex == 0) {
                                    // 记录索引
                                    for (int colIndex = startCol; colIndex < columnArray.length; colIndex++) {
                                        index2ZhNameMap.put(colIndex, columnArray[colIndex].trim());
                                    }
                                } else {
                                    for (int colIndex = startCol; colIndex < columnArray.length; colIndex++) {
                                        String code = columnArray[0];
                                        code2ZhName2ValMap.putIfAbsent(code, new LinkedHashMap<>());
                                        code2ZhName2ValMap.get(code).putIfAbsent(index2ZhNameMap.get(colIndex), columnArray[colIndex]);
                                    }
                                }
                                rowIndex++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        String primaryId = "id", jsonName = "conduit";
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
                Map<String, String> zhName2ValMap = code2ZhName2ValMap.get(code);
                if (zhName2ValMap == null) {
                    continue;
                }
                for (Map.Entry<String, String> entry1 : zhName2ValMap.entrySet()) {
                    String zhName = entry1.getKey();
                    tmpObject.put(zhName, entry1.getValue());
                }
            }
            // 输出内容到文件
            FileWriter fileWriter = new FileWriter(outputFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String outStr = JSON.toJSONString(jsonObject);
            bufferedWriter.write(outStr);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 删除csv
        modelService.deleteCSVFunc(parentDirName);
    }

    /**
     * <pre>
     * xsinsert 转换成 count
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void xsinsert2countTest() {
        String startTime = "2023-12-18 00:00:00";
        String endTime = "2023-12-20 23:59:59";
        List<String> timeList = DateUtils.intervalByHour(startTime, endTime, BaseConstant.dateFormat.substring(0, 14));
        // 查询depart_id => device_id 转换关系
        List<Map<String, Object>> deviceMapList = baseService.queryForList("select depart_id, device_id from sys_factory_device", new QueryWrapper<>());
        Map<String, String> depart2DeviceMap = deviceMapList.stream().collect(Collectors.toMap(item -> StrUtils.getString(item.get("depart_id")), item -> StrUtils.getString(item.get("device_id")), (v1, v2) -> v2));
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        for (String time : timeList) {
            String tableSuffix = time.substring(0, 10).replaceAll("-", "");
            String tmpStartTime = time + "00:00";
            String tmpStartTs = StrUtils.getString(DateUtils.dateStr2Sec(tmpStartTime, BaseConstant.dateFormat));
            String tmpEndTime = time + "59:59";
            String tmpEndTs = StrUtils.getString(DateUtils.dateStr2Sec(tmpEndTime, BaseConstant.dateFormat));
            Map<String, String[]> nm2InfoMap = new LinkedHashMap<>();
            Map<String, List<String>> nm2ValListMap = new LinkedHashMap<>();
            // 查询这一个小时的所有数据(按照时间升序)
            List<Map<String, Object>> queryMapList = pgDB.queryForList("select nm, v, ts, factoryname, type from xsinsert" + tableSuffix + " where ts >= ? and ts <= ? order by ts asc", tmpStartTs, tmpEndTs);
            for (Map<String, Object> item : queryMapList) {
                String nm = StrUtils.getString(item.get("nm"));
                String v = StrUtils.getString(item.get("v"));
                String ts = StrUtils.getString(item.get("ts"));
                String factoryName = StrUtils.getString(item.get("factoryname"));
                String type = StrUtils.getString(item.get("type"));
                nm2InfoMap.put(nm, new String[]{type, factoryName});
                nm2ValListMap.putIfAbsent(nm, new ArrayList<>());
                nm2ValListMap.get(nm).add(v);
            }
            // 插入数据(挨个插入nm)
            for (Map.Entry<String, String[]> entry : nm2InfoMap.entrySet()) {
                String nm = entry.getKey();
                String type = entry.getValue()[0];
                String factoryName = entry.getValue()[1];
                String tableNamePrefix = depart2DeviceMap.get(factoryName);
                // 不存在则直接跳过
                if (StringUtils.isEmpty(tableNamePrefix)) {
                    continue;
                }
                String tableName = tableNamePrefix.toLowerCase() + "_count";
                List<String> valList = nm2ValListMap.get(nm);
                Double val = null;
                if ("3".equals(type) && valList.size() > 1) {
                    // 求差
                    val = StrUtils.getDouble(valList.get(valList.size() - 1), 0D) - StrUtils.getDouble(valList.get(0), 0D);
                } else if ("4".equals(type)) {
                    // 求平均
                    val = valList.stream().mapToDouble(item -> StrUtils.getDouble(item, 0D)).average().getAsDouble();
                }
                if (val != null) {
                    // 先查询此nm, ts是否有值，无值才补录
                    Map<String, Object> countQueryMap = pgDB.queryForMap("select count(*) as total from " + tableName + " where nm = ? and ts = ?", nm, tmpStartTs);
                    double total = StrUtils.getDouble(countQueryMap.get("total"), 0D);
                    if (total == 0) {
                        try {
                            pgDB.update("insert into " + tableName + "(nm, v, ts, createtime, factoryname, devicename, type, gatewaycode) values(?, ?, ?, ?, ?, ?, ?, ?)", nm, val, tmpStartTs, Long.parseLong(tmpStartTs), factoryName, "custom", type, factoryName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Test
    public void readExcelTest() {
        try {
            // 指定本地Excel文件路径，替换为实际文件路径
            String filePath = "E:\\Company\\kingtrol\\007-云南丽江\\入数据\\丽江市日供水数据01.xlsx";
            FileInputStream file = new FileInputStream(new File(filePath));
            // 创建一个Workbook对象，打开Excel文件
            Workbook workbook = WorkbookFactory.create(file);
            // 获取第一个sheet页
            Sheet sheet0 = workbook.getSheetAt(0);
            // 遍历每一行并打印内容
            Map<String, String> time2ValMap = new LinkedHashMap<>();
            int rowIndex = 0;
            for (Row row0 : sheet0) {
                if (rowIndex > 0) {
                    Date date = DateUtil.getJavaDate(row0.getCell(0).getNumericCellValue());
                    String time = DateUtils.date2Str(date, BaseConstant.dateFormat);
                    String val = StrUtils.getString(row0.getCell(1)).trim();
                    time2ValMap.put(time, val);
                }
                rowIndex++;
            }
            String nm = "custom_rjgsl";
            for (Map.Entry<String, String> entry : time2ValMap.entrySet()) {
                String ts = String.valueOf(DateUtils.dateStr2Sec(entry.getKey(), BaseConstant.dateFormat));
                String tableSuffix = entry.getKey().substring(0, 10).replaceAll("-", "");
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("nm", nm);
                entityMap.put("ts", ts);
                entityMap.put("v", entry.getValue());
                DataSourceTemplate.execute("pg-db", () -> {
                    // baseService.insert("xsinsert" + tableSuffix, entityMap);
                    baseService.insert("rjgsl_count", entityMap);
                    return 1;
                });
            }
            // 关闭workbook和文件流
            workbook.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
