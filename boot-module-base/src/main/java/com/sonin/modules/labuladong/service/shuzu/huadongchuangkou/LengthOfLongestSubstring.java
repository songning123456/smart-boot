package com.sonin.modules.labuladong.service.shuzu.huadongchuangkou;

import com.sonin.modules.labuladong.service.Solution;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 3. 无重复字符的最长子串
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/29 11:27
 */
public class LengthOfLongestSubstring implements Solution {

    @Override
    public Object handle() {
        String s = "abcabcbb";
        return lengthOfLongestSubstring(s);
    }

    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> window = new HashMap<>();
        int left = 0, right = 0, res = 0;
        while (right < s.length()) {
            Character toEnterChar = s.charAt(right);
            right++;
            window.put(toEnterChar, window.getOrDefault(toEnterChar, 0) + 1);
            while (window.get(toEnterChar) > 1) {
                Character toLeaveChar = s.charAt(left);
                left++;
                window.put(toLeaveChar, window.get(toLeaveChar) - 1);
            }
            res = Math.max(res, right - left);
        }
        return res;
    }

}
