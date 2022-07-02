package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

/**
 * <pre>
 * 509. 斐波那契数
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 17:24
 */
public class Fib implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int fib(int n) {
        if (n ==0) {
            return 0;
        }
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

}
