package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/3 11:11
 */
public class MinimumDeleteSum implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    private int[][] memory;

    public int minimumDeleteSum(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        memory = new int[m][n];
        for (int i = 0; i < m; i++) {
            Arrays.fill(memory[i], -1);
        }
        return dp(s1, 0, s2, 0);
    }

    private int dp(String s1, int i, String s2, int j) {
        int res = 0;
        if (i == s1.length()) {
            while (j < s2.length()) {
                res += s2.charAt(j);
                j++;
            }
            return res;
        }
        if (j == s2.length()) {
            while (i < s1.length()) {
                res += s1.charAt(i);
                i++;
            }
            return res;
        }
        if (memory[i][j] != -1) {
            return memory[i][j];
        }
        if (s1.charAt(i) == s2.charAt(j)) {
            memory[i][j] = dp(s1, i + 1, s2, j + 1);
        } else {
            memory[i][j] = Math.min(s1.charAt(i) + dp(s1, i + 1, s2, j), s2.charAt(j) + dp(s1, i, s2, j + 1));
        }
        return memory[i][j];
    }

}
