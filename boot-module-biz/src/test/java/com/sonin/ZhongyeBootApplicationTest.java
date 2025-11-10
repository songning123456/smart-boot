package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import com.sonin.utils.ExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author：sonin
 * @Date：2025/2/28 13:07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
@ActiveProfiles("zhongye")
public class ZhongyeBootApplicationTest {

    @Autowired
    private IMPPService baseService;

    /**
     * 考试得分排名
     */
    @Test
    public void scoreRankTest() {
        // 查询所有的试卷
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select distinct paper_id from edu_user_paper", new QueryWrapper<>());
        // 更新排名
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        for (Map<String, Object> item : queryMapList0) {
            String paperId = ConvertUtils.getString(item.get("paper_id"));
            String sql1 = "UPDATE edu_user_paper up, ( SELECT a.user_id, ( SELECT count( DISTINCT cast( b.score AS DECIMAL ( 5, 2 ) ) ) + 1 FROM edu_user_paper b WHERE cast( b.score AS DECIMAL ( 5, 2 ) ) > cast( a.score AS DECIMAL ( 5, 2 ) ) AND b.paper_id = '{paperId}' ) sort FROM edu_user_paper a WHERE a.paper_id = '{paperId}' ) aaa SET up.rank = aaa.sort WHERE up.paper_id = '{paperId}' AND up.user_id = aaa.user_id;";
            String sql = sql1.replaceAll("\\{paperId}", paperId);
            masterDB.execute(sql);
        }
    }

    @Test
    public void syncFactoryInfoTest() {
        // 查询所有的用户信息
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select sys_user.*, sys_user_depart.dep_id from sys_user left join sys_user_depart on sys_user.id = sys_user_depart.user_id", new QueryWrapper<>());
        // 查询所有水厂信息
        List<Map<String, Object>> queryMapList1 = baseService.queryForList("select * from sys_factory_user_info", new QueryWrapper<>());
        List<String> userIdList = queryMapList1.stream().map(item -> ConvertUtils.getString(item.get("user_id"))).collect(Collectors.toList());
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        for (Map<String, Object> item : queryMapList0) {
            String userId = ConvertUtils.getString(item.get("id"));
            // 如果不存在，则插入
            if (!userIdList.contains(userId)) {
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", null);
                entityMap.put("depart_id", ConvertUtils.getString(item.get("dep_id")));
                entityMap.put("user_name", ConvertUtils.getString(item.get("realname")));
                entityMap.put("user_code", "");
                entityMap.put("telephone", ConvertUtils.getString(item.get("phone")));
                entityMap.put("position", "");
                entityMap.put("email", "");
                entityMap.put("degree", "");
                entityMap.put("work_no", "");
                entityMap.put("user_age", "");
                entityMap.put("sex", ConvertUtils.getString(item.get("sex")));
                entityMap.put("user_id", userId);
                entityMap.put("user_contract", "");
                entityMap.put("user_img", "");
                entityMap.put("entry_date", "");
                entityMap.put("contract_enddate", "");
                entityMap.put("remark", "系统用户补录");
                entityMapList.add(entityMap);
            }
        }
        if (!entityMapList.isEmpty()) {
            baseService.insertBatch("sys_factory_user_info", entityMapList);
        }
    }

