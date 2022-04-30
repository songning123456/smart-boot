package com.sonin.modules.sys.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.modules.file.service.ExcelService;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.service.SysRoleService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/30 10:16
 */
@Service("sysRole")
public class SysRoleExcelServiceImpl implements ExcelService {

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public Result importHandle(HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public Workbook exportHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Map<String, String> zhName2EnNameMap = new LinkedHashMap<String, String>() {{
            put("名称", "name");
            put("编码", "code");
            put("备注", "remark");
        }};
        this.createTitleLine(workbook, new ArrayList<>(zhName2EnNameMap.keySet()));
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
            for (String fieldName : zhName2EnNameMap.values()) {
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
        List<String> zhNameList = Arrays.asList("名称", "编码", "备注");
        this.createTitleLine(workbook, zhNameList);
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
