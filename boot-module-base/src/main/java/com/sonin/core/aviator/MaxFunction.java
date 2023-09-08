package com.sonin.core.aviator;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 最大值计算服务
 *
 * @author sonin
 */
public class MaxFunction extends AbstractFunction {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        List obj = (List) arg1.getValue(env);
        //排序
        Collections.sort(obj);
        if (obj.size() > 0) {
            return new AviatorDouble(Double.parseDouble(obj.get(obj.size() - 1).toString()));
        } else {
            return new AviatorDouble(0.00);
        }
    }

    @Override
    public String getName() {
        return "MAX";
    }
}
