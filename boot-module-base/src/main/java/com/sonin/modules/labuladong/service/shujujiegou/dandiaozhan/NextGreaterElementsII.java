package com.sonin.modules.labuladong.service.shujujiegou.dandiaozhan;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Stack;

/**
 * <pre>
 * 503. 下一个更大元素 II
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/15 10:54
 */
public class NextGreaterElementsII implements Solution {

    @Override
    public Object handle() {
        return this.nextGreaterElements(new int[]{2, 1, 2, 4, 3});
    }

    public int[] nextGreaterElements(int[] nums) {
        int len = nums.length;
        // 存放答案的数组
        int[] res = new int[nums.length];
        Stack<Integer> stack = new Stack<>();
        // 倒着入栈
        for (int i = 2 * nums.length - 1; i >= 0; i--) {
            while (!stack.isEmpty() && stack.peek() <= nums[i % len]) {
                // 矮个子起开，反正被挡着了
                stack.pop();
            }
            res[i % len] = stack.isEmpty() ? -1 : stack.peek();
            // 入栈
            stack.push(nums[i % len]);
        }
        return res;
    }

}
