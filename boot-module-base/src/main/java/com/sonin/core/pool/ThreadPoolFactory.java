package com.sonin.core.pool;

/**
 * @author sonin
 * @date 2021/12/6 8:40
 */
public class ThreadPoolFactory {

    public static ThreadPool create() {
        return new ThreadPool();
    }
}
