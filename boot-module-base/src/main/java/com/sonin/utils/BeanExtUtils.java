package com.sonin.utils;

import com.sonin.core.annotation.BeanAnno;
import com.sonin.core.callback.IBeanConvertCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author sonin
 * @date 2021/10/2 13:31
 * bean类型转换
 */
public class BeanExtUtils {

    /**
     * Bean => Bean
     *
     * @param src
     * @param targetClass
     * @throws Exception
     */
    public static <S, T> T bean2Bean(S src, Class<T> targetClass) throws Exception {
        // 判空
        if (src == null || targetClass == null) {
            return null;
        }
        // 获取Method[]
        Method[] srcMethods = src.getClass().getMethods();
        Method[] targetMethods = targetClass.getMethods();
        // 创建目标对象
        T target = targetClass.newInstance();
        // 创建 fieldName => Method 缓存
        Map<String, Method> srcMap = new HashMap<>(srcMethods.length >> 1);
        // 缓存src
        for (Method srcMethod : srcMethods) {
            String srcMethodName = srcMethod.getName();
            if (srcMethodName.startsWith("get") && !"getClass".equals(srcMethodName)) {
                srcMap.put(srcMethodName.replaceFirst("get", ""), srcMethod);
            }
        }
        // 根据缓存的src创建target
        for (Method targetMethod : targetMethods) {
            String targetMethodName = targetMethod.getName();
            if (targetMethodName.startsWith("set")) {
                Method srcMethod = srcMap.get(targetMethodName.replaceFirst("set", ""));
                if (srcMethod == null) {
                    continue;
                }
                Object fieldVal = srcMethod.invoke(src);
                targetMethod.invoke(target, fieldVal);
            }
        }
        // 清理缓存
        srcMap.clear();
        // 返回目标对象
        return target;
    }

