package com.sonin.core.callback;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 10:19
 */
@FunctionalInterface
public interface IDictCallback {

    Object dict(String srcFieldName, Object srcFieldValue);

}
