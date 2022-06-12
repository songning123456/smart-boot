package com.sonin.modules.labuladong.service.shuzu.constantarray;

import com.sonin.modules.labuladong.service.Solution;

import java.util.*;

/**
 * <pre>
 * 380. O(1) 时间插入、删除和获取随机元素
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/12 14:20
 */
public class RandomizedSet implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    // 存储元素的值
    private LinkedList<Integer> valList;

    // 存储元素的值 => 索引
    private LinkedHashMap<Integer, Integer> val2IndexMap;

    public RandomizedSet() {
        valList = new LinkedList<>();
        val2IndexMap = new LinkedHashMap<>();
    }

    public boolean insert(int val) {
        // 若val已存在，不用再插入
        if (val2IndexMap.containsKey(val)) {
            return false;
        }
        // 若val不存在，插入到nums尾部
        valList.add(val);
        // 记录val对应的索引值
        val2IndexMap.put(val, valList.size());
        return true;
    }

    public boolean remove(int val) {
        // 若val不存在，不用再删除
        if (!val2IndexMap.containsKey(val)) {
            return false;
        }
        // 获取val的索引
        int index = val2IndexMap.get(val);
        // 获取最后一个val值
        int lastVal = valList.getLast();
        // 交换val和lastVal位置，记得更新 val => index
        valList.set(valList.size() - 1, val);
        valList.set(index, lastVal);
        val2IndexMap.put(lastVal, index);
        // 此时val在最末尾，删除val
        valList.removeLast();
        val2IndexMap.remove(val);
        return true;
    }

    public int getRandom() {
        return valList.get((int) (Math.random() * valList.size()));
    }

}