    /**
     * List<S> => List<T>
     *
     * @param srcList
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <S, T> List<T> beans2Beans(List<S> srcList, Class<T> targetClass) throws Exception {
        // 判空
        if (srcList == null || targetClass == null) {
            return null;
        }
        // 判空
        if (srcList.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取Method[]
        Method[] srcMethods = srcList.get(0).getClass().getMethods();
        Method[] targetMethods = targetClass.getMethods();
        // 创建目标对象
        List<T> targetList = new ArrayList<>();
        // 创建 fieldName => Method 缓存
        Map<String, Method> srcMap = new HashMap<>(srcMethods.length >> 1);
        Map<String, Method> targetMap = new HashMap<>(targetMethods.length >> 1);
        // 遍历
        for (S src : srcList) {
            T target = targetClass.newInstance();
            if (srcMap.isEmpty() && targetMap.isEmpty()) {
                for (Method srcMethod : srcMethods) {
                    String srcMethodName = srcMethod.getName();
                    if (srcMethodName.startsWith("get") && !"getClass".equals(srcMethodName)) {
                        srcMap.put(srcMethodName.replaceFirst("get", ""), srcMethod);
                    }
                }
                for (Method targetMethod : targetMethods) {
                    String targetMethodName = targetMethod.getName();
                    if (targetMethodName.startsWith("set")) {
                        Method srcMethod = srcMap.get(targetMethodName.replaceFirst("set", ""));
                        if (srcMethod == null) {
                            continue;
                        }
                        Object fieldVal = srcMethod.invoke(src);
                        targetMethod.invoke(target, fieldVal);
                        targetMap.put(targetMethodName.replaceFirst("set", ""), targetMethod);
                    }
                }
            } else {
                for (Map.Entry<String, Method> item : targetMap.entrySet()) {
                    String fieldName = item.getKey();
                    Method srcMethod = srcMap.get(fieldName);
                    if (srcMethod == null) {
                        continue;
                    }
                    Object fieldVal = srcMethod.invoke(src);
                    Method targetMethod = targetMap.get(fieldName);
                    targetMethod.invoke(target, fieldVal);
                }
            }
            targetList.add(target);
        }
        // 清理缓存
        srcMap.clear();
        targetMap.clear();
        // 返回目标对象
        return targetList;
    }

    /**
     * bean => bean 自定义实现值
     *
     * @param src
     * @param targetClass
     * @param iBeanConvertCallback
     * @param <S>
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <S, T> T bean2Bean(S src, Class<T> targetClass, IBeanConvertCallback iBeanConvertCallback) throws Exception {
        // 判空
        if (src == null || targetClass == null || iBeanConvertCallback == null) {
            return null;
        }
        // 创建目标对象
        T target = targetClass.newInstance();
        // 缓存
        Map<String, Object> srcMap = new HashMap<>();
        Map<String, Object> annoMap = new HashMap<>();
        // 遍历src对象属性
        Class tmpSrcClass = src.getClass();
        while (!"java.lang.Object".equals(tmpSrcClass.getName())) {
            Field[] srcFields = tmpSrcClass.getDeclaredFields();
            for (Field srcField : srcFields) {
                srcField.setAccessible(true);
                srcMap.put(srcField.getName(), srcField.get(src));
                beanAnnoFunc(src, srcField, annoMap);
            }
            tmpSrcClass = tmpSrcClass.getSuperclass();
        }
        // 遍历target对象属性
        Class tmpTargetClass = targetClass;
        while (!"java.lang.Object".equals(tmpTargetClass.getName())) {
            Field[] targetFields = tmpTargetClass.getDeclaredFields();
            for (Field targetField : targetFields) {
                targetField.setAccessible(true);
                setTargetFunc(targetField, target, srcMap, annoMap, iBeanConvertCallback);
            }
            tmpTargetClass = tmpTargetClass.getSuperclass();
        }
        // 清理缓存
        srcMap.clear();
        annoMap.clear();
        // 返回目标对象
        return target;
    }

    /**
     * beans => beans 自定义实现值
     *
     * @param srcList
     * @param targetClass
     * @param iBeanConvertCallback
     * @param <S>
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <S, T> List<T> beans2Beans(List<S> srcList, Class<T> targetClass, IBeanConvertCallback iBeanConvertCallback) throws Exception {
        // 判空
        if (srcList == null || targetClass == null) {
            return null;
        }
        // 判空
        if (srcList.isEmpty()) {
            return new ArrayList<>();
        }
        // 创建目标对象集合
        List<T> targetList = new ArrayList<>();
        // src、target的Field缓存
        List<Field> srcFieldList = new ArrayList<>();
        List<Field> targetFieldList = new ArrayList<>();
        for (S src : srcList) {
            // 创建目标对象
            T target = targetClass.newInstance();
            // 缓存
            Map<String, Object> srcMap = new HashMap<>();
            Map<String, Object> annoMap = new HashMap<>();
            if (srcFieldList.isEmpty() && targetFieldList.isEmpty()) {
                // 遍历src对象属性
                Class tmpSrcClass = src.getClass();
                while (!"java.lang.Object".equals(tmpSrcClass.getName())) {
                    Field[] srcFields = tmpSrcClass.getDeclaredFields();
                    for (Field srcField : srcFields) {
                        srcField.setAccessible(true);
                        srcFieldList.add(srcField);
                        srcMap.put(srcField.getName(), srcField.get(src));
                        beanAnnoFunc(src, srcField, annoMap);
                    }
                    tmpSrcClass = tmpSrcClass.getSuperclass();
                }
                // 遍历target对象属性
                Class tmpTargetClass = targetClass;
                while (!"java.lang.Object".equals(tmpTargetClass.getName())) {
                    Field[] targetFields = tmpTargetClass.getDeclaredFields();
                    for (Field targetField : targetFields) {
                        targetField.setAccessible(true);
                        targetFieldList.add(targetField);
                        setTargetFunc(targetField, target, srcMap, annoMap, iBeanConvertCallback);
                    }
                    tmpTargetClass = tmpTargetClass.getSuperclass();
                }
            } else {
                for (Field srcField : srcFieldList) {
                    srcField.setAccessible(true);
                    srcMap.put(srcField.getName(), srcField.get(src));
                    beanAnnoFunc(src, srcField, annoMap);
                }
                for (Field targetField : targetFieldList) {
                    targetField.setAccessible(true);
                    setTargetFunc(targetField, target, srcMap, annoMap, iBeanConvertCallback);
                }
            }
            // 清理缓存
            srcMap.clear();
            annoMap.clear();
            // 集合添加目标对象
            targetList.add(target);
        }
        // 清理缓存
        srcFieldList.clear();
        targetFieldList.clear();
        // 返回目标对象
        return targetList;
    }

    /**
     * 根据注解缓存annoMap
     *
     * @param src
     * @param srcField
     * @param annoMap
     * @param <S>
     * @throws Exception
     */
    private static <S> void beanAnnoFunc(S src, Field srcField, Map<String, Object> annoMap) throws Exception {
        BeanAnno beanAnno = srcField.getAnnotation(BeanAnno.class);
        if (beanAnno != null) {
            if (!"".equals(beanAnno.targetFieldName())) {
                annoMap.put(beanAnno.targetFieldName(), srcField.get(src));
            }
            if (beanAnno.targetFieldNames().length > 0) {
                for (String targetFieldName : beanAnno.targetFieldNames()) {
                    if (!"".equals(targetFieldName)) {
                        annoMap.put(targetFieldName, srcField.get(src));
                    }
                }
            }
        }
        srcField.setAccessible(false);
    }

