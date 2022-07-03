package com.sonin.modules.labuladong.service.dongtaiguihua;

import com.sonin.modules.labuladong.service.Solution;

import java.util.Arrays;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/3 9:37
 */
public class MinFallingPathSum implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    private int[][] memory;

    public int minFallingPathSum(int[][] matrix) {
        int n = matrix.length;
        int res = Integer.MAX_VALUE;
        memory = new int[n][n];
        for (int[] tmp : memory) {
            Arrays.fill(tmp, 66666);
        }
        for (int j = 0; j < n; j++) {
            res = Math.min(res, dp(matrix, n - 1, j));
        }
        return res;
    }

    private int dp(int[][] matrix, int i, int j) {
        if (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length) {
            return 99999;
        }
        if (i == 0) {
            return matrix[0][j];
        }
        if (memory[i][j] != 66666) {
            return memory[i][j];
        }
        memory[i][j] = matrix[i][j] + min(dp(matrix, i - 1, j - 1), dp(matrix, i - 1, j), dp(matrix, i - 1, j + 1));
        return memory[i][j];
    }

    private int min(int i, int j, int k) {
        return Math.min(i, Math.min(j, k));
    }

}
