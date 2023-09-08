package com.sonin.core.aviator;

import com.googlecode.aviator.AviatorEvaluator;

import java.util.Map;

/**
 * 公共计算服务
 *
 * @author sonin
 */
public class CustomCalculation {
    /**
     * aviator对外提供计算接口，注意env中的key值千万不要以数字开头
     *
     * @param formula
     * @param env
     * @return
     */
    public static double calculation(String formula, Map<String, Object> env) {
        if (!AviatorEvaluator.containsFunction("SUM")) {
            AviatorEvaluator.addFunction(new SumFunction());
        }
        if (!AviatorEvaluator.containsFunction("MIN")) {
            AviatorEvaluator.addFunction(new MinFunction());
        }
        if (!AviatorEvaluator.containsFunction("MAX")) {
            AviatorEvaluator.addFunction(new MaxFunction());
        }
        if (!AviatorEvaluator.containsFunction("AVG")) {
            AviatorEvaluator.addFunction(new AvgFunction());
        }
        if (!AviatorEvaluator.containsFunction("COUNT")) {
            AviatorEvaluator.addFunction(new CountFunction());
        }
        Object result = AviatorEvaluator.execute(formula, env);
        return Double.parseDouble(String.format("%.2f", result));
    }

}
