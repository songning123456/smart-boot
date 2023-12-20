package com.sonin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.model.dto.ModelInDTO;
import com.sonin.modules.model.dto.ModelOutDTO;
import com.sonin.utils.DateUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
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

    /**
     * <pre>
     * 读取.csv
     * 备注: CSV读取是流式读取，只能forEach循环读取，不能先获取大小再重新更具索引index获取数据。
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void readCSVTest() {
        String timePrefix = DateUtils.date2Str(new Date(), BaseConstant.dateFormat).substring(0, 10);
        String fileName = "E:\\Company\\kingtrol\\007-云南丽江\\模型\\geojson原始数据\\test01.csv";
        int startCol = 2;
        try {
            // 查询 管井 经纬度
            List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from pipenetwork_tubewell", new QueryWrapper<>());
            Map<String, String[]> code2PositionMap = queryMapList0.stream().collect(Collectors.toMap(item -> StrUtils.getString(item.get("id")), item -> new String[]{StrUtils.getString(item.get("original_longitude")), StrUtils.getString(item.get("original_latitude"))}));
            ModelOutDTO modelOutDTO = new ModelOutDTO();
            Reader reader = new FileReader(fileName);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int i = 0;
            Map<Integer, String> index2CodeMap = new LinkedHashMap<>();
            for (CSVRecord csvRecord : csvParser) {
                if (i == 0) {
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        String configCode = csvRecord.get(colIndex);
                        index2CodeMap.put(colIndex, configCode);
                    }
                } else if (i == 1) {
                    String time = timePrefix + " " + StrUtils.getString(csvRecord.get(0)).split(" ")[1];
                    // 从第2列开始读取指标数据(0: Time; 1: Seconds)
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        ModelInDTO modelInDTO = new ModelInDTO();
                        modelInDTO.setId(colIndex - 2);
                        Map<String, Object> geometryMap = new LinkedHashMap<>();
                        geometryMap.put("type", "Point");
                        geometryMap.put("coordinates", code2PositionMap.getOrDefault(index2CodeMap.get(colIndex), new String[]{"", ""}));
                        modelInDTO.setGeometry(geometryMap);
                        Map<String, Object> propertiesMap = new LinkedHashMap<>();
                        propertiesMap.put("FID", modelInDTO.getId());
                        propertiesMap.put("id", index2CodeMap.get(colIndex));
                        propertiesMap.put(time, csvRecord.get(colIndex).trim());
                        modelInDTO.setProperties(propertiesMap);
                        modelOutDTO.getFeatures().add(modelInDTO);
                    }
                } else {
                    String time = timePrefix + " " + StrUtils.getString(csvRecord.get(0)).split(" ")[1];
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        modelOutDTO.getFeatures().get(colIndex - 2).getProperties().put(time, csvRecord.get(colIndex).trim());
                    }
                }
                i++;
            }
            // 输出内容到文件
            String newFileName = fileName.replaceAll("\\.csv", ".json");
            FileWriter fileWriter = new FileWriter(newFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String outStr = JSONObject.toJSONString(modelOutDTO);
            bufferedWriter.write(outStr);
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * xsinert 转换成 count
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

}
