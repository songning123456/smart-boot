package com.sonin.core.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 求和计算服务
 *
 * @author sonin
 */
public class SumFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            AviatorJavaType aviatorJavaType = (AviatorJavaType) arg1;
            String key = aviatorJavaType.getName();
            Object sumResult = AviatorEvaluator.execute("reduce(" + key + ",+,0)", env);
            if (sumResult instanceof Double) {
                //double型
                return new AviatorDouble((Double) sumResult);
            } else {
                //long型
                return new AviatorDouble((Long) sumResult);
            }
        } catch (Exception e) {
            return new AviatorDouble(0.00);
        }

    }

    @Override
    public String getName() {
        return "SUM";
    }
}
