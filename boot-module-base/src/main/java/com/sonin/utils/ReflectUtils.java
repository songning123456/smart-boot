package com.sonin.utils;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static  <T> Field lambdaField(SFunction<T, ?> func) {
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

}
