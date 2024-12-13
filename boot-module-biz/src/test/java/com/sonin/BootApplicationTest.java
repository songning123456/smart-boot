package com.sonin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.entity.CaseWhen;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.constant.BaseConstant;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.ConvertUtils;
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

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
     * 读取文件内容并修改
     */
    @Test
    public void readAndReplaceTest() {
        File folder = new File("E:\\Project\\kingtrol\\sk-se-boot-dev-factory-base\\sk-module-biz\\src\\main\\java\\com\\skua\\modules\\equip\\entity");
        if (folder.exists() && folder.isDirectory()) {
            // 获取文件夹下的所有文件
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 只处理文件，不包括子文件夹
                    if (file.isFile()) {
                        try {
                            Path filePath = Paths.get(file.getAbsolutePath());
                            // 1. 读取文件内容
                            List<String> lines = Files.readAllLines(filePath);
                            for (int i = 0; i < lines.size(); i++) {
                                String curLine = lines.get(i);
                                if (curLine.contains("private")) {
                                    String[] contentArray = curLine.replaceAll(";", "").split(" ");
                                    String column = StrUtil.toUnderlineCase(contentArray[contentArray.length - 1]);
                                    String updateLine = "@TableField(\"" + column + "\") " + curLine;
                                    lines.set(i, updateLine);
                                }
                            }
                            // 3. 将修改后的内容写回到文件
                            Files.write(filePath, lines);
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
                        String id = ConvertUtils.UUID(configType + configCode);
                        index2IdMap.put(colIndex, id);
                        Map<String, Object> entityMap0 = new HashMap<String, Object>() {{
                            put("id", id);
                            put("config_type", configType);
                            put("config_code", configCode);
                        }};
                        baseService.insert("model_config", entityMap0, com.sonin.modules.base.constant.BaseConstant.INSERT_IGNORE);
                    }
                } else {
                    String time = ConvertUtils.getString(csvRecord.get(0));
                    // todo 下一行待删除
                    time = "2023-12-15 00:00:00";
                    int ts = DateUtils.dateStr2Sec(time, BusinessConstant.dateFormat).intValue();
                    // 从第2列开始读取指标数据(0: Time; 1: Seconds)
                    for (int colIndex = startCol; colIndex < csvRecord.size(); colIndex++) {
                        String cellValue = csvRecord.get(colIndex);
                        Map<String, Object> entityMap = new HashMap<>();
                        String id = ConvertUtils.UUID(index2IdMap.get(colIndex) + ts);
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
     * 连接clickhouse测试
     */
    @Test
    public void clickhouseTest() {
        List<Map<String, Object>> queryMapList0 = DataSourceTemplate.execute("ck-ds", () -> baseService.queryForList("select * from base_customer", new QueryWrapper<>().last("limit 10")));
        System.out.println(queryMapList0);
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
        int res0 = baseService.insert("demo_n", entity0Map, BaseConstant.INSERT);
        // 测试insert ignore
        Map<String, Object> entity1Map = new LinkedHashMap<String, Object>() {{
            put("id", "1");
            put("create_by", "sonin1");
        }};
        // 忽略主键相同的数据，res1=0
        int res1 = baseService.insert("demo_n", entity1Map, BaseConstant.INSERT_IGNORE);
        // 测试replace
        Map<String, Object> entity2Map = new LinkedHashMap<String, Object>() {{
            put("id", "1");
            put("create_by", "sonin2");
        }};
        // 若有相同主键则替换，若无则新生成，res2=2
        int res2 = baseService.insert("demo_n", entity2Map, BaseConstant.REPLACE);
        System.out.println("end");
    }

}