    @Test
    public void eduRankTest() {
        // 查询所有的试卷
        List<Map<String, Object>> queryMapList = baseService.queryForList("select distinct paper_id from edu_user_paper", new QueryWrapper<>());
        // 遍历
        for (Map<String, Object> item : queryMapList) {
            String paperId = ConvertUtils.getString(item.get("paper_id"));
            QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
            queryWrapper0.eq("paper_id", paperId);
            // 查询所有的用户试卷信息
            List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from edu_user_paper", queryWrapper0);
            // 排序，按照得分和考试时间排序
            queryMapList0.sort((o1, o2) -> {
                double score1 = ConvertUtils.getDouble(o1.get("score"), 0D);
                double score2 = ConvertUtils.getDouble(o2.get("score"), 0D);
                if (score1 > score2) {
                    return -1;
                } else if (score1 < score2) {
                    return 1;
                } else {
                    long costTime1 = DateUtils.dateStr2Sec(ConvertUtils.getString(o1.get("end_time")), BusinessConstant.DATE_FORMAT) - DateUtils.dateStr2Sec(ConvertUtils.getString(o1.get("start_time")), BusinessConstant.DATE_FORMAT);
                    long costTime2 = DateUtils.dateStr2Sec(ConvertUtils.getString(o2.get("end_time")), BusinessConstant.DATE_FORMAT) - DateUtils.dateStr2Sec(ConvertUtils.getString(o2.get("start_time")), BusinessConstant.DATE_FORMAT);
                    return Long.compare(costTime1, costTime2);
                }
            });
            for (int i = 0; i < queryMapList0.size(); i++) {
                String id = ConvertUtils.getString(queryMapList0.get(i).get("id"));
                int rank = i + 1;
                // 更新排名
                UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                updateWrapper0.set("rank", rank).eq("id", id);
                baseService.update("edu_user_paper", updateWrapper0);
            }
        }
    }

