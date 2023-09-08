package com.sonin.core.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * 计数计算服务
 *
 * @author sonin
 */
public class CountFunction extends AbstractFunction {
    /**
     * 无条件方法
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        try {
            AviatorJavaType aviatorJavaType = (AviatorJavaType) arg1;
            String key = aviatorJavaType.getName();
            Object sumResult = AviatorEvaluator.execute("count(" + key + ")", env);
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

    /**
     * 有条件数据
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject expObject) {
        try {
            String expValue = FunctionUtils.getStringValue(expObject, env);
            String exp = "";
            String[] expArray = expValue.split(";");
            if (expArray.length > 1) {
                //区间条件
                String[] temp = expArray[0].split(">");
                String[] temp2 = expArray[1].split("<");
                exp = "count(filter(filter(" + temp[0] + ",seq.gt(" + temp[1] + ")),seq.lt(" + temp2[1] + ")))";
            } else {
                //单条件
                if (expArray[0].contains(">")) {
                    String[] temp = expArray[0].split(">");
                    exp = "count(filter(" + temp[0] + ",seq.gt(" + temp[1] + ")))";
                }
                if (expArray[0].contains("<")) {
                    String[] temp = expArray[0].split("<");
                    exp = "count(filter(" + temp[0] + ",seq.lt(" + temp[1] + ")))";
                }
            }
            Object sumResult = AviatorEvaluator.execute(exp, env);
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
        return "COUNT";
    }

}
