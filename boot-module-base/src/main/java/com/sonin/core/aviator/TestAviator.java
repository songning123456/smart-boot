package com.sonin.core.aviator;

import com.googlecode.aviator.AviatorEvaluator;

import java.util.*;

/**
 * <pre>
 * 计算框架测试demo
 * </pre>
 *
 * @author sonin
 * @version V0.1, 2020年7月27日 下午6:00:36
 */
public class TestAviator {

    public static void main(String[] args) {

        AviatorEvaluator.addFunction(new SumFunction());
        AviatorEvaluator.addFunction(new MinFunction());
        AviatorEvaluator.addFunction(new MaxFunction());
        AviatorEvaluator.addFunction(new AvgFunction());
        AviatorEvaluator.addFunction(new CountFunction());

        Map<String, Object> env = new HashMap<>();
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("A", "1");
        testMap.put("B", "2");
        System.out.println(AviatorEvaluator.execute("A+B", testMap));
        env.put("list", Arrays.asList(9.3, 2.5, 4.5));
        env.put("listExp", "list>3;list<10");
        //在计算前进行数据筛选
        //Object result = AviatorEvaluator.execute("SUM(list)-1+MIN(list)*10+MAX(list)/23+AVG(list)-5",env);
        Object result = AviatorEvaluator.execute("COUNT(list,listExp)/COUNT(list)", env);
        System.out.println(Double.parseDouble(String.format("%.2f", result)));
        // 聚合
        System.out.println(AviatorEvaluator.execute("SUM(list)", env));
        // 最小值
        System.out.println(AviatorEvaluator.execute("MIN(list)", env));
        // 最大值
        System.out.println(AviatorEvaluator.execute("MAX(list)", env));
        // 平均值
        System.out.println(AviatorEvaluator.execute("AVG(list)", env));
        // 最大值
        System.out.println(AviatorEvaluator.execute("max(list2)", env));
    }

}
