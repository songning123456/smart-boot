package com.sonin.core.callback;

import org.springframework.lang.Nullable;

/**
 * <pre>
 * 数据源选择
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 15:05
 */
@FunctionalInterface
public interface IDataSourceCallback<T> {

    @Nullable
    T execute();

}
