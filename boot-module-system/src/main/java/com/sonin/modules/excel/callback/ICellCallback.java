package com.sonin.modules.excel.callback;

import org.springframework.lang.Nullable;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/14 9:07
 */
@FunctionalInterface
public interface ICellCallback {

    @Nullable
    Object doCellConvert(String srcFieldName, Object srcFieldVal);

}
