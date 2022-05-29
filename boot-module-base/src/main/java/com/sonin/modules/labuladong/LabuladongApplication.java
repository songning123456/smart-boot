package com.sonin.modules.labuladong;

import com.sonin.modules.labuladong.service.Solution;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * <pre>
 * labuladong的算法小抄
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/15 8:29
 */
@Slf4j
public class LabuladongApplication {

    /**
    * <pre>
    * main函数入口
    * </pre>
     * @param args
    * @author sonin
    * @Description: TODO(这里描述这个方法的需求变更情况)
    */
    public static void main(String[] args) throws Exception {
        String className;
        // 下一个更大元素
        // className = initClassName("shujujiegou", "dandiaozhan", "NextGreaterElements");
        // 下一个更大元素II
        // className = initClassName("shujujiegou", "dandiaozhan", "NextGreaterElementsII");
        // 每日温度
        // className = initClassName("shujujiegou", "dandiaozhan", "DailyTemperatures");
        // LRU
        // className = initClassName("shujujiegou", "lru", "LRUCache");
        // 最小覆盖子串
        // className = initClassName("shuzu", "huadongchuangkou", "MinWindow");
        // 字符串的排列
        // className = initClassName("shuzu", "huadongchuangkou", "CheckInclusion");
        // 找到字符串中所有字母异位词
        // className = initClassName("shuzu", "huadongchuangkou", "FindAnagrams");
        // 无重复字符的最长子串
        className = initClassName("shuzu", "huadongchuangkou", "LengthOfLongestSubstring");
        execute(className);
    }

    /**
     * <pre>
     * 初始化类名
     * </pre>
     *
     * @param suffixNames
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private static String initClassName(String... suffixNames) {
        // 基本前缀
        String basePrefix = "com.sonin.modules.labuladong.service.";
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        for (int i = 0; i < suffixNames.length; i++) {
            stringBuilder.append(suffixNames[i]);
            if (i != suffixNames.length - 1) {
                stringBuilder.append(".");
            }
        }
        return basePrefix + stringBuilder.toString();
    }

    /**
     * <pre>
     * 执行结果
     * </pre>
     *
     * @param className
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private static void execute(String className) throws Exception {
        // 生成class
        Solution solution = (Solution) Class.forName(className).newInstance();
        // 执行结果
        Object result = solution.handle();
        // 解析结果，方便输出
        if (result instanceof int[]) {
            result = Arrays.toString((int[]) result);
        }
        log.info("{}执行结果: {}", solution.getClass().getSimpleName(), result);
    }

}
