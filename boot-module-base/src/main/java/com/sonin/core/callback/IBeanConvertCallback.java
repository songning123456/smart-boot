package com.sonin.core.callback;

import org.springframework.lang.Nullable;

/**
 * @author sonin
 * @date 2021/10/3 13:11
 * 自定义实现Bean的转换
 */
@FunctionalInterface
public interface IBeanConvertCallback {

    @Nullable
    Object doBeanConvert(String targetFieldName, Object srcFieldVal);

}
