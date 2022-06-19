package com.sonin.modules.labuladong.service.shuzu.quanzhongsuiji;

import java.util.Random;

/**
 * <pre>
 * 528. 按权重随机选择
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/19 15:14
 */
public class Solution {

    private int[] preSum;

    private Random random = new Random();

    public Solution(int[] w) {
        int n = w.length;
        preSum = new int[n + 1];
        preSum[0] = 0;
        for (int i = 1; i <= n; i++) {
            preSum[i] = preSum[i - 1] + w[i - 1];
        }
    }

    public int pickIndex() {
        int n = preSum.length;
        // 0 <= nextInt(n) < n => [1, preSum[n - 1]]
        int target = random.nextInt(preSum[n - 1]) + 1;
        return leftBound(preSum, target) - 1;
    }

    private int leftBound(int[] nums, int target) {
        if (nums.length == 0) {
            return -1;
        }
        int left = 0, right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                right = mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] > target) {
                right = mid;
            }
        }
        return left;
    }

}
