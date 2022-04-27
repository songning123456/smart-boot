package com.sonin.modules.file.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.modules.file.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
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
 * Excel导入测试
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 10:32
 */
@Service("importTest")
@Slf4j
public class ImportTestExcelServiceImpl implements ExcelService {

    @Override
    public Result handle(HttpServletRequest request) throws Exception {
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

}
