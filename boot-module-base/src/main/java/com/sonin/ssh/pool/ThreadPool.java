package com.sonin.ssh.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sonin.ssh.enums.ConstantEnum;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sonin
 * @date 2020/10/1 16:17
 */
public class ThreadPool {

    private static volatile ThreadPoolExecutor threadPoolExecutor;

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            synchronized (ThreadPool.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(ConstantEnum.getValue("threadPool", "corePoolSize"), ConstantEnum.getValue("threadPool", "maximumPoolSize"), ConstantEnum.getValue("threadPool", "keepAliveTime"), TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>((Integer) ConstantEnum.getValue("threadPool", "capacity")), new ThreadFactoryBuilder().setNameFormat("SSH-%d").setDaemon(true).build());
                }
            }
        }
        return threadPoolExecutor;
    }

}
