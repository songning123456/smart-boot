package com.sonin.modules.file.service;

import com.sonin.api.vo.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 10:17
 */
public interface ExcelService {

    Result handle(HttpServletRequest request) throws Exception;

}
