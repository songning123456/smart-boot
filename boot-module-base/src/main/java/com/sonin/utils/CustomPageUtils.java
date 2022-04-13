package com.sonin.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sonin
 * @date 2021/11/1 13:40
 * 假分页
 */
public class CustomPageUtils {

    public static <T> List<T> page(int pageNo, int pageSize, List<T> totalList) {
        List<T> pageList = new ArrayList<>();
        for (int i = (pageNo - 1) * pageSize; i < totalList.size() && i < pageNo * pageSize; i++) {
            pageList.add(totalList.get(i));
        }
        return pageList;
    }

}
