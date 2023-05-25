package com.sonin.core.callback;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/25 13:59
 */
@FunctionalInterface
public interface IPageQueryCallback {

    void execute(List<Map<String, Object>> queryMapList);

}
