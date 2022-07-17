package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * 213. 打家劫舍 II
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/17 8:51
 */
public class RobII implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return nums[n - 1];
        }
        int[] memory1 = new int[nums.length];
        int[] memory2 = new int[nums.length];
        Arrays.fill(memory1, -1);
        Arrays.fill(memory2, -1);
        return Math.max(dp(nums, 0, n - 2, memory1), dp(nums, 1, n - 1, memory2));
    }

    private int dp(int[] nums, int start, int end, int[] memory) {
        if (start > end) {
            return 0;
        }
        if (memory[start] != -1) {
            return memory[start];
        }
        int res = Math.max(dp(nums, start + 1, end, memory), dp(nums, start + 2, end, memory) + nums[start]);
        memory[start] = res;
        return res;
    }

}
