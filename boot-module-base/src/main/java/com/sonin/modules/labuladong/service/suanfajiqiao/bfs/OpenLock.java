package com.sonin.modules.labuladong.service.suanfajiqiao.bfs;

import com.sonin.modules.labuladong.service.Solution;

import java.util.*;

/**
 * <pre>
 * 752. 打开转盘锁
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/26 11:21
 */
public class OpenLock implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int openLock(String[] deadends, String target) {
        Set<String> deadSet = new HashSet<>();
        Collections.addAll(deadSet, deadends);
        Set<String> visitedSet = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        int step = 0;
        queue.offer("0000");
        visitedSet.add("0000");

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String cur = queue.poll();
                if (cur == null) {
                    break;
                }
                if (deadSet.contains(cur)) {
                    continue;
                }
                if (cur.equals(target)) {
                    return step;
                }
                for (int j = 0; j < 4; j++) {
                    String up = plusOne(cur, j);
                    if (!visitedSet.contains(up)) {
                        queue.offer(up);
                        visitedSet.add(up);
                    }
                    String down = minuOne(cur, j);
                    if (!visitedSet.contains(down)) {
                        queue.offer(down);
                        visitedSet.add(down);
                    }
                }
            }
            step++;
        }
        return -1;
    }

    private String plusOne(String s, int j) {
        char[] ch = s.toCharArray();
        if (ch[j] == '9') {
            ch[j] = '0';
        } else {
            ch[j] += 1;
        }
        return new String(ch);
    }

    private String minuOne(String s, int j) {
        char[] ch = s.toCharArray();
        if (ch[j] == '0') {
            ch[j] = '9';
        } else {
            ch[j] -= 1;
        }
        return new String(ch);
    }

}
