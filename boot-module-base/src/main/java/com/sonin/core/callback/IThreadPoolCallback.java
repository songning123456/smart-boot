package com.sonin.core.callback;

import org.springframework.lang.Nullable;

/**
 * @author sonin
 * @date 2021/12/6 8:37
 */
@FunctionalInterface
public interface IThreadPoolCallback {

    @Nullable
    void doThreadPool();

}
