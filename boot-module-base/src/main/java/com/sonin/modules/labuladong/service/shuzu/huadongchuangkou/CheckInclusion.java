package com.sonin.modules.labuladong.service.shuzu.huadongchuangkou;

import com.sonin.modules.labuladong.service.Solution;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 567. 字符串的排列
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/29 11:15
 */
public class CheckInclusion implements Solution {

    @Override
    public Object handle() {
        String s1 = "ab";
        String s2 = "eidbaooo";
        return checkInclusion(s1, s2);
    }

    public boolean checkInclusion(String s1, String s2) {
        Map<Character, Integer> need = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (Character character : s1.toCharArray()) {
            need.put(character, need.getOrDefault(character, 0) + 1);
        }
        int left = 0, right = 0, valid = 0;
        while (right < s2.length()) {
            Character toEnterChar = s2.charAt(right);
            right++;

            if (need.containsKey(toEnterChar)) {
                window.put(toEnterChar, window.getOrDefault(toEnterChar, 0) + 1);
                if (window.get(toEnterChar).equals(need.get(toEnterChar))) {
                    valid++;
                }
            }

            while (right - left >= s1.length()) {
                if (valid == need.size()) {
                    return true;
                }
                Character toLeaveChar = s2.charAt(left);
                left++;
                if (need.containsKey(toLeaveChar)) {
                    if (window.get(toLeaveChar).equals(need.get(toLeaveChar))) {
                        valid--;
                    }
                    window.put(toLeaveChar, window.get(toLeaveChar) - 1);
                }
            }
        }
        return false;
    }

}
