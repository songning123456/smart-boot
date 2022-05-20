package com.sonin.modules.labuladong.service.shujujiegou.lru;

import com.sonin.modules.labuladong.service.Solution;

/**
 * <pre>
 * 146. LRU 缓存
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/20 15:12
 */
public class LRUCache implements Solution {

    @Override
    public Object handle() {
        LRUCache lruCache = new LRUCache(10);
        return lruCache.get(0);
    }

    public LRUCache(int capacity) {

    }

    public int get(int key) {
        return 0;
    }

    public void put(int key, int value) {

    }

}
