package com.sonin.modules.labuladong.service.shujujiegou.dandiaozhan;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Stack;

/**
 * <pre>
 * 739. 每日温度
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/15 11:09
 */
public class DailyTemperatures implements Solution {

    @Override
    public Object handle() {
        return dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73});
    }

    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] res = new int[n];
        // 存放索引
        Stack<Integer> stack = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && temperatures[stack.peek()] <= temperatures[i]) {
                stack.pop();
            }
            // 获取索引间距
            res[i] = stack.isEmpty() ? 0 : stack.peek() - i;
            // 索引入栈
            stack.push(i);
        }
        return res;
    }

}
