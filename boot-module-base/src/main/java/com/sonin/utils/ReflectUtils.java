package com.sonin.utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.google.common.base.CaseFormat;
import com.sonin.core.mpp.IBase;
import org.apache.ibatis.reflection.property.PropertyNamer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/11/14 15:19
 */
public class ReflectUtils {

    /**
     * 解决HttpServletRequest中字段多余T时，转换结果异常问题(e.g：_t=xxx)
     * e.g: EquipmentRepairVO equipmentRepairVO = BeanUtil.mapToBean(request.getParameterMap(), EquipmentRepairVO.class, true);
     *
     * @param httpServletRequest
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T request2BeanFunc(HttpServletRequest httpServletRequest, Class<T> clazz) {
        if (httpServletRequest == null || clazz == null) {
            throw new IllegalArgumentException("Request and class cannot be null");
        }
        // 获取所有请求参数
        Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
        // 创建新的Map用于存储过滤后的参数
        Map<String, String> filteredMap = new HashMap<>(paramMap.size());
        // 获取T及其父类的所有字段名
        Set<String> fieldNameSet = new HashSet<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fieldArr = currentClass.getDeclaredFields();
            for (Field field : fieldArr) {
                fieldNameSet.add(field.getName());
            }
            currentClass = currentClass.getSuperclass();
        }
        // 只保留T中存在的字段
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if (fieldNameSet.contains(entry.getKey())) {
                filteredMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        try {
            // 使用过滤后的Map进行转换
            return BeanUtil.mapToBean(filteredMap, clazz, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert request to bean: " + clazz.getName(), e);
        }
    }

    /**
     * <pre>
     * 获取某一个类的所有字段名称
     * </pre>
     *
     * @param clazz
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Map<String, Class> getAllField(Class clazz) {
        Map<String, Class> fieldMap = new LinkedHashMap<>();
        try {
            Field[] fields;
            while (!"java.lang.Object".equals(clazz.getName())) {
                fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    fieldMap.put(field.getName(), field.getType());
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldMap;
    }

    /**
     * <pre>
     * 设置对象
     * </pre>
     *
     * @param entity
     * @param paramMap
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static void setObj(Object entity, Map<String, String> paramMap) throws Exception {
        String fieldName, fieldValue, typeName;
        for (Map.Entry<String, String> item : paramMap.entrySet()) {
            fieldName = item.getKey();
            fieldValue = item.getValue();
            Class clazz = entity.getClass();
            while (!"java.lang.Object".equals(clazz.getName())) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        field.setAccessible(true);
                        // 判断类型
                        typeName = field.getGenericType().getTypeName();
                        switch (typeName) {
                            case "java.lang.String":
                                field.set(entity, fieldValue);
                                break;
                            case "java.lang.Integer":
                                field.set(entity, new Integer(fieldValue));
                                break;
                            case "java.lang.Double":
                                field.set(entity, new Double(fieldValue));
                                break;
                            case "java.math.BigDecimal":
                                field.set(entity, new BigDecimal(fieldValue));
                                break;
                        }
                        field.setAccessible(false);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    public static <T> Field lambdaField(SFunction<T, ?> func) {
        SerializedLambda serializedLambda = LambdaUtils.resolve(func);
        Field targetField;
        try {
            targetField = ClassUtils.toClassConfident(serializedLambda.getImplClass().getName()).getDeclaredField(PropertyNamer.methodToProperty(serializedLambda.getImplMethodName()));
        } catch (Exception e) {
            e.printStackTrace();
            targetField = null;
        }
        return targetField;
    }

    public static String select(Class... clazzArr) {
        List<String> aliasList = new ArrayList<>();
        String className, tableName, classFieldName, tableFieldName, alias;
        Field[] fields;
        for (Class clazz : clazzArr) {
            className = clazz.getSimpleName();
            if (clazz.isAnnotationPresent(TableName.class)) {
                TableName tableNameAnno = (TableName) clazz.getAnnotation(TableName.class);
                tableName = tableNameAnno.value();
            } else {
                tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
            }
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 过滤掉 @TableField(exist = false) 情况
                TableField tableFieldAnno = field.getAnnotation(TableField.class);
                if (tableFieldAnno != null && !tableFieldAnno.exist()) {
                    continue;
                }
                classFieldName = field.getName();
                tableFieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, classFieldName);
                if (clazzArr.length == 1) {
                    alias = tableName + IBase.DOT + tableFieldName + IBase.SPACE + IBase.AS + classFieldName;
                } else {
                    alias = tableName + IBase.DOT + tableFieldName + IBase.SPACE + IBase.AS + IBase.SPACE + className + IBase.UNDERLINE + classFieldName;
                }
                aliasList.add(alias);
            }
        }
        return String.join(IBase.COMMA, aliasList);
    }

}
