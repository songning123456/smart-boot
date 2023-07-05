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
        T result = null;
        DynamicDataSourceContextHolder.push(DBName);
        try {
            result = iDataSourceCallback.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
        return result;
    }

}
