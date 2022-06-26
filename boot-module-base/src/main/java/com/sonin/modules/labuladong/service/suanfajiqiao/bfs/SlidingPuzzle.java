package com.sonin.modules.labuladong.service.suanfajiqiao.bfs;

import com.sonin.modules.labuladong.service.Solution;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/26 15:07
 */
public class SlidingPuzzle implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int slidingPuzzle(int[][] board) {
        int m = 2, n = 3;
        StringBuilder sb = new StringBuilder();
        String target = "123450";
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j]);
            }
        }
        String start = sb.toString();
        int[][] neighbor = new int[][]{
                {1, 3},
                {0, 4, 2},
                {1, 5},
                {0, 4},
                {3, 1, 5},
                {4, 2}
        };
        Queue<String> queue = new LinkedList<>();
        Set<String> visitedSet = new HashSet<>();
        queue.offer(start);
        visitedSet.add(start);
        int step = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur == null) {
                    break;
                }
                if (target.equals(cur)) {
                    return step;
                }
                int idx = 0;
                while (cur.charAt(idx) != '0') {
                    idx++;
                }
                for (int adj : neighbor[idx]) {
                    String newBoard = swap(cur.toCharArray(), adj, idx);
                    if (!visitedSet.contains(newBoard)) {
                        queue.offer(newBoard);
                        visitedSet.add(newBoard);
                    }
                }
            }
            step++;
        }
        return -1;
    }

    private String swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
        return new String(chars);
    }

}
