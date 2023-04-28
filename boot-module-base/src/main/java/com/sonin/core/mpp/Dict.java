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

    public Dict dicts(String... srcFields) {
        for (String srcField : srcFields) {
            dictMap.put(srcField, srcField + dictSuffix);
        }
        return this;
    }

    public Dict dict(String srcField, String targetField) {
        dictMap.put(srcField, targetField);
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
        String srcField, targetField;
        Object srcValue, targetValue;
        Map<String, Object> targetMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : srcMap.entrySet()) {
            srcField = entry.getKey();
            srcValue = entry.getValue();
            targetMap.put(srcField, srcValue);
            // 默认解析Date格式
            if (srcValue instanceof Date) {
                targetValue = DateUtils.date2Str((Date) srcValue, BaseConstant.dateFormat);
                targetMap.put(srcField, targetValue);
            }
            targetField = dictMap.get(srcField);
            if (!"".equals(targetField) && targetField != null) {
                targetValue = iDictCallback.dict(srcField, srcValue);
                targetMap.put(targetField, targetValue);
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
