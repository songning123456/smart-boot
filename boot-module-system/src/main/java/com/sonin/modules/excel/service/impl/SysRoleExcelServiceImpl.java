package com.sonin.modules.excel.service.impl;

import com.sonin.api.vo.Result;
import com.sonin.modules.excel.service.ExcelService;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.utils.BeanExtUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
public class SysRoleExcelServiceImpl extends ExcelService {

    @Autowired
    private SysRoleService sysRoleService;

    private Map<String, String> excelMap = new LinkedHashMap<String, String>() {{
        put("名称", "name");
        put("编码", "code");
        put("备注", "remark");
    }};

    @Override
    public Result importHandle(HttpServletRequest request) throws Exception {
        Result result = new Result();
        List<Map<String, Object>> dataList = this.analysisExcel(request, new ArrayList<>(excelMap.values()));
        List<SysRole> sysRoleList = BeanExtUtils.maps2Beans(dataList, SysRole.class);
        sysRoleService.saveBatch(sysRoleList);
        result.setMessage("导入成功");
        return result;
    }

    @Override
    public Workbook exportHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.initTitle(workbook, new ArrayList<>(excelMap.keySet()));
        List<SysRole> dataList = sysRoleService.list();
        this.fillExcel(workbook, excelMap.values(), dataList, null);
        return workbook;
    }

    @Override
    public Map<String, String> getExcelMap() {
        return this.excelMap;
    }

}
