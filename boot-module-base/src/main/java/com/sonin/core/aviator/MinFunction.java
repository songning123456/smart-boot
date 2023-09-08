package com.sonin.core.aviator;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 最小值计算服务
 *
 * @author sonin
 */
public class MinFunction extends AbstractFunction {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        List obj = (List) arg1.getValue(env);
        //排序
        if (obj != null && obj.size() > 0) {
            Collections.sort(obj);
            return new AviatorDouble(Double.parseDouble(obj.get(0).toString()));
        } else {
            return new AviatorDouble(0.00);
        }
    }

    @Override
    public String getName() {
        return "MIN";
    }
}
