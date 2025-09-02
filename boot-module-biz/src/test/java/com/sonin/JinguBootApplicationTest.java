package com.sonin;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.modules.base.constant.BaseConstant;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.ExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.aspose.words.*;


/**
 * @Author：sonin
 * @Date：2025/2/28 13:07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
@ActiveProfiles("jingu")
public class JinguBootApplicationTest {

    @Autowired
    private IBaseService baseService;

    /**
     * 读取excel
     */
    @Test
    public void readExcelTest() throws Exception {
        // 1. 创建输入流，读取 Excel 文件
        FileInputStream fileInputStream = new FileInputStream("E:\\Company\\kingtrol\\033-津沽\\评估诊断\\导入数据.xlsx");
        // 2. 使用 XSSFWorkbook 解析Excel文件（.xlsx 格式）
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        // 3. 获取第一个工作表
        Sheet sheet = workbook.getSheetAt(3);
        // 4. 遍历行和列
        int index = 0;
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        String prevScoreOne = "", prevScoreTwo = "", prevScoreThree = "", prevScoreThreeElement = "", prevScoreThreeRule = "";
        String scoreTime = "2025-01";
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from evaluate_score_desc", new QueryWrapper<>());
        Map<String, String> dictMap = queryMapList0.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("score_desc")), item -> ConvertUtils.getString(item.get("id"))));
        for (Row row : sheet) {
            if (index != 0) {
                Map<String, Object> dataMap = new HashMap<>();
                String scoreOne = row.getCell(0).getStringCellValue();
                if (StringUtils.isEmpty(scoreOne)) {
                    scoreOne = prevScoreOne;
                } else {
                    prevScoreOne = scoreOne;
                }
                String scoreTwo = row.getCell(1).getStringCellValue();
                if (StringUtils.isEmpty(scoreTwo)) {
                    scoreTwo = prevScoreTwo;
                } else {
                    prevScoreTwo = scoreTwo;
                }
                String scoreThree = row.getCell(2).getStringCellValue();
                if (StringUtils.isEmpty(scoreThree)) {
                    scoreThree = prevScoreThree;
                } else {
                    prevScoreThree = scoreThree;
                }
                String scoreThreeElement = row.getCell(3).getStringCellValue();
                String scoreThreeRule = row.getCell(4).getStringCellValue();
                double scoreThreeValue = row.getCell(5).getNumericCellValue();
                dataMap.put("id", RandomUtil.randomString(32));
                dataMap.put("score_time", scoreTime);
                dataMap.put("score_one", dictMap.get(scoreOne));
                dataMap.put("score_two", dictMap.get(scoreTwo));
                dataMap.put("score_three", dictMap.get(scoreThree));
                dataMap.put("score_three_element", dictMap.get(scoreThreeElement));
                dataMap.put("score_three_rule", dictMap.get(scoreThreeRule));
                dataMap.put("score_three_value", scoreThreeValue);
                dataMap.put("order_num", index);
                dataMapList.add(dataMap);
            }
            index++;
        }
        baseService.insertBatch("evaluate_score", dataMapList);
        // 5. 关闭文件流
        workbook.close();
        fileInputStream.close();
    }

    /**
     * 组合评估得分数据
     */
    @Test
    public void treeTest() {
        String scoreTime = "2025-01";
        String rootId = ConvertUtils.UUID("root");
        int orderNum = 10;
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from evaluate_score_20250228", new QueryWrapper<>().eq("score_time", scoreTime));
        for (Map<String, Object> item : queryMapList0) {
            String scoreOne = ConvertUtils.getString(item.get("score_one"));
            String scoreTwo = ConvertUtils.getString(item.get("score_two"));
            String scoreThree = ConvertUtils.getString(item.get("score_three"));
            String scoreThreeElement = ConvertUtils.getString(item.get("score_three_element"));
            String scoreThreeRule = ConvertUtils.getString(item.get("score_three_rule"));
            int scoreThreeValue = ConvertUtils.getInt(item.get("score_three_value"));
            // 1
            Map<String, Object> dataMap0 = new HashMap<>();
            dataMap0.put("id", ConvertUtils.UUID(scoreOne));
            dataMap0.put("parent_id", rootId);
            dataMap0.put("score_time", scoreTime);
            dataMap0.put("score_desc_id", scoreOne);
            dataMap0.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("evaluate_score", dataMap0, BaseConstant.INSERT_IGNORE);
            // 2
            Map<String, Object> dataMap1 = new HashMap<>();
            dataMap1.put("id", ConvertUtils.UUID(scoreTwo + scoreOne));
            dataMap1.put("parent_id", ConvertUtils.UUID(scoreOne));
            dataMap1.put("score_time", scoreTime);
            dataMap1.put("score_desc_id", scoreTwo);
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("evaluate_score", dataMap1, BaseConstant.INSERT_IGNORE);
            // 3
            Map<String, Object> dataMap2 = new HashMap<>();
            dataMap2.put("id", ConvertUtils.UUID(scoreThree + scoreTwo + scoreOne));
            dataMap2.put("parent_id", ConvertUtils.UUID(scoreTwo + scoreOne));
            dataMap2.put("score_time", scoreTime);
            dataMap2.put("score_desc_id", scoreThree);
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("evaluate_score", dataMap2, BaseConstant.INSERT_IGNORE);
            // 4
            Map<String, Object> dataMap3 = new HashMap<>();
            dataMap3.put("id", ConvertUtils.UUID(scoreThreeElement + scoreThree + scoreTwo + scoreOne));
            dataMap3.put("parent_id", ConvertUtils.UUID(scoreThree + scoreTwo + scoreOne));
            dataMap3.put("score_time", scoreTime);
            dataMap3.put("score_desc_id", scoreThreeElement);
            dataMap3.put("score_value", scoreThreeValue);
            dataMap3.put("score_remark", scoreThreeRule);
            dataMap3.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("evaluate_score", dataMap3, BaseConstant.INSERT_IGNORE);
        }
        baseService.insert("evaluate_score", new HashMap<String, Object>() {{
            put("id", rootId);
            put("score_time", scoreTime);
            put("score_desc_id", "root");
            put("order_num", 0);
        }}, BaseConstant.INSERT_IGNORE);
    }

    /**
     * 碳中和定性描述
     */
    @Test
    public void readExcel2Test() throws Exception {
        // 1. 创建输入流，读取Excel文件
        FileInputStream fileInputStream = new FileInputStream("E:\\Company\\kingtrol\\033-津沽\\碳中和分析\\定性分析.xlsx");
        // 2. 使用 XSSFWorkbook 解析Excel文件（.xlsx 格式）
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        // 3. 获取第一个工作表
        Sheet sheet = workbook.getSheetAt(0);
        List<List<String>> excelDataList = new ArrayList<>();
        for (Row row : sheet) {
            List<String> tmpList = new ArrayList<>();
            tmpList.add(row.getCell(0).getStringCellValue());
            tmpList.add(row.getCell(1).getStringCellValue());
            tmpList.add(row.getCell(2).getStringCellValue());
            tmpList.add(row.getCell(3).getStringCellValue());
            excelDataList.add(tmpList);
        }
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        for (int i = 0; i < excelDataList.size(); i++) {
            if (StringUtils.isNotEmpty(excelDataList.get(i).get(0))) {
                Map<String, Object> dataMap0 = new HashMap<>();
                dataMap0.put("id", "A" + i);
                dataMap0.put("qualitative_desc", excelDataList.get(i).get(0));
                dataMapList.add(dataMap0);
            }
            if (StringUtils.isNotEmpty(excelDataList.get(i).get(1))) {
                Map<String, Object> dataMap1 = new HashMap<>();
                dataMap1.put("id", "B" + i);
                dataMap1.put("qualitative_desc", excelDataList.get(i).get(1));
                dataMapList.add(dataMap1);
            }
            if (StringUtils.isNotEmpty(excelDataList.get(i).get(2))) {
                Map<String, Object> dataMap2 = new HashMap<>();
                dataMap2.put("id", "C" + i);
                dataMap2.put("qualitative_desc", excelDataList.get(i).get(2));
                dataMapList.add(dataMap2);
            }
            if (StringUtils.isNotEmpty(excelDataList.get(i).get(3))) {
                Map<String, Object> dataMap3 = new HashMap<>();
                dataMap3.put("id", "D" + i);
                dataMap3.put("qualitative_desc", excelDataList.get(i).get(3));
                dataMapList.add(dataMap3);
            }
        }
        baseService.insertBatch("carbon_qualitative_desc", dataMapList);
        // 5. 关闭文件流
        workbook.close();
        fileInputStream.close();
    }

    /**
     * 碳中和定性树
     */
    @Test
    public void tree2Test() throws Exception {
        String qualitativeTime = "2025-01";
        String rootId = ConvertUtils.UUID("root");
        int orderNum = 10;
        // 翻译
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from carbon_qualitative_desc", new QueryWrapper<>());
        Map<String, String> dictMap = queryMapList0.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("qualitative_desc")), item -> ConvertUtils.getString(item.get("id"))));
        List<Map<String, Object>> queryMapList1 = baseService.queryForList("select * from demo_excel_carbon_qualitative", new QueryWrapper<>());
        for (Map<String, Object> item : queryMapList1) {
            String one = ConvertUtils.getString(item.get("a0"));
            String two = ConvertUtils.getString(item.get("a1"));
            String three = ConvertUtils.getString(item.get("a2"));
            String four = ConvertUtils.getString(item.get("a3"));
            double value = ConvertUtils.getDouble(item.get("a4"), 0);
            // 1
            Map<String, Object> dataMap0 = new HashMap<>();
            dataMap0.put("id", ConvertUtils.UUID(one));
            dataMap0.put("parent_id", rootId);
            dataMap0.put("qualitative_time", qualitativeTime);
            dataMap0.put("qualitative_desc_id", dictMap.get(one));
            dataMap0.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap0, BaseConstant.INSERT_IGNORE);
            // 2
            Map<String, Object> dataMap1 = new HashMap<>();
            dataMap1.put("id", ConvertUtils.UUID(two + one));
            dataMap1.put("parent_id", ConvertUtils.UUID(one));
            dataMap1.put("qualitative_time", qualitativeTime);
            dataMap1.put("qualitative_desc_id", dictMap.get(two));
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap1, BaseConstant.INSERT_IGNORE);
            // 3
            Map<String, Object> dataMap2 = new HashMap<>();
            dataMap2.put("id", ConvertUtils.UUID(three + two + one));
            dataMap2.put("parent_id", ConvertUtils.UUID(two + one));
            dataMap2.put("qualitative_time", qualitativeTime);
            dataMap2.put("qualitative_desc_id", dictMap.get(three));
            if (StringUtils.isEmpty(four)) {
                dataMap2.put("qualitative_value", value);
            }
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap2, BaseConstant.INSERT_IGNORE);
            // 4
            if (StringUtils.isEmpty(four)) {
                continue;
            }
            Map<String, Object> dataMap3 = new HashMap<>();
            dataMap3.put("id", ConvertUtils.UUID(four + three + two + one));
            dataMap3.put("parent_id", ConvertUtils.UUID(three + two + one));
            dataMap3.put("qualitative_time", qualitativeTime);
            dataMap3.put("qualitative_desc_id", dictMap.get(four));
            dataMap3.put("qualitative_value", value);
            dataMap3.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap3, BaseConstant.INSERT_IGNORE);
        }
        baseService.insert("carbon_qualitative", new HashMap<String, Object>() {{
            put("id", rootId);
            put("qualitative_time", qualitativeTime);
            put("qualitative_desc_id", "root");
            put("order_num", 0);
        }}, BaseConstant.INSERT_IGNORE);
    }

    @Test
    public void tree3Test() throws Exception {
        String qualitativeTime = "2025-01";
        String rootId = ConvertUtils.UUID("root");
        int orderNum = 10;
        // 翻译
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from carbon_qualitative_desc", new QueryWrapper<>());
        Map<String, String> dictMap = queryMapList0.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("qualitative_desc")), item -> ConvertUtils.getString(item.get("id"))));
        // 1. 创建输入流，读取Excel文件
        FileInputStream fileInputStream = new FileInputStream("E:\\Company\\kingtrol\\033-津沽\\碳中和分析\\定性分析.xlsx");
        // 2. 使用 XSSFWorkbook 解析Excel文件（.xlsx 格式）
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        // 3. 获取第一个工作表
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            String one = row.getCell(0).getStringCellValue();
            String two = row.getCell(1).getStringCellValue();
            String three = row.getCell(2).getStringCellValue();
            String four = row.getCell(3).getStringCellValue();
            double value = row.getCell(4).getNumericCellValue();
            // 1
            Map<String, Object> dataMap0 = new HashMap<>();
            dataMap0.put("id", one);
            dataMap0.put("parent_id", rootId);
            dataMap0.put("qualitative_time", qualitativeTime);
            dataMap0.put("qualitative_desc_id", one);
            dataMap0.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap0, BaseConstant.INSERT_IGNORE);
            // 2
            Map<String, Object> dataMap1 = new HashMap<>();
            dataMap1.put("id", two + one);
            dataMap1.put("parent_id", one);
            dataMap1.put("qualitative_time", qualitativeTime);
            dataMap1.put("qualitative_desc_id", two);
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap1, BaseConstant.INSERT_IGNORE);
            // 3
            Map<String, Object> dataMap2 = new HashMap<>();
            dataMap2.put("id", three + two + one);
            dataMap2.put("parent_id", two + one);
            dataMap2.put("qualitative_time", qualitativeTime);
            dataMap2.put("qualitative_desc_id", three);
            if (StringUtils.isEmpty(four)) {
                dataMap2.put("qualitative_value", value);
            }
            dataMap1.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap2, BaseConstant.INSERT_IGNORE);
            // 4
            if (StringUtils.isEmpty(four)) {
                continue;
            }
            Map<String, Object> dataMap3 = new HashMap<>();
            dataMap3.put("id", four + three + two + one);
            dataMap3.put("parent_id", three + two + one);
            dataMap3.put("qualitative_time", qualitativeTime);
            dataMap3.put("qualitative_desc_id", four);
            dataMap3.put("qualitative_value", value);
            dataMap3.put("order_num", orderNum);
            orderNum += 10;
            baseService.insert("carbon_qualitative", dataMap3, BaseConstant.INSERT_IGNORE);
        }
        baseService.insert("carbon_qualitative", new HashMap<String, Object>() {{
            put("id", rootId);
            put("qualitative_time", qualitativeTime);
            put("qualitative_desc_id", "root");
            put("order_num", 0);
        }}, BaseConstant.INSERT_IGNORE);
    }

    /**
     * 插入运行看板 数据
     */
    @Test
    public void runDataDisplayTest() {
        // 从 二沉池 开始
        Map<String, String> parentId2DataNamesMap = new LinkedHashMap<>();
        parentId2DataNamesMap.put("1902201792569143297", "泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201887486242817", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201987440701442", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202069326098434", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201817713995778", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201913641922562", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202009125253122", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202089366482945", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201844071002114", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902201939243954178", "泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202031183097858", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202109507530753", "\t\t\t泥位反馈值\n" +
                "\t\t\t低位设定\n" +
                "\t\t\t高限设定\n" +
                "\t\t\t高高限设定\n" +
                "\t\t\t运行计时\n" +
                "\t\t\t运行周期设定\n" +
                "\t\t\t停止计时\n" +
                "\t\t\t停止周期设定");
        parentId2DataNamesMap.put("1902202268618452993", "\t\t\t恒定液位设定\n" +
                "\t\t\t前级低液位设定\n" +
                "\t\t\t后级低液位设定\n" +
                "\t\t\t快速启动泵设定\n" +
                "\t\t\t手动频率给定\n" +
                "\t\t\t增减泵液位差值设定\n" +
                "\t\t\t增泵时间设定\n" +
                "\t\t\t减泵时间设定\t\n" +
                "\t\t\t增益\n" +
                "\t\t\t积分\n" +
                "\t\t\t微分");
        parentId2DataNamesMap.put("1902202304328757249", "\t\t\t恒定液位设定\n" +
                "\t\t\t前级低液位设定\n" +
                "\t\t\t后级低液位设定\n" +
                "\t\t\t快速启动泵设定\n" +
                "\t\t\t手动频率给定\n" +
                "\t\t\t增减泵液位差值设定\n" +
                "\t\t\t增泵时间设定\n" +
                "\t\t\t减泵时间设定\t\n" +
                "\t\t\t增益\n" +
                "\t\t\t积分\n" +
                "\t\t\t微分");
        parentId2DataNamesMap.put("1902202380916748290", "\t\t\t恒定液位设定\n" +
                "\t\t\t前级低液位设定\n" +
                "\t\t\t后级低液位设定\n" +
                "\t\t\t快速启动泵设定\n" +
                "\t\t\t手动频率给定\n" +
                "\t\t\t增减泵液位差值设定");
        parentId2DataNamesMap.put("1902202415490396161", "\t\t\t开1台泵低液位设定\n" +
                "\t\t\t开1台泵高液位设定\n" +
                "\t\t\t开2台泵低液位设定\n" +
                "\t\t\t开2台泵高液位设定\n" +
                "\t\t\t开3台泵低液位设定\n" +
                "\t\t\t开3台泵高液位设定\n" +
                "\t\t\t开4台泵低液位设定\n" +
                "\t\t\t开4台泵高液位设定");
        parentId2DataNamesMap.put("1902202502199242753", "\t\t\t恒定液位设定\n" +
                "\t\t\t前级低液位设定\n" +
                "\t\t\t后级低液位设定\n" +
                "\t\t\t快速启动泵设定\n" +
                "\t\t\t手动频率给定\n" +
                "\t\t\t增减泵液位差值设定");
        parentId2DataNamesMap.put("1902202536013721601", "\t\t\t开1台泵低液位设定\n" +
                "\t\t\t开1台泵高液位设定\n" +
                "\t\t\t开2台泵低液位设定\n" +
                "\t\t\t开2台泵高液位设定\n" +
                "\t\t\t开3台泵低液位设定\n" +
                "\t\t\t开3台泵高液位设定\n" +
                "\t\t\t开4台泵低液位设定\n" +
                "\t\t\t开4台泵高液位设定");
        parentId2DataNamesMap.put("1902202699876790273", "\t\t\t\tpH\n" +
                "\t\t\t\t温度\n" +
                "\t\t\t\tSS\n" +
                "\t\t\t\tTP\n" +
                "\t\t\t\t浊度");
        parentId2DataNamesMap.put("1902202734597238785", "\t\t\t\tpH\n" +
                "\t\t\t\t温度\n" +
                "\t\t\t\tSS\n" +
                "\t\t\t\tTP\n" +
                "\t\t\t\t浊度");
        parentId2DataNamesMap.put("1902202860367638530", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902202901446651905", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902203007633846273", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902203040093564930", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902203130015248386", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902203169080995842", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902203259543744514", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902203286689280002", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902203464649404418", "\t\t\t\tpH\n" +
                "\t\t\t\t温度\n" +
                "\t\t\t\tSS\n" +
                "\t\t\t\tTP\n" +
                "\t\t\t\t浊度");
        parentId2DataNamesMap.put("1902203497411112961", "\t\t\t\tpH\n" +
                "\t\t\t\t温度\n" +
                "\t\t\t\tSS\n" +
                "\t\t\t\tTP\n" +
                "\t\t\t\t浊度");
        parentId2DataNamesMap.put("1902239996517097474", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902240023285145601", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902240108995747841", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902240141568712706", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902240223835791362", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902240252461916161", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902240343759331330", "\t\t\t\t系统状态\n" +
                "\t\t\t\t排泥模式\n" +
                "\t\t\t\t排泥步序\n" +
                "\t\t\t\t进水流量反馈\n" +
                "\t\t\t\t目标循环流量\n" +
                "\t\t\t\t上次排泥后的累计流量\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t排泥时间\n" +
                "\t\t\t\t当前排泥时间设定值\n" +
                "\t\t\t\t强制排泥周期");
        parentId2DataNamesMap.put("1902240378895015937", "\t\t\t\t正常排泥时间设定\n" +
                "\t\t\t\t过扭矩排泥时间设定\n" +
                "\t\t\t\t高泥位排泥时间设定\n" +
                "\t\t\t\t过扭矩扩展排泥时间设定\n" +
                "\t\t\t\t高泥位扩展排泥时间设定\n" +
                "\t\t\t\t亚重故障延时停止时间设定\n" +
                "\t\t\t\t当前排泥间隔时间\n" +
                "\t\t\t\t循环泵故障延时时间设定\n" +
                "\t\t\t\t待机时排泥间隔时间设定\n" +
                "\t\t\t\t待机时排泥时间设定\n" +
                "\t\t\t\t强制排泥闻隔时间设定");
        parentId2DataNamesMap.put("1902240425598590977", "");
        parentId2DataNamesMap.put("1902240457605324801", "\t\t2#反冲洗泵\n" +
                "\t\t3#反冲洗泵\n" +
                "\t\t放空阀\n" +
                "\t\t1#反洗风机\n" +
                "\t\t2#反洗风机\n" +
                "\t\t3#反洗风机\n" +
                "\t\t反洗降液位设定\n" +
                "\t\t清水池低液位设定1\n" +
                "\t\t清水池低液位设定2\n" +
                "\t\t废水池高液位设定1\n" +
                "\t\t废水池高液位设定2\n" +
                "\t\t关进水阀、开出水阀延时1\n" +
                "\t\t关进水阀、开出水阀延时2\n" +
                "\t\t启反洗风机延时1\n" +
                "\t\t启反洗风机延时2\n" +
                "\t\t关放空阀、开始气洗时长1\n" +
                "\t\t关放空阀、开始气洗时长2\n" +
                "\t\t启反洗泵、气水联洗时长1\n" +
                "\t\t启反洗泵、气水联洗时长2\n" +
                "\t\t开放空阀、关气洗阀延时1\n" +
                "\t\t开放空阀、关气洗阀延时2\n" +
                "\t\t停反洗风机、开始水洗时长1\n" +
                "\t\t停反洗风机、开始水洗时长2\n" +
                "\t\t停反洗泵延时1\n" +
                "\t\t停反洗泵延时2\n" +
                "\t\t关反洗进水阀延时1\n" +
                "\t\t关反洗进水阀延时2\n" +
                "\t\t关废水阀延时1\n" +
                "\t\t关废水阀延时2\n" +
                "\t\t开进水阀、反洗完成1\n" +
                "\t\t开进水阀、反洗完成2");
        parentId2DataNamesMap.put("1902240650052575234", "\t\t系统模式\n" +
                "\t\t驱动状态\n" +
                "\t\t1#反洗风机\n" +
                "\t\t2#反洗风机\n" +
                "\t\t6#滤池驱氮次数\n" +
                "\t\t6#滤池状态\n" +
                "\t\t7#滤池驱氮次数\n" +
                "\t\t7#滤池状态\n" +
                "\t\t8#滤池驱氮次数\n" +
                "\t\t8#滤池状态\n" +
                "\t\t9#滤池驱氮次数\n" +
                "\t\t9#滤池状态\n" +
                "\t\t滤池正常滤水驱氨液位设定\n" +
                "\t\t滤池正常滤水出水阀给定\n" +
                "\t\t调节液位、开出水阀延时\n" +
                "\t\t正常驱氮延时1\n" +
                "\t\t正常驱氮延时2");
        for (Map.Entry<String, String> entry : parentId2DataNamesMap.entrySet()) {
            String parentId = entry.getKey().trim();
            String dataNames = entry.getValue();
            if (StringUtils.isEmpty(parentId) || StringUtils.isEmpty(dataNames)) {
                continue;
            }
            String[] dataNameArray = dataNames.split("\t\t");
            Date now = new Date();
            int orderNum = 10;
            for (String dataName : dataNameArray) {
                dataName = dataName.trim();
                if (StringUtils.isEmpty(dataName)) {
                    continue;
                }
                // 封装对象
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", null);
                entityMap.put("parent_id", parentId);
                entityMap.put("relation_id", "{jingu01_GXCA_Tag371}");
                entityMap.put("data_name", dataName);
                entityMap.put("data_type", "data");
                entityMap.put("default_value", "");
                entityMap.put("decimal_length", "2");
                entityMap.put("data_unit", "");
                entityMap.put("remark", "");
                entityMap.put("create_time", now);
                entityMap.put("order_num", orderNum);
                entityMap.put("del_flag", "0");
                baseService.insert("run_data_display", entityMap);
                orderNum += 10;
            }
        }
    }

    @Test
    public void runDataInstrumentTest() {
        Map<String, String> parentId2DataNamesMap = new LinkedHashMap<>();
        // 整厂
        parentId2DataNamesMap.put("1912042832918585345", "\t\t进水\n" +
                "\t\t自来水\n" +
                "\t\t未知来水\n" +
                "\t\t出水量\n" +
                "\t\t蒸发水量\n" +
                "\t\t下渗水量\n" +
                "\t\t运出污泥含水量");
        // 二级处理
        parentId2DataNamesMap.put("1912043067728306178", "\t\t\t曝气沉砂池出口流量\n" +
                "\t\t\t初沉污泥产量\n" +
                "\t\t\t剩余污泥量\n" +
                "\t\t\t中间提升流量");
        // 深度处理
        parentId2DataNamesMap.put("1912043120643645442", "\t\t\t中间提升水量\n" +
                "\t\t\t高效沉淀池出水量\n" +
                "\t\t\t化学污泥流量\n" +
                "\t\t\t臭氧氧化池进水量\n" +
                "\t\t\t水源空调站给水量\n" +
                "\t\t\t深床滤池废水量");
        // 进水提升泵房
        parentId2DataNamesMap.put("1912043399707467777", "厂外流量\n" +
                "\t\t\t除臭混合液\n" +
                "\t\t\t深床滤池废水量\n" +
                "\t\t\t一级提升流量\n" +
                "\t\t\t液位差");
        // 初沉池及污泥泵房
        parentId2DataNamesMap.put("1912043476899438593", "\t\t\t沉砂池水量\n" +
                "\t\t\t排出污泥流量\n" +
                "\t\t\t中间提升水量\n" +
                "\t\t\t剩余污泥流量");
        // 中间提升泵房
        parentId2DataNamesMap.put("1912043591739482114", "\t\t\t剩余污泥流量\n" +
                "\t\t\t液位差");
        // 高效沉淀池
        parentId2DataNamesMap.put("1912043657355173890", "\t\t\t中间提升水量\n" +
                "\t\t\tPAC药剂投加量\n" +
                "\t\t\tPAM药剂投加量\n" +
                "\t\t\t高效沉淀池出水量\n" +
                "\t\t\t化学污泥流量");
        // 反硝化深床滤池
        parentId2DataNamesMap.put("1912043707124785154", "\t\t\t高效沉淀池出水量\n" +
                "\t\t\t臭氧氧化池进水量\n" +
                "\t\t\t水源空调站给水量\n" +
                "\t\t\t深床滤池废水量");
        // 臭氧高级氧化接触池及出水泵房
        parentId2DataNamesMap.put("1912043787118551041", "\t\t\t臭氧氧化池进水量\n" +
                "\t\t\t消毒药剂投加量\n" +
                "\t\t\t出水泵房水量\n" +
                "\t\t\t至污水区的再生水量\n" +
                "\t\t\t至污泥区的再生水量\n" +
                "\t\t\t排泥流量");
        // 消防泵池
        parentId2DataNamesMap.put("1912044083215441921", "\t\t\t再来水进水量\n" +
                "\t\t\t再生水进水量\n" +
                "\t\t\t消防用水量\n" +
                "\t\t\t液位差");
        // 前贮泥池
        parentId2DataNamesMap.put("1912044248055783426", "\t\t\t前贮泥池出泥流量\n" +
                "\t\t\t生物池排泥流量\n" +
                "\t\t\t高效沉淀池排泥流量");
        // 后贮泥池
        parentId2DataNamesMap.put("1912044307778478081", "\t\t\t污泥浓缩池出泥量\n" +
                "\t\t\t初沉污泥流量\n" +
                "\t\t\t脱水机前输送污泥流量");
        // 污泥总量平衡1
        parentId2DataNamesMap.put("1912045170651668482", "\t\t\t运出的绝干污泥\n" +
                "\t\t\t初沉干污泥\n" +
                "\t\t\t剩余干污泥\n" +
                "\t\t\t化学干污泥");
        // 污泥总量平衡2
        parentId2DataNamesMap.put("1912045214251458561", "\t\t\t运出污泥量\n" +
                "\t\t\t运出污泥含水率\n" +
                "\t\t\t剩余污泥流量\n" +
                "\t\t\t污泥浓度\n" +
                "\t\t\t初沉污泥流量\n" +
                "\t\t\t初沉污泥浓度\n" +
                "\t\t\t化学污泥流量\n" +
                "\t\t\t污泥浓度");
        // COD
        parentId2DataNamesMap.put("1912045991435018241", "\t\t\tCD0\n" +
                "\t\t\tCD8\n" +
                "\t\t\tCD9");
        // BOD
        parentId2DataNamesMap.put("1912046074993942530", "\t\t\tBD0\n" +
                "\t\t\tBD9");
        // 水平衡
        parentId2DataNamesMap.put("1912046673563066370", "\t\t\t中间提升水量\n" +
                "\t\t\t药剂投加量\n" +
                "\t\t\t高效沉淀池出水量\n" +
                "\t\t\t化学污泥流量");
        for (Map.Entry<String, String> entry : parentId2DataNamesMap.entrySet()) {
            String parentId = entry.getKey().trim();
            String dataNames = entry.getValue();
            if (StringUtils.isEmpty(parentId) || StringUtils.isEmpty(dataNames)) {
                continue;
            }
            String[] dataNameArray = dataNames.split("\t\t");
            Date now = new Date();
            int orderNum = 10;
            for (String dataName : dataNameArray) {
                dataName = dataName.trim();
                if (StringUtils.isEmpty(dataName)) {
                    continue;
                }
                // 封装对象
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", null);
                entityMap.put("parent_id", parentId);
                entityMap.put("relation_id", "{jingu01_GXCA_Tag371}");
                entityMap.put("data_name", dataName);
                entityMap.put("data_type", "data");
                entityMap.put("remark", "");
                entityMap.put("create_time", now);
                entityMap.put("order_num", orderNum);
                entityMap.put("del_flag", "0");
                baseService.insert("run_data_instrument", entityMap);
                orderNum += 10;
            }
        }
    }

    @Test
    public void wordTest() throws Exception {
        // 加载文档
        Document doc = new Document("E:\\Downloads\\test01.docx");

        // 获取文档中的第一个表格
        Table table = (Table) doc.getChild(NodeType.TABLE, 0, true);

        if (table != null) {
            // 假设要删除表格的第二行（索引从0开始）
            int rowIndexToDelete = 1;
            if (rowIndexToDelete < table.getRows().getCount()) {
                com.aspose.words.Row rowToDelete = table.getRows().get(rowIndexToDelete);
                rowToDelete.remove();
            }
        }

        // 保存修改后的文档
        doc.save("E:\\Downloads\\test01_v1.docx");
    }

    /**
     * 插入新的报表
     */
    @Test
    public void newReportTest() {
        // 查询数据项
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("report_id", "5101948d8a2e893012889dca94a5cd2e")
                .likeRight("item_alias", "碳中和_")
                .orderByAsc("sort_num");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from f_report_item", queryWrapper0);
        for (Map<String, Object> item : queryMapList0) {
            String id = ConvertUtils.getString(item.get("id"));
            String reportId = ConvertUtils.getString(item.get("report_id"));
            String itemAlias = ConvertUtils.getString(item.get("item_alias"));
            String itemCode = ConvertUtils.getString(item.get("item_code"));
            // 插入f_report_custom_item
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("id", null);
            entityMap.put("parent_field_code", "");
            entityMap.put("config_datasource_type", "1");
            entityMap.put("edit_tag", "1");
            entityMap.put("field_code", itemCode);
            entityMap.put("field_name", itemAlias);
            entityMap.put("field_type", "number");
            entityMap.put("report_id", "1904353157014716418");
            entityMap.put("report_item_id", id);
            entityMap.put("report_item_name", reportId + "," + id);
            entityMap.put("scale", "2");
            baseService.insert("f_report_custom_item", entityMap);
        }
    }

    @Test
    public void jsonMapTest() {
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from run_data_instrument", new QueryWrapper<>().like("data_name", "浓度"));
        for (Map<String, Object> item : queryMapList0) {
            String id = ConvertUtils.getString(item.get("id"));
            String relationId = ConvertUtils.getString(item.get("relation_id"));
            List<String> tmpCodeList = ExpressionUtils.parseExpression(relationId);
            Map<String, String> tmpMap = new HashMap<>();
            tmpMap.put(tmpCodeList.get(0), "avg");
            String jsonStr = JSONUtil.toJsonStr(tmpMap);
            UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
            updateWrapper0.set("data_value_type", jsonStr)
                    .eq("id", id);
            baseService.update("run_data_instrument", updateWrapper0);
        }
    }

    /**
     * 药耗分析报表 数据项转换
     */
    @Test
    public void algorithmTest() {
        // 统计编码
        List<String> statisticsodeList = new ArrayList<String>() {{
            add("YHFX-CLYJDSYH-Y");
            add("YHFX-DSYJXHQS-Y-CL");
            add("YHFX-DSYJXHMX-SZ-Y-CLYJ");
            add("YHFX-TYYCOD-TN-Y");
            add("YHFX-CLYJYTPQCL-Y");
            add("YHFX-WNYJYWNL-Y");
            add("YHFX-CY-SYSTJYCODQCL-Y");
            add("YHFX-TYYJDSYH-Y");
            add("YHFX-DSYJXHMX-TJT-Y-CL");
            add("YHFX-XDYJDSYH-Y");
            add("YHFX-WNTSYJDSYH-Y");
        }};
        // 1. 获取 公式id
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.in("statistics_code", statisticsodeList).isNotNull("algorithm_id");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from sys_algorithm_statistics_library", queryWrapper0);
        List<String> algorithmIdList = new ArrayList<>();
        for (Map<String, Object> item : queryMapList0) {
            String algorithmId = ConvertUtils.getString(item.get("algorithm_id"));
            algorithmIdList.addAll(Arrays.asList(algorithmId.split(",")));
        }
        // 2. 获取 公式数据项
        QueryWrapper<?> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("library_id", algorithmIdList);
        List<Map<String, Object>> queryMapList1 = baseService.queryForList("select * from sys_algorithm_library_detail", queryWrapper1);
        List<Map<String, Object>> reportConvertFormList = new ArrayList<>();
        Set<String> convertCodeSet = new HashSet<>();
        for (Map<String, Object> item: queryMapList1) {
            String indexCode = ConvertUtils.getString(item.get("index_code"));
            String realIndexCode = ConvertUtils.getString(item.get("real_index_code"));
            if (convertCodeSet.contains(indexCode)) {
                continue;
            }
            convertCodeSet.add(indexCode);
            // 插入对象
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("id", null);
            // 后续生成报表后修改
            entityMap.put("report_id", "123456789");
            entityMap.put("convert_code", indexCode);
            entityMap.put("metric_info_id", realIndexCode);
            reportConvertFormList.add(entityMap);
        }
        baseService.insertBatch("report_convert_form", reportConvertFormList);
    }

}
