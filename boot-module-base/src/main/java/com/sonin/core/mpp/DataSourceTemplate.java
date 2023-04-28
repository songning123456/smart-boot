package com.sonin.core.mpp;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.sonin.core.callback.IDataSourceCallback;

/**
 * <pre>
 * 数据源模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 15:02
 */
public class DataSourceTemplate {

    public static <T> T execute(String DBName, IDataSourceCallback<T> iDataSourceCallback) {
        DynamicDataSourceContextHolder.push(DBName);
        T result = iDataSourceCallback.execute();
        DynamicDataSourceContextHolder.clear();
        return result;
    }

}
