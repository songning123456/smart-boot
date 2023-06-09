package com.sonin.core.mpp;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.sonin.core.callback.IDictCallback;
import com.sonin.core.constant.BaseConstant;
import com.sonin.utils.DateUtils;
import com.sonin.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 9:07
 */
public class Dict implements IBase {

    private String defaultDictSuffix = "_dictText";

    private String dictSuffix = defaultDictSuffix;

    private Map<String, String> dictMap = new LinkedHashMap<>();

    public Dict dictSuffix(String dictSuffix) {
        this.dictSuffix = dictSuffix;
        // 修改已经设置默认后缀的dict
        for (Map.Entry<String, String> entry: dictMap.entrySet()) {
            if (entry.getValue().endsWith(defaultDictSuffix)) {
                dictMap.put(entry.getKey(), entry.getKey() + this.dictSuffix);
            }
        }
        return this;
    }

    public Dict dicts(String... srcFieldNames) {
        for (String srcFieldName : srcFieldNames) {
            dictMap.put(srcFieldName, srcFieldName + dictSuffix);
        }
        return this;
    }

    public Dict dict(String srcFieldName) {
        dictMap.put(srcFieldName, srcFieldName + dictSuffix);
        return this;
    }

    public Dict dict(String srcFieldName, String targetFieldName) {
        dictMap.put(srcFieldName, targetFieldName);
        return this;
    }

    public Dict dict(boolean prefixCondition, Field srcField, Field targetField) {
        String srcFieldName = srcField.getName();
        String targetFieldName = targetField.getName();
        if (prefixCondition) {
            String srcClassName = srcField.getDeclaringClass().getSimpleName();
            String targetClassName = targetField.getDeclaringClass().getSimpleName();
            dictMap.put(srcClassName + UNDERLINE + srcFieldName, targetClassName + UNDERLINE + targetFieldName);
        } else {
            dictMap.put(srcFieldName, targetFieldName);
        }
        return this;
    }

    public <T> Dict dict(boolean prefixCondition, SFunction<T, ?> srcFunc, SFunction<T, ?> targetFunc) {
        dict(prefixCondition, ReflectUtils.lambdaField(srcFunc), ReflectUtils.lambdaField(targetFunc));
        return this;
    }

    public Map<String, Object> dictMap(Map<String, Object> srcMap, IDictCallback iDictCallback) {
        String srcFieldName, targetFieldName;
        Object srcFieldValue, targetFieldValue;
        Map<String, Object> targetMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : srcMap.entrySet()) {
            srcFieldName = entry.getKey();
            srcFieldValue = entry.getValue();
            targetMap.put(srcFieldName, srcFieldValue);
            // 默认解析Date格式
            if (srcFieldValue instanceof Date) {
                targetFieldValue = DateUtils.date2Str((Date) srcFieldValue, BaseConstant.dateFormat);
                targetMap.put(srcFieldName, targetFieldValue);
            }
            targetFieldName = dictMap.get(srcFieldName);
            if (!"".equals(targetFieldName) && targetFieldName != null) {
                targetFieldValue = iDictCallback.dict(srcFieldName, srcFieldValue);
                targetMap.put(targetFieldName, targetFieldValue);
            }
        }
        return targetMap;
    }

    public List<Map<String, Object>> dictMaps(List<Map<String, Object>> srcMapList, IDictCallback iDictCallback) {
        List<Map<String, Object>> targetMapList = new ArrayList<>();
        for (Map<String, Object> srcMap : srcMapList) {
            targetMapList.add(dictMap(srcMap, iDictCallback));
        }
        return targetMapList;
    }

}
