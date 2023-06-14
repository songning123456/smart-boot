package com.sonin.core.javassist;

/**
 * <pre>
 * 生成类类型
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/14 13:57
 */
public enum ClassEnum {

    // 根据field生成类
    FIELD,

    // 根据相似类(similarClass)生成类
    SIMILAR_CLASS

}
