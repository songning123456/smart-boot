package com.sonin.modules.labuladong.service.shuzu.huadongchuangkou;

import com.sonin.modules.labuladong.service.Solution;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 76. 最小覆盖子串
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/29 10:46
 */
public class MinWindow implements Solution {

    @Override
    public Object handle() {
        String s = "ADOBECODEBANC";
        String t = "ABC";
        return minWindow(s, t);
    }

    public String minWindow(String s, String t) {
        Map<Character, Integer> need = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (Character character : t.toCharArray()) {
            need.put(character, need.getOrDefault(character, 0) + 1);
        }
        int left = 0, right = 0, valid = 0;
        int start = 0, len = Integer.MAX_VALUE;
        while (right < s.length()) {
            Character toEnterChar = s.charAt(right);
            right++;

            if (need.containsKey(toEnterChar)) {
                window.put(toEnterChar, window.getOrDefault(toEnterChar, 0) + 1);
                if (window.get(toEnterChar).equals(need.get(toEnterChar))) {
                    valid++;
                }
            }

            while (valid == need.size()) {
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                Character toLeaveChar = s.charAt(left);
                left++;
                if (need.containsKey(toLeaveChar)) {
                    if (window.get(toLeaveChar).equals(need.get(toLeaveChar))) {
                        valid--;
                    }
                    window.put(toLeaveChar, window.get(toLeaveChar) - 1);
                }
            }
        }
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }

}
