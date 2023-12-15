package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.entity.CaseWhen;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
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
     * 读取文件测试
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void readFileTest() {
        File folder = new File("E:\\Project\\github-pages\\interview-linux\\pages");
        StringBuilder stringBuilder = new StringBuilder();
        // 检查文件夹是否存在
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // 获取文件夹下的所有文件
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) { // 只处理文件，不包括子文件夹
                        stringBuilder.append("* [").append(file.getName().replaceAll("\\.md", "")).append("](/pages/").append(file.getName()).append(")\n");
                    }
                }
            }
        }
        System.out.println(stringBuilder.toString());
    }

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
        String fileName = "E:\\Company\\kingtrol\\007-云南丽江\\丽江模型结果输出demo及说明\\CSV\\Link_ _工况1_ds_depth.csv";
        String[] fileNameArray = fileName.split("\\\\");
        String configType = fileNameArray[fileNameArray.length - 1].split("\\.")[0];
        int startCol = 2;
        try {
            Reader reader = new FileReader(fileName);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int i = 0;
            Map<Integer, String> index2IdMap = new LinkedHashMap<>();
            for (CSVRecord csvRecord : csvParser) {
                if (i == 0) {
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        String configCode = csvRecord.get(colIndex);
                        String id = StrUtils.UUID(configType + configCode);
                        index2IdMap.put(colIndex, id);
                        Map<String, Object> entityMap0 = new HashMap<String, Object>() {{
                            put("id", id);
                            put("config_type", configType);
                            put("config_code", configCode);
                        }};
                        baseService.insert("model_config", entityMap0, com.sonin.modules.base.constant.BaseConstant.INSERT_IGNORE);
                    }
                } else {
                    String time = StrUtils.getString(csvRecord.get(0));
                    // todo 下一行待删除
                    time = "2023-12-15 00:00:00";
                    int ts = DateUtils.dateStr2Sec(time, BaseConstant.dateFormat).intValue();
                    // 从第2列开始读取指标数据(0: Time; 1: Seconds)
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        String cellValue = csvRecord.get(colIndex);
                        Map<String, Object> entityMap = new HashMap<>();
                        String id = StrUtils.UUID(index2IdMap.get(colIndex) + ts);
                        entityMap.put("id", id);
                        entityMap.put("ts", ts);
                        entityMap.put("config_id", index2IdMap.get(colIndex));
                        entityMap.put("v", cellValue);
                        String tableSuffix = time.substring(0, 10).replaceAll("-", "");
                        DataSourceTemplate.execute("ynlj-third", () -> {
                            baseService.insert("model_data" + tableSuffix, entityMap, com.sonin.modules.base.constant.BaseConstant.INSERT_IGNORE);
                            return 1;
                        });
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 模拟数据
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void generateDataTest() {
        String yearMonthDayStr = "2023-08-24";
        List<String> nmList = new ArrayList<String>() {{
            add("demo_test");
        }};
        String startTime = yearMonthDayStr + " 00:00:00";
        String endTime = yearMonthDayStr + " 23:59:59";
        String countTable = "ffs_count";
        long startTs = DateUtils.strToDate(startTime, BaseConstant.dateFormat).getTime() / 1000;
        long endTs = DateUtils.strToDate(endTime, BaseConstant.dateFormat).getTime() / 1000;
        List<Map<String, Object>> historyDataList = new ArrayList<>();
        List<Map<String, Object>> countDataList = new ArrayList<>();
        int index = 0;
        for (String nm : nmList) {
            for (long ts = startTs; ts <= endTs; ts += 10) {
                long finalTs = ts;
                historyDataList.add(new HashMap<String, Object>() {{
                    put("nm", nm);
                    put("v", DigitalUtils.intRandom(0, 100));
                    put("ts", finalTs);
                }});
            }
            for (long ts = startTs; ts <= endTs; ts += 3600) {
                String hourStr = DateUtils.sec2DateStr(ts, BaseConstant.dateFormat.substring(0, 13)) + ":00:00";
                Long hourTs = DateUtils.dateStr2Sec(hourStr, BaseConstant.dateFormat);
                int finalIndex = index;
                countDataList.add(new HashMap<String, Object>() {{
                    put("id", finalIndex);
                    put("nm", nm);
                    put("v", DigitalUtils.intRandom(0, 100));
                    put("ts", StrUtils.getString(hourTs));
                }});
                index++;
            }
        }
        if (!historyDataList.isEmpty() && !countDataList.isEmpty()) {
            DataSourceTemplate.execute("pg-db", () -> {
                baseService.insert("realtimedata", historyDataList.get(historyDataList.size() - 1));
                baseService.insertBatch("xsinsert" + yearMonthDayStr.replaceAll("-", ""), historyDataList);
                baseService.insertBatch(countTable, countDataList);
                return 1;
            });
        }
    }

    /**
     * <pre>
     * case when 测试
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void caseWhenTest() {
        // mysql测试
        CaseWhen caseWhen = new CaseWhen();
        caseWhen.selectCaseWhen("sex = '0'", "1", "0", "mysql0");
        caseWhen.selectCaseWhen("sex = '1'", "1", "0", "mysql1");
        Map<String, Object> queryMap = baseService.queryForMap("select " + caseWhen.print() + " from sys_user", new QueryWrapper<>());
        // mysql返回BigDecimal类型
        BigDecimal mysql0 = (BigDecimal) queryMap.get("mysql0");
        System.out.println(mysql0);
        // pg测试
        CaseWhen caseWhen2 = new CaseWhen();
        caseWhen2.selectPgCaseWhen("ts >= '1693375200'", "1", "0", "pg0");
        caseWhen2.selectPgCaseWhen("ts < '1693375200'", "1", "0", "pg1");
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("pg-db");
        Map<String, Object> queryMap2 = pgDB.queryForMap("select " + caseWhen2.print() + " from ffs_count");
        // pg返回Double类型
        Double pg0 = (Double) queryMap2.get("pg0");
        System.out.println(pg0);
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
        String startTime = "2023-12-09 00:00:00";
        String endTime = "2023-12-14 23:59:59";
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

    /**
     * <pre>
     * mysql中insert、insert ignore、replace插入测试
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void insertTypeTest() {
        // 测试insert
        Map<String, Object> entity0Map = new LinkedHashMap<String, Object>() {{
            put("id", "1");
            put("create_by", "sonin0");
        }};
        // 检查主键，重复会报错，res0=1
        int res0 = baseService.insert("demo_n", entity0Map, com.sonin.modules.base.constant.BaseConstant.INSERT);
        // 测试insert ignore
        Map<String, Object> entity1Map = new LinkedHashMap<String, Object>() {{
            put("id", "1");
            put("create_by", "sonin1");
        }};
        // 忽略主键相同的数据，res1=0
        int res1 = baseService.insert("demo_n", entity1Map, com.sonin.modules.base.constant.BaseConstant.INSERT_IGNORE);
        // 测试replace
        Map<String, Object> entity2Map = new LinkedHashMap<String, Object>() {{
            put("id", "1");
            put("create_by", "sonin2");
        }};
        // 若有相同主键则替换，若无则新生成，res2=2
        int res2 = baseService.insert("demo_n", entity2Map, com.sonin.modules.base.constant.BaseConstant.REPLACE);
        System.out.println("end");
    }

}
