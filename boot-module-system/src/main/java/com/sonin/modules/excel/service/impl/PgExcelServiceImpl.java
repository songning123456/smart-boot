package com.sonin.modules.excel.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.excel.service.ExcelService;
import com.sonin.utils.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * <pre>
 * 马鞍山江东中铁水务( d49416cc5b304b21839dbc2bfc1c3f23 => masjd_count )
 * 广德中铁经开水务( 263ff89fb61849be954acf2088f70ee1 => gdjk_count )
 * 进出厂的水质、水量，生化池的所有仪表数据，风机的所有数据、加药间的所有数据、回流泵的所有数据；时间范围：一年；时间粒度：小时
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/9/6 8:59
 */
@Service
public class PgExcelServiceImpl extends ExcelService {

    @Value(value = "${boot.path.fileUpload}")
    private String fileUploadPath;

    @Override
    public Result importHandle(HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public Workbook exportHandle(HttpServletRequest request) throws Exception {
        JdbcTemplate mysqlDB = (JdbcTemplate) SpringContext.getBean("master");
        JdbcTemplate pgDB = (JdbcTemplate) SpringContext.getBean("zt-pg");
        Map<String, String> excelMap = new LinkedHashMap<>();
        // 初始化
        if ("1".equals(request.getParameter("init"))) {
            mysqlDB.update("update sys_monitor_metric_info set column32 = '0'");
        }
        Map<String, String> factoryId2TableMap = new HashMap<String, String>() {{
            put("d49416cc5b304b21839dbc2bfc1c3f23", "masjd_count");
            put("263ff89fb61849be954acf2088f70ee1", "gdjk_count");
        }};
        Map<String, String> factoryId2NameMap = new HashMap<String, String>() {{
            put("d49416cc5b304b21839dbc2bfc1c3f23", "马鞍山江东中铁水务");
            put("263ff89fb61849be954acf2088f70ee1", "广德中铁经开水务");
        }};
        // todo 可以修改
        String factoryId;
        if (StringUtils.isNotEmpty(request.getParameter("factoryId"))) {
            factoryId = request.getParameter("factoryId");
        } else {
            factoryId = "d49416cc5b304b21839dbc2bfc1c3f23";
        }
        String table = factoryId2TableMap.getOrDefault(factoryId, "masjd_count");
        String factoryName = factoryId2NameMap.getOrDefault(factoryId, "未知");
        String sqlOne = "select id, metric_name from sys_monitor_metric_info where fac_code = '{0}' and column32 != '1'";
        sqlOne = sqlOne.replaceFirst("\\{0}", factoryId);
        List<Map<String, Object>> mapList = mysqlDB.queryForList(sqlOne);
        for (Map<String, Object> item : mapList) {
            String id = "" + item.get("id");
            String metricName = "" + item.get("metric_name");
            excelMap.put(metricName, id);
        }
        // * 查询pg数据
        String format = "yyyy-MM-dd HH:mm:ss";
        // 当前时间
        String endTime = "2022-09-01 00:00:00";
        Date currentDate = DateUtils.strToDate(endTime, format);
        String tsEnd = "" + currentDate.getTime() / 1000;
        // 去年时间
        String startTime = "2020-09-01 00:00:00";
        Date prevDate = DateUtils.strToDate(startTime, format);
        String tsStart = "" + prevDate.getTime() / 1000;
        for (Map.Entry<String, String> entry : excelMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String sqlPg = "select nm, ts, v from {table} where nm = '{value}' and ts >= '{startTime}' and ts <= '{endTime}' order by ts asc";
            sqlPg = sqlPg.replaceFirst("\\{table}", table).replaceFirst("\\{value}", value).replaceFirst("\\{startTime}", tsStart).replaceFirst("\\{endTime}", tsEnd);
            mapList = pgDB.queryForList(sqlPg);
            //创建一个文件
            try {
                File file = new File(fileUploadPath + File.separator + "zt2" + File.separator + factoryName + "_" + key + ".xls");
                file.createNewFile();
                FileOutputStream fileOutputStream = FileUtils.openOutputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook();
                // 创建sheet0
                HSSFSheet sheet0 = workbook.createSheet("sheet0");
                // 设置 居中样式
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                // 创建 第0行 => 标题行
                HSSFRow curRow = sheet0.createRow(0);
                HSSFCell curCell;
                // 0-0
                curCell = curRow.createCell(0);
                curCell.setCellValue("nm");
                curCell.setCellStyle(cellStyle);
                // 0-1
                curCell = curRow.createCell(1);
                curCell.setCellValue("ts");
                curCell.setCellStyle(cellStyle);
                // 0-2
                curCell = curRow.createCell(2);
                curCell.setCellValue("v");
                curCell.setCellStyle(cellStyle);
                int rowNum = 1;
                for (Map<String, Object> entity : mapList) {
                    curRow = sheet0.createRow(rowNum);
                    curCell = curRow.createCell(0);
                    curCell.setCellValue("" + entity.get("nm"));
                    curCell = curRow.createCell(1);
                    curCell.setCellValue("" + entity.get("ts"));
                    curCell = curRow.createCell(2);
                    curCell.setCellValue("" + entity.get("v"));
                    rowNum++;
                }
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                String sqlThree = "update sys_monitor_metric_info set column32 = '1' where id = '{id}'";
                sqlThree = sqlThree.replaceFirst("\\{id}", value);
                mysqlDB.update(sqlThree);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HSSFWorkbook();
    }

    @Override
    protected Map<String, String> getExcelMap() {
        return null;
    }
}
