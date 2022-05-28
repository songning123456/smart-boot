package com.sonin.modules.labuladong.service.shujujiegou.lru;

import com.sonin.modules.labuladong.entity.DoubleList;
import com.sonin.modules.labuladong.entity.Node;
import com.sonin.modules.labuladong.service.Solution;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 146. LRU 缓存
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/20 15:12
 */
public class LRUCache implements Solution {

    public LRUCache() {
    }

    @Override
    public Object handle() {
        LRUCache lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3
        lRUCache.get(4);    // 返回 4
        return null;
    }

    private Map<Integer, Node> key2NodeMap;

    // Node(k1, v1) <=> Node(k2, v2)...
    private DoubleList doubleList;

    // 容量
    private Integer capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        key2NodeMap = new HashMap<>();
        doubleList = new DoubleList();
    }

    // key提升为最近使用
    private void makeRecently(int key) {
        Node x = key2NodeMap.get(key);
        doubleList.remove(x);
        doubleList.addLast(x);
    }

    // 添加最近使用
    private void addRecently(int key, int val) {
        Node x = new Node(key, val);
        doubleList.addLast(x);
        key2NodeMap.put(key, x);
    }

    // 删除key
    private void deleteKey(int key) {
        Node x = key2NodeMap.get(key);
        doubleList.remove(x);
        key2NodeMap.remove(key);
    }

    // 删除最久未使用的
    private void removeLeastRecently() {
        Node target = doubleList.removeFirst();
        key2NodeMap.remove(target.key);
    }

    public int get(int key) {
        if (!key2NodeMap.containsKey(key)) {
            return -1;
        }
        makeRecently(key);
        return key2NodeMap.get(key).val;
    }

    public void put(int key, int value) {
        if (key2NodeMap.containsKey(key)) {
            deleteKey(key);
            addRecently(key, value);
            return;
        }
        if (capacity == key2NodeMap.size()) {
            removeLeastRecently();
        }
        addRecently(key, value);
    }

}
