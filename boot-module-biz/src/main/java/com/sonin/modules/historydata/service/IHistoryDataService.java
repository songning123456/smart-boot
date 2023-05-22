package com.sonin.modules.historydata.service;

import java.util.List;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/22 11:26
 */
public interface IHistoryDataService {

    Double queryDiffForDay(String code, String yearMonthDay);

}
