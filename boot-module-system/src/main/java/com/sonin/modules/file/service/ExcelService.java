package com.sonin.modules.file.service;

import com.sonin.api.vo.Result;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 * Excel 导入/导出/下载模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 10:17
 */
public interface ExcelService {

    /**
     * <pre>
     * 导入操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    Result importHandle(HttpServletRequest request) throws Exception;

    /**
     * <pre>
     * 导出操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    Workbook exportHandle(HttpServletRequest request) throws Exception;

    /**
     * <pre>
     * 导出模板操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    Workbook templateHandle(HttpServletRequest request) throws Exception;

}
