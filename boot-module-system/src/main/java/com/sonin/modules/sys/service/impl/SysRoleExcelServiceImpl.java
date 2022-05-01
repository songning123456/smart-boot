package com.sonin.modules.sys.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.modules.file.service.ExcelService;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.service.SysRoleService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <pre>
 * SysRole 导入/导出/下载模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/30 10:16
 */
@Service("sysRole")
public class SysRoleExcelServiceImpl implements ExcelService {

    @Autowired
    private SysRoleService sysRoleService;

    private Map<String, String> sysRoleExcelMap = new LinkedHashMap<String, String>() {{
        put("名称", "name");
        put("编码", "code");
        put("备注", "remark");
    }};

    @Override
    public Result importHandle(HttpServletRequest request) throws Exception {
        Result result = new Result();
        MultiValueMap<String, MultipartFile> multiValueMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        List<String> fieldList = new ArrayList<>(sysRoleExcelMap.values());
        List<SysRole> sysRoleList = new ArrayList<>();
        SysRole sysRole;
        Field field;
        for (Map.Entry<String, List<MultipartFile>> item : multiValueMap.entrySet()) {
            String name = item.getKey(); // 请求的文本参数中key值
            List<MultipartFile> fileList = item.getValue();
            // 请求的文本参数列表
            for (MultipartFile multipartFile : fileList) {
                Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
                // 默认只有一个sheet
                Sheet sheet = workbook.getSheetAt(0);
                // 获得sheet有多少行
                int rows = sheet.getPhysicalNumberOfRows();
                // 跳过第0行，此行是标题行
                for (int i = 1; i < rows; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        sysRole = new SysRole();
                        sysRoleList.add(sysRole);
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                field = SysRole.class.getDeclaredField(fieldList.get(j));
                                field.setAccessible(true);
                                // 设置单元格格式
                                cell.setCellType(CellType.STRING);
                                field.set(sysRole, cell.getStringCellValue());
                                field.setAccessible(false);
                            }
                        }
                    }
                }
                workbook.close();
            }
        }
        sysRoleService.saveBatch(sysRoleList);
        result.setMessage("导入成功");
        return result;
    }

    @Override
    public Workbook exportHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.createTitleLine(workbook, new ArrayList<>(sysRoleExcelMap.keySet()));
        HSSFSheet sheet0 = workbook.getSheetAt(0);
        List<SysRole> sysRoleList = sysRoleService.list();
        HSSFRow curRow;
        HSSFCell curCell;
        Field field;
        int i = 0, j;
        for (SysRole sysRole : sysRoleList) {
            // 每一条数据创建一行
            curRow = sheet0.createRow(i + 1);
            j = 0;
            for (String fieldName : sysRoleExcelMap.values()) {
                curCell = curRow.createCell(j);
                field = SysRole.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                curCell.setCellValue("" + field.get(sysRole));
                field.setAccessible(false);
                j++;
            }
            i++;
        }
        return workbook;
    }

    @Override
    public Workbook templateHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.createTitleLine(workbook, new ArrayList<>(sysRoleExcelMap.keySet()));
        return workbook;
    }

    /**
     * <pre>
     * 创建标题行
     * </pre>
     *
     * @param workbook
     * @param titleList
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void createTitleLine(HSSFWorkbook workbook, List<String> titleList) {
        // 创建sheet0
        HSSFSheet sheet0 = workbook.createSheet("sheet0");
        // 创建第0行=>标题行
        HSSFRow row0 = sheet0.createRow(0);
        HSSFCell curCell;
        for (int i = 0; i < titleList.size(); i++) {
            curCell = row0.createCell(i);
            curCell.setCellValue(titleList.get(i));
        }
    }

}
