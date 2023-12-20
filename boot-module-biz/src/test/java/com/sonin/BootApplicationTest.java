package com.sonin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
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
import org.springframework.test.context.junit4.SpringRunner;

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

}
