package com.sonin.modules.file.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.modules.file.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Excel导入导出测试
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 10:32
 */
@Service("excelTest")
@Slf4j
public class TestExcelServiceImpl implements ExcelService {

    @Override
    public Result importHandle(HttpServletRequest request) throws Exception {
        Result result = new Result();
        MultiValueMap<String, MultipartFile> multiValueMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        for (Map.Entry<String, List<MultipartFile>> item : multiValueMap.entrySet()) {
            String name = item.getKey(); // 请求的文本参数中key值
            List<MultipartFile> fileList = item.getValue();
            // 请求的文本参数列表
            for (MultipartFile multipartFile : fileList) {
                Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
                Sheet sheet = workbook.getSheetAt(0); // 默认只有一个sheet
                int rows = sheet.getPhysicalNumberOfRows(); // 获得sheet有多少行
                // 读取第一个sheet
                for (int i = 0; i < rows; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                System.out.println(cell.toString());
                            }
                        }
                    }
                }
                workbook.close();
            }
        }
        result.setMessage("导入成功");
        return result;
    }

    @Override
    public Workbook exportHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建sheet0
        HSSFSheet hssfSheet0 = workbook.createSheet("sheet0");
        // 创建第0行
        HSSFRow row0 = hssfSheet0.createRow(0);
        // 创建第0行-0列
        HSSFCell cell0 = row0.createCell(0);
        cell0.setCellValue("测试值");
        return workbook;
    }

    @Override
    public Workbook templateHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建sheet0
        HSSFSheet hssfSheet0 = workbook.createSheet("sheet0");
        // 创建第0行
        HSSFRow row0 = hssfSheet0.createRow(0);
        // 创建第0行-0列
        HSSFCell cell0 = row0.createCell(0);
        cell0.setCellValue("测试值");
        return workbook;
    }

}