    /**
     * 整理表结构到excel
     */
    @Test
    public void tableTest() throws Exception {
        // 操作前请先复制一份原始excel。
        // excel文件全路径
        String srcFilePath = "E:\\Company\\kingtrol\\034-中冶\\关于进一步推进中国中冶数据治理工作的通知_28720250610172047\\附件二 中国中冶数据资源目录清单-数字化企业大脑000.xlsx";
        String targetFilePath = "E:\\Company\\kingtrol\\034-中冶\\关于进一步推进中国中冶数据治理工作的通知_28720250610172047\\数字化企业大脑" + System.currentTimeMillis() / 1000 + ".xlsx";
        FileInputStream srcFileInputStream = null;
        FileOutputStream targetFileOutputStream = null;
        Workbook srcWorkbook = null, targetWorkbook = null;
        try {
            srcFileInputStream = new FileInputStream(srcFilePath);
            targetFileOutputStream = new FileOutputStream(targetFilePath);
            // 根据文件扩展名判断Excel版本
            if (srcFilePath.toLowerCase().endsWith(".xlsx")) {
                // 处理XLSX文件
                srcWorkbook = new XSSFWorkbook(srcFileInputStream);
                targetWorkbook = new XSSFWorkbook();
            } else if (srcFilePath.toLowerCase().endsWith(".xls")) {
                // 处理XLS文件
                srcWorkbook = new HSSFWorkbook(srcFileInputStream);
                targetWorkbook = new HSSFWorkbook();
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持.xls和.xlsx格式");
            }
            // 开始解析并组装excel
            Sheet srcSheet = srcWorkbook.getSheetAt(2);
            Sheet targetSheet = targetWorkbook.createSheet("000");
            // 需要过滤的字段
            List<String> srcIgnoreFieldList = new ArrayList<String>() {{
                add("id");
                add("create_time");
                add("create_by");
                add("update_time");
                add("update_by");
                add("del_flag");
                add("depart_id");
                add("create_dept");
                add("create_cmpy");
            }};
            // 需要复制的cell序号
            List<Integer> copyCellIndexList = new ArrayList<Integer>() {{
                add(1);
                add(2);
                add(3);
                add(4);
                add(5);
                add(6);
                add(12);
                add(17);
                add(19);
                add(20);
                add(21);
                add(24);
            }};
            // 需要翻译的字段
            Map<String, String> table2ColumnDictMap = new HashMap<String, String>() {{
                put("ajh_edu_training=>edu_tra_type", "eduType");
                put("ajh_edu_training_record=>edu_tra_lx", "edu_tra_lx");
                put("ajh_leader_inspection_record=>inspection_type", "ajh_inspection_type");
                put("ajh_rectification_info=>rec_depart", "rec_depart");
                put("ajh_rectification_info=>rec_ord_classify", "rectification_info_classify");
                put("ajh_rectification_info=>rec_ord_level", "rectification_info_level");
                put("ajh_rectification_info=>handle_result", "rectification_info_handleresult");
                put("ajh_rectification_info=>rec_ord_status", "recordStatus");
                put("danger_level_manage=>danger_type", "dangerType");
                put("danger_level_manage=>danger_level", "dangerLevel");
                put("danger_level_manage=>danger_rank", "dangerRank");
                put("f_major_issues_process=>cycle_unit", "loop_unit");
                put("f_major_issues_process=>status", "meetting_status");
                put("f_major_issues_process=>issue_type", "MajorIssuesProcess_issueType");
                put("emergency_plan_data=>doc_type", "plan_type");
                put("emergency_plan_data=>rectification_info_classify", "rectification_info_classify");
                put("emergency_plan_data=>module_type", "moduleType");
                put("emergency_plan_data=>is_local_filing", "yn");
                put("emergency_plan_data=>plan_type", "plan_type");
                put("emergency_plan_data=>application_type", "application_type");
                put("material_info=>fac_informationtype", "fac_information_type");
                put("material_info=>rules_type", "rules_type");
                put("material_info=>laws_type", "laws_type");
                put("material_info=>laws_source", "laws_source");
                put("material_info=>standards_type", "standards_type");
                put("material_info=>standards_source", "standards_source");
                put("material_info=>effective_status", "ajh_effectiveStatus");
                put("material_info=>status", "purchase_plan_status");
                put("safety_health_records=>inspect_result", "health_records_inspection_results");
                put("sys_factory_user_info=>sex", "sex");
            }};
            int targetIndex = 0;
            // 从 数据行 开始
            for (int srcI = 3; srcI < srcSheet.getLastRowNum(); srcI++) {
                Row srcRow = srcSheet.getRow(srcI);
                // 获取表名
                String tmpTableName = ConvertUtils.getString(srcRow.getCell(6));
                if (StringUtils.isEmpty(tmpTableName)) {
                    continue;
                }
                // 查询表的字段
                List<Map<String, Object>> tmpQueryMapList = baseService.queryForList("SHOW FULL COLUMNS FROM " + tmpTableName, new QueryWrapper<>());
                // 在目标excel中插入表结构数据
                for (Map<String, Object> item : tmpQueryMapList) {
                    String tmpField = ConvertUtils.getString(item.get("Field"));
                    String tmpType = ConvertUtils.getString(item.get("Type"));
                    String tmpComment = ConvertUtils.getString(item.get("Comment"));
                    if (srcIgnoreFieldList.contains(tmpField)) {
                        continue;
                    }
                    Row targetRow = targetSheet.createRow(targetIndex);
                    // 设置常量cell
                    for (Cell tmpCell : srcRow) {
                        if (copyCellIndexList.contains(tmpCell.getColumnIndex())) {
                            targetRow.createCell(tmpCell.getColumnIndex()).setCellValue(tmpCell.getStringCellValue());
                        } else {
                            // 如果不需要复制的字段，则设置为空
                            targetRow.createCell(tmpCell.getColumnIndex()).setCellValue("");
                        }
                    }
                    // 变量cell
                    targetRow.createCell(7).setCellValue(tmpComment);
                    targetRow.createCell(8).setCellValue(tmpField);
                    if (tmpType.contains("char")) {
                        targetRow.createCell(10).setCellValue("文本类");
                        if (tmpType.contains("(") && tmpType.contains(")")) {
                            // 提取长度
                            String lengthStr = tmpType.substring(tmpType.indexOf("(") + 1, tmpType.indexOf(")"));
                            targetRow.createCell(11).setCellValue(Integer.parseInt(lengthStr));
                        }
                    } else if (tmpType.contains("datetime")) {
                        targetRow.createCell(10).setCellValue("日期类");
                    } else if (tmpType.contains("int") || tmpType.contains("decimal") || tmpType.contains("float")) {
                        targetRow.createCell(10).setCellValue("数字类");
                    } else {
                        targetRow.createCell(10).setCellValue("");
                    }
                    // 设置翻译cell
                    String tmpDictKey = tmpTableName + "=>" + tmpField;
                    if (table2ColumnDictMap.containsKey(tmpDictKey)) {
                        List<Map<String, Object>> tmpDictMapList = baseService.queryForList("select item_text as key0, item_value as value0 from sys_dict_item left join sys_dict on sys_dict_item.dict_id = sys_dict.id", new QueryWrapper<>().eq("sys_dict.dict_code", table2ColumnDictMap.get(tmpDictKey)));
                        List<String> tmpDictList = tmpDictMapList.stream().map(it -> it.get("value0") + ":" + it.get("key0")).collect(Collectors.toList());
                        targetRow.createCell(9).setCellValue(String.join(";", tmpDictList));
                        targetRow.getCell(10).setCellValue("代码类");
                    }
                    targetIndex++;
                }
                // 每个表之间留一行空行
                targetIndex++;
            }
            targetWorkbook.write(targetFileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (srcWorkbook != null) {
                srcWorkbook.close();
            }
            if (targetWorkbook != null) {
                targetWorkbook.close();
            }
            if (srcFileInputStream != null) {
                srcFileInputStream.close();
            }
            if (targetFileOutputStream != null) {
                targetFileOutputStream.close();
            }
        }
    }

    /**
     * 导入新的物料信息
     */
    @Test
    public void equipmentSparepartTest() throws Exception {
        // excel文件全路径
//        String fileName = "E:\\Company\\kingtrol\\034-中冶\\物料信息导入20250704\\物料信息- 导入类别v1.xlsx";
        String fileName = "E:\\Company\\kingtrol\\034-中冶\\物料信息导入20250704\\物料信息导入类别wh1107v2.xlsx";
        String remarkPrefix = "20251107";
        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            // 根据文件扩展名判断Excel版本
            if (fileName.toLowerCase().endsWith(".xlsx")) {
                // 处理XLSX文件
                workbook = new XSSFWorkbook(fileInputStream);
            } else if (fileName.toLowerCase().endsWith(".xls")) {
                // 处理XLS文件
                workbook = new HSSFWorkbook(fileInputStream);
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持.xls和.xlsx格式");
            }
            // 开始解析并组装excel
            Sheet sheet0 = workbook.getSheetAt(0);
            // 结果集
            List<Map<String, Object>> entityMapList = new ArrayList<>();
            for (Row row : sheet0) {
                String sparepart_type = ConvertUtils.getString(row.getCell(1));
                if (!sparepart_type.startsWith("16") && !sparepart_type.startsWith("17") && !sparepart_type.startsWith("18") && !sparepart_type.startsWith("19")) {
                    continue;
                }
                String sparepart_name = ConvertUtils.getString(row.getCell(3));
                String sparepart_code = ConvertUtils.getString(row.getCell(4));
                String specification = ConvertUtils.getString(row.getCell(5));
                String measuring_unit = ConvertUtils.getString(row.getCell(6));
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", ConvertUtils.UUID(sparepart_type + sparepart_name + sparepart_code + specification + measuring_unit));
                log.info("id:{}=>{}", ConvertUtils.UUID(sparepart_type + sparepart_name + sparepart_code + specification + measuring_unit), sparepart_type + sparepart_name + sparepart_code + specification + measuring_unit);
                entityMap.put("sparepart_type", sparepart_type);
                entityMap.put("sparepart_name", sparepart_name);
                entityMap.put("sparepart_code", sparepart_code);
                entityMap.put("specification", specification);
                entityMap.put("measuring_unit", measuring_unit);
                entityMap.put("remark", remarkPrefix + "新增");
                entityMapList.add(entityMap);
            }
            baseService.insertBatch("equipment_sparepart_supplies", entityMapList, MPPConstant.INSERT_IGNORE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (workbook != null) {
                workbook.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    @Test
    public void reportItemvSyncExcelTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\034-中冶\\报表数据同步\\报表数据同步v1.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        List<String> sheetNameList = new ArrayList<String>() {{
            add("碳中和");
        }};
        for (String curSheetName : sheetNameList) {
            XSSFSheet curSheet = workbook.getSheet(curSheetName);
            // 从第1行开始，过滤标题行
            List<Map<String, Object>> entityMapList = new ArrayList<>();
            for (int i = 1; i <= curSheet.getLastRowNum(); i++) {
                Row curRow = curSheet.getRow(i);
                // String srcReportId = ConvertUtils.getString(curRow.getCell(1));
                String srcItemId = ConvertUtils.getString(curRow.getCell(2));
                if (StringUtils.isEmpty(srcItemId)) {
                    continue;
                }
                if (!srcItemId.contains("{")) {
                    srcItemId = "{" + srcItemId + "}";
                }
                // 查询src_report_id
                List<String> tmpSrcItemIdList = ExpressionUtils.parseExpression(srcItemId);
                List<Map<String, Object>> tmpReportIdQueryMapList = baseService.queryForList("select distinct report_id from f_report_item", new QueryWrapper<>().in("id", tmpSrcItemIdList));
                String srcReportId = tmpReportIdQueryMapList.stream().map(item -> ConvertUtils.getString(item.get("report_id"))).collect(Collectors.joining(","));
                String srcDateFormat = ConvertUtils.getString(curRow.getCell(3));
                String srcSyncType = ConvertUtils.getString(curRow.getCell(4));
                String targetItemId = ConvertUtils.getString(curRow.getCell(5));
                String targetDateFormat = ConvertUtils.getString(curRow.getCell(6));
                String syncFlag = ConvertUtils.getString(curRow.getCell(7));
                if (!syncFlag.equals("是")) {
                    continue;
                }
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", targetItemId);
                entityMap.put("src_report_id", srcReportId);
                entityMap.put("src_item_id", srcItemId);
                entityMap.put("src_date_format", srcDateFormat);
                entityMap.put("src_sync_type", srcSyncType);
                entityMap.put("target_item_id", targetItemId);
                entityMap.put("target_date_format", targetDateFormat);
                entityMap.put("create_by", curSheetName);
                entityMapList.add(entityMap);
            }
            if (!entityMapList.isEmpty()) {
                baseService.insertBatch("f_report_itemv_sync", entityMapList, MPPConstant.REPLACE);
            }
        }
    }

    /**
     * 恩菲数据吨改成kg
     */
    @Test
    public void updateEnfeiDataTest() {
        // 查询所有恩菲机构
        QueryWrapper<?> enfeiQueryWrapper = new QueryWrapper<>();
        enfeiQueryWrapper.eq("item_type", "nhrb");
        List<Map<String, Object>> enfeiMapList = DataSourceTemplate.execute("nf-db", () -> baseService.queryForList("select distinct depart_id from day_report_data", enfeiQueryWrapper));
        List<String> departIdList = enfeiMapList.stream().map(item -> ConvertUtils.getString(item.get("depart_id"))).collect(Collectors.toList());
        // 查询所有itemv数据
        List<String> itemCodeList = new ArrayList<String>() {{
            add("PACGT");
            add("PAMZ");
            add("PAMF");
            add("PAMRJ");
            add("SH");
        }};
        String updateBy = "sonin20251110";
        String inItemCodeStr = itemCodeList.stream().map(item -> "'" + item + "'").collect(Collectors.joining(","));
        QueryWrapper<?> itemvQueryWrapper = new QueryWrapper<>();
        itemvQueryWrapper.in("depart_id", departIdList)
                .inSql("reit_id", "select id from f_report_item where report_id = '3a243d5715b9e1a3753c180872ca0df9' and item_code in (" + inItemCodeStr + ")");
        List<Map<String, Object>> itemvMapList = baseService.queryForList("select * from f_report_itemv", itemvQueryWrapper);
        for (Map<String, Object> item: itemvMapList) {
            String id = ConvertUtils.getString(item.get("id"));
            String itemValue = ConvertUtils.getString(item.get("item_value"));
            String newItemValue = ConvertUtils.getString(ConvertUtils.getDouble(itemValue, 0D) * 1000);
            UpdateWrapper<?> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("item_value", newItemValue)
                    .set("update_by", updateBy)
                    .eq("id", id);
            baseService.update("f_report_itemv", updateWrapper);
        }
    }

}
