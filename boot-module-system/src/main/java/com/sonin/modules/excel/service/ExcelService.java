package com.sonin.modules.excel.service;

import com.sonin.api.vo.Result;
import com.sonin.modules.excel.callback.ICellCallback;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <pre>
 * Excel 导入/导出/下载模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/27 10:17
 */
public abstract class ExcelService {

    /**
     * <pre>
     * 导入操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public abstract Result importHandle(HttpServletRequest request) throws Exception;

    /**
     * <pre>
     * 导出操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public abstract Workbook exportHandle(HttpServletRequest request) throws Exception;

    /**
    * <pre>
    * 获取子类的excelMap
    * </pre>
     * @param
    * @author sonin
    * @Description: TODO(这里描述这个方法的需求变更情况)
    */
    protected abstract Map<String, String> getExcelMap();

    /**
     * <pre>
     * 导出模板操作
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public Workbook templateHandle(HttpServletRequest request) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.initTitle(workbook, new ArrayList<>(getExcelMap().keySet()));
        return workbook;
    }

    /**
     * <pre>
     * 创建时，初始化标题行
     * </pre>
     *
     * @param workbook
     * @param titleList
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    protected void initTitle(HSSFWorkbook workbook, List<String> titleList) {
        // 创建sheet0
        HSSFSheet sheet0 = workbook.createSheet("sheet0");
        // 设置 居中样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 创建 第0行 => 标题行
        HSSFRow row0 = sheet0.createRow(0);
        HSSFCell curCell;
        for (int i = 0; i < titleList.size(); i++) {
            curCell = row0.createCell(i);
            curCell.setCellValue(titleList.get(i));
            curCell.setCellStyle(cellStyle);
        }
    }

    /**
     * <pre>
     * 导出时填充POI
     * </pre>
     *
     * @param workbook
     * @param fieldNameList
     * @param dataList
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    protected <T> void fillExcel(HSSFWorkbook workbook, Collection<String> fieldNameList, List<T> dataList, ICellCallback iCellCallback) throws Exception {
        HSSFSheet sheet0 = workbook.getSheetAt(0);
        HSSFRow curRow;
        HSSFCell curCell;
        Field field;
        int i = 0, j;
        Object srcFieldVal, targetFieldVal;
        for (T data : dataList) {
            // 每一条数据创建一行
            curRow = sheet0.createRow(i + 1);
            j = 0;
            for (String fieldName : fieldNameList) {
                curCell = curRow.createCell(j);
                if (data instanceof Map) {
                    srcFieldVal = ((Map) data).get(fieldName);
                } else {
                    field = data.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    srcFieldVal = field.get(data);
                    field.setAccessible(false);
                }
                if (iCellCallback != null) {
                    targetFieldVal = iCellCallback.doCellConvert(fieldName, srcFieldVal);
                } else {
                    targetFieldVal = srcFieldVal;
                }
                if (targetFieldVal != null) {
                    curCell.setCellValue("" + targetFieldVal);
                }
                j++;
            }
            i++;
        }
    }

    /**
     * <pre>
     * 解析导入的文件
     * </pre>
     *
     * @param request
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    protected List<Map<String, Object>> analysisExcel(HttpServletRequest request, List<String> fieldNameList) throws Exception {
        MultiValueMap<String, MultipartFile> multiValueMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data;
        List<MultipartFile> fileList;
        Workbook workbook;
        Sheet sheet0;
        Row curRow;
        Cell curCell;
        for (Map.Entry<String, List<MultipartFile>> item : multiValueMap.entrySet()) {
            // String name = item.getKey(); // 请求的文本参数中key值
            fileList = item.getValue();
            // 请求的文本参数列表
            for (MultipartFile multipartFile : fileList) {
                workbook = WorkbookFactory.create(multipartFile.getInputStream());
                // 默认只有一个sheet
                sheet0 = workbook.getSheetAt(0);
                // 遍历sheet有多少行；并跳过第0行，此行是标题行
                for (int i = 1; i < sheet0.getPhysicalNumberOfRows(); i++) {
                    curRow = sheet0.getRow(i);
                    if (curRow == null) {
                        continue;
                    }
                    data = new HashMap<>();
                    dataList.add(data);
                    for (int j = 0; j < curRow.getLastCellNum(); j++) {
                        curCell = curRow.getCell(j);
                        if (curCell == null) {
                            continue;
                        }
                        switch (curCell.getCellType()) {
                            case Cell.CELL_TYPE_NUMERIC:
                                // 如果是日期类型
                                if (HSSFDateUtil.isCellDateFormatted(curCell)) {
                                    data.put(fieldNameList.get(j), curCell.getDateCellValue());
                                } else {
                                    data.put(fieldNameList.get(j), curCell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                data.put(fieldNameList.get(j), curCell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                data.put(fieldNameList.get(j), "");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                data.put(fieldNameList.get(j), curCell.getBooleanCellValue());
                                break;
                            default:
                                curCell.setCellType(Cell.CELL_TYPE_STRING);
                                data.put(fieldNameList.get(j), curCell.getStringCellValue());
                                break;
                        }
                    }
                }
                workbook.cloneSheet(0);
            }
        }
        return dataList;
    }

}
