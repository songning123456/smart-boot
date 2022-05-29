package com.sonin.modules.labuladong.service.shuzu.huadongchuangkou;

import com.sonin.modules.labuladong.service.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 438. 找到字符串中所有字母异位词
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/29 11:23
 */
public class FindAnagrams implements Solution {

    @Override
    public Object handle() {
        String s = "cbaebabacd";
        String p = "abc";
        return findAnagrams(s, p);
    }

    public List<Integer> findAnagrams(String s, String p) {
        Map<Character, Integer> need = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (Character character : p.toCharArray()) {
            need.put(character, need.getOrDefault(character, 0) + 1);
        }
        int left = 0, right = 0, valid = 0;
        List<Integer> res = new ArrayList<>();
        while (right < s.length()) {
            Character toEnterChar = s.charAt(right);
            right++;

            if (need.containsKey(toEnterChar)) {
                window.put(toEnterChar, window.getOrDefault(toEnterChar, 0) + 1);
                if (window.get(toEnterChar).equals(need.get(toEnterChar))) {
                    valid++;
                }
            }

            while (right - left >= p.length()) {
                if (valid == need.size()) {
                    res.add(left);
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
        return res;
    }

}
