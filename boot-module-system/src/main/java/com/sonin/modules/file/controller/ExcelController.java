package com.sonin.modules.file.controller;

import cn.hutool.json.JSONUtil;
import com.sonin.api.vo.Result;
import com.sonin.core.context.SpringContext;
import com.sonin.modules.file.service.ExcelService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 * Excel导入/导出/模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 9:59
 */
@RestController
@RequestMapping("/file/excel")
public class ExcelController {

    @PostMapping("/import")
    public void importCtrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        Result result = null;
        try {
            if (StringUtils.isEmpty(request.getParameter("component"))) {
                result = Result.error("请输入组件component参数");
            } else {
                ExcelService excelService = (ExcelService) SpringContext.getBean(request.getParameter("component"));
                result = excelService.importHandle(request);
            }
        } catch (Exception e) {
            result = Result.error(e.getMessage());
        } finally {
            outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        }
    }

    @GetMapping("/export")
    public void exportCtrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        Result result;
        try {
            if (StringUtils.isEmpty(request.getParameter("component"))) {
                result = Result.error("请输入组件component参数");
                outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
            } else {
                ExcelService excelService = (ExcelService) SpringContext.getBean(request.getParameter("component"));
                Workbook workbook = excelService.exportHandle(request);
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            result = Result.error(e.getMessage());
            outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    @GetMapping("/template")
    public void templateCtrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        Result result;
        try {
            if (StringUtils.isEmpty(request.getParameter("component"))) {
                result = Result.error("请输入组件component参数");
                outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
            } else {
                ExcelService excelService = (ExcelService) SpringContext.getBean(request.getParameter("component"));
                Workbook workbook = excelService.templateHandle(request);
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            result = Result.error(e.getMessage());
            outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

}
