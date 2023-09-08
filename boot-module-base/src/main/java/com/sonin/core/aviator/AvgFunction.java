package com.sonin.core.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDecimal;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 平均值计算服务
 *
 * @author sonin
 */
public class AvgFunction extends AbstractFunction {
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        AviatorJavaType aviatorJavaType = (AviatorJavaType) arg1;
        String key = aviatorJavaType.getName();
        Object sumResult = AviatorEvaluator.execute("reduce(" + key + ",+,0)", env);
        if (sumResult instanceof Double) {
            double sumDoubleResult = (Double) sumResult;
            Long count = (Long) AviatorEvaluator.execute("count(" + key + ")", env);
            Double avgResult = sumDoubleResult / count;
            return new AviatorDecimal(Double.parseDouble(String.format("%.2f", avgResult)));
        } else {
            Long sumLongResult = (Long) sumResult;
            double count = ((Long) AviatorEvaluator.execute("count(" + key + ")", env)).doubleValue();
            double avgResult = (sumLongResult / count);
            return new AviatorDecimal(avgResult);
        }
    }

    @Override
    public String getName() {
        return "AVG";
    }

}