    /**
     * 设置target的field值
     *
     * @param targetField
     * @param target
     * @param srcMap
     * @param annoMap
     * @param iBeanConvertCallback
     * @param <T>
     * @throws Exception
     */
    private static <T> void setTargetFunc(Field targetField, T target, Map<String, Object> srcMap, Map<String, Object> annoMap, IBeanConvertCallback iBeanConvertCallback) throws Exception {
        if (annoMap.get(targetField.getName()) != null) {
            Object targetFieldVal = iBeanConvertCallback.doBeanConvert(targetField.getName(), annoMap.get(targetField.getName()));
            targetField.set(target, targetFieldVal);
        } else {
            targetField.set(target, srcMap.get(targetField.getName()));
        }
        targetField.setAccessible(false);
    }

    /**
     * Map => Bean
     *
     * @param map
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map, Class<T> targetClass) throws Exception {
        // 判空
        if (map == null || targetClass == null) {
            return null;
        }
        // 判空
        if (map.isEmpty()) {
            return targetClass.newInstance();
        }
        // 构建对象
        T target = targetClass.newInstance();
        // 获取所有方法
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                // 截取属性名
                String fieldName = method.getName();
                fieldName = fieldName.toLowerCase().charAt(3) + fieldName.substring(4);
                if (map.containsKey(fieldName)) {
                    method.invoke(target, map.get(fieldName));
                }
            }
        }
        return target;
    }

    /**
     * Maps => Beans
     *
     * @param mapList
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> List<T> maps2Beans(List<Map<String, Object>> mapList, Class<T> targetClass) throws Exception {
        // 判空
        if (mapList == null || targetClass == null) {
            return null;
        }
        // 判空
        if (mapList.isEmpty()) {
            return new ArrayList<>();
        }
        // 构建对象集合
        List<T> targetList = new ArrayList<>();
        // fieldName:method 缓存
        Map<String, Method> methodMap = new HashMap<>();
        // 遍历存储
        for (Map<String, Object> map : mapList) {
            T target = targetClass.newInstance();
            if (methodMap.isEmpty()) {
                Method[] methods = targetClass.getMethods();
                for (Method method : methods) {
                    if (method.getName().startsWith("set")) {
                        // 截取属性名
                        String fieldName = method.getName();
                        fieldName = fieldName.toLowerCase().charAt(3) + fieldName.substring(4);
                        if (map.containsKey(fieldName)) {
                            method.invoke(target, map.get(fieldName));
                            methodMap.put(fieldName, method);
                        }
                    }
                }
            } else {
                for (Map.Entry<String, Method> item : methodMap.entrySet()) {
                    String fieldName = item.getKey();
                    Method method = item.getValue();
                    method.invoke(target, map.get(fieldName));
                }
            }
            targetList.add(target);
        }
        // 清理缓存
        methodMap.clear();
        return targetList;
    }

    /**
     * Bean => Map
     *
     * @param src
     * @param <S>
     * @return
     */
    public static <S> Map<String, Object> bean2Map(S src) throws Exception {
        // 判空
        if (src == null) {
            return new HashMap<>();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        Class srcClass = src.getClass();
        while (srcClass != null && !"java.lang.Object".equals(srcClass.getName())) {
            Field[] srcFields = srcClass.getDeclaredFields();
            for (Field srcField : srcFields) {
                srcField.setAccessible(true);
                map.put(srcField.getName(), srcField.get(src));
                srcField.setAccessible(false);
            }
            srcClass = srcClass.getSuperclass();
        }
        return map;
    }

    /**
     * Beans => Maps
     *
     * @param srcList
     * @param <S>
     * @return
     */
    public static <S> List<Map<String, Object>> beans2Maps(List<S> srcList) throws Exception {
        // 判空
        if (srcList == null) {
            return null;
        }
        // 判空
        if (srcList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        // 字段属性缓存
        List<Field> fieldList = new ArrayList<>();
        // 遍历存储
        for (S src : srcList) {
            Map<String, Object> map = new LinkedHashMap<>();
            if (fieldList.isEmpty()) {
                Class srcClass = src.getClass();
                while (srcClass != null && !"java.lang.Object".equals(srcClass.getName())) {
                    Field[] srcFields = srcClass.getDeclaredFields();
                    for (Field srcField : srcFields) {
                        srcField.setAccessible(true);
                        map.put(srcField.getName(), srcField.get(src));
                        fieldList.add(srcField);
                        srcField.setAccessible(false);
                    }
                    srcClass = srcClass.getSuperclass();
                }
            } else {
                for (Field srcField : fieldList) {
                    srcField.setAccessible(true);
                    map.put(srcField.getName(), srcField.get(src));
                    srcField.setAccessible(false);
                }
            }
            mapList.add(map);
        }
        // 清理缓存
        fieldList.clear();
        return mapList;
    }

    /**
     * vo 转换成map; e.g: DemoDTO{DemoA_id:1,DemoA_name:"nameA",DemoB_id:2,DemoB_name:"nameB"} => {DemoA:{id:1,name:"nameA"},DemoB:{id:2,name:"nameB"}}
     *
     * @param src
     * @param <S>
     * @return
     * @throws Exception
     */
    public static <S> Map<String, Map<String, Object>> bean2MultiMap(S src) throws Exception {
        // 判空
        if (src == null) {
            return new HashMap<>();
        }
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        Class srcClass = src.getClass();
        while (srcClass != null && !"java.lang.Object".equals(srcClass.getName())) {
            Field[] srcFields = srcClass.getDeclaredFields();
            for (Field srcField : srcFields) {
                srcField.setAccessible(true);
                String[] fieldNames = splitByLowerUnderscore(srcField.getName());
                map.computeIfAbsent(fieldNames[0], k -> new LinkedHashMap<>());
                map.get(fieldNames[0]).put(fieldNames[1], srcField.get(src));
                srcField.setAccessible(false);
            }
            srcClass = srcClass.getSuperclass();
        }
        return map;
    }

    private static String[] splitByLowerUnderscore(String srcFieldName) {
        String[] targetFieldNames = new String[2];
        StringBuilder stringBuilder = new StringBuilder();
        String[] srcFieldNames = srcFieldName.split("_");
        targetFieldNames[0] = srcFieldNames[0];
        int i = srcFieldNames.length > 1 ? 1 : 0;
        while (i < srcFieldNames.length) {
            stringBuilder.append("_").append(srcFieldNames[i]);
            i++;
        }
        targetFieldNames[1] = stringBuilder.toString().replaceFirst("_", "");
        return targetFieldNames;
    }

}
