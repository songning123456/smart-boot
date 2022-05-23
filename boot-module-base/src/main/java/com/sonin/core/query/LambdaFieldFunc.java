package com.sonin.core.query;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/23 17:51
 */
@FunctionalInterface
public interface LambdaFieldFunc<T, R> extends Function<T, R>, Serializable {

    public static <T> String fieldName(LambdaFieldFunc<T, ?> lambdaFieldFunc) {
        try {
            // 通过获取对象方法，判断是否存在该方法
            Method method = lambdaFieldFunc.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            // 利用jdk的SerializedLambda解析方法引用
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambdaFieldFunc);
            String getMethodName = serializedLambda.getImplMethodName();
            if (getMethodName.startsWith("get")) {
                getMethodName = getMethodName.substring(3);
            } else if (getMethodName.startsWith("is")) {
                getMethodName = getMethodName.substring(2);
            }
            if (StringUtils.isEmpty(getMethodName)) {
                return "";
            }
            return getMethodName.substring(0, 1).toLowerCase() + getMethodName.substring(1);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
