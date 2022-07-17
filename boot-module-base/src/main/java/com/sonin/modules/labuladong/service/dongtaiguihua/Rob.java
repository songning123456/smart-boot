package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * 198. 打家劫舍
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/17 8:33
 */
public class Rob implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    private int[] memory;

    public int rob(int[] nums) {
        memory = new int[nums.length];
        Arrays.fill(memory, -1);
        return dp(nums, 0);
    }

    private int dp(int[] nums, int start) {
        if (start >= nums.length) {
            return 0;
        }
        if (memory[start] != -1) {
            return memory[start];
        }
        int res = Math.max(dp(nums, start + 1), dp(nums, start + 2) + nums[start]);
        memory[start] = res;
        return res;
    }

}
