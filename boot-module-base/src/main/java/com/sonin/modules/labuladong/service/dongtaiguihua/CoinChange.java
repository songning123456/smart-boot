package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 17:38
 */
public class CoinChange implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    private int[] memory;

    public int coinChange(int[] coins, int amount) {
        memory = new int[amount + 1];
        Arrays.fill(memory, -666);
        return dp(coins, amount);
    }

    private int dp(int[] coins, int amount) {
        if (amount < 0) {
            return -1;
        } else if (amount == 0) {
            return 0;
        }
        if (memory[amount] != -666) {
            return memory[amount];
        }
        int res = Integer.MAX_VALUE;
        for (int coin : coins) {
            int sub = dp(coins, amount - coin);
            if (sub < 0) {
                continue;
            }
            res = Math.min(res, sub + 1);
        }
        memory[amount] = (res == Integer.MAX_VALUE) ? -1 : res;
        return memory[amount];
    }

}
