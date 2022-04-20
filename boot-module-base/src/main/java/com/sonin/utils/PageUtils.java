package com.sonin.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 分页工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:22
 */
public class PageUtils {

    public static <T> List<T> page(int pageNo, int pageSize, List<T> totalList) {
        List<T> pageList = new ArrayList<>();
        for (int i = (pageNo - 1) * pageSize; i < totalList.size() && i < pageNo * pageSize; i++) {
            pageList.add(totalList.get(i));
        }
        return pageList;
    }

}
