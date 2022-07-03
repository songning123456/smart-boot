package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/3 9:19
 */
public class LengthOfLIS implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int lengthOfLIS(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }
        int res = 0;
        for (int value : dp) {
            res = Math.max(res, value);
        }
        return res;
    }

}
