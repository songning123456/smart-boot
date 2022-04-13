package com.sonin.core.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sonin.core.callback.IThreadPoolCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * @author sonin
 * @date 2021/12/6 8:28
 */
public class ThreadPool {

    private static final Log log = LogFactory.getLog(ThreadPool.class);

    private String threadPoolName;
    private Boolean daemon;
    private Integer priority;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Long keepAliveTime;
    private TimeUnit timeUnit;
    private Integer queueCapacity;
    private RejectedExecutionHandler rejectedExecutionHandler;

    ThreadPool() {
        threadPoolName = "default-" + System.currentTimeMillis();
        daemon = false;
        priority = 5;
        uncaughtExceptionHandler = (thread, exception) -> {
            log.error(thread.getName() + " => " + exception.getMessage());
        };
        corePoolSize = Runtime.getRuntime().availableProcessors();
        maximumPoolSize = 2 * Runtime.getRuntime().availableProcessors();
        keepAliveTime = 0L;
        timeUnit = TimeUnit.MICROSECONDS;
        queueCapacity = 10 * Runtime.getRuntime().availableProcessors();
        rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    public ThreadPool threadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
        return this;
    }

    public ThreadPool daemon(Boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadPool priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public ThreadPool uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    public ThreadPool corePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
        if (this.maximumPoolSize < corePoolSize) {
            this.maximumPoolSize = corePoolSize;
        }
        return this;
    }

    public ThreadPool maximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        if (this.corePoolSize > maximumPoolSize) {
            this.corePoolSize = maximumPoolSize;
        }
        return this;
    }

    public ThreadPool keepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPool timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPool queueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public ThreadPool rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    private ThreadPoolExecutor build() {
        String threadPoolName = this.threadPoolName;
        Boolean daemon = this.daemon;
        Integer priority = this.priority;
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.uncaughtExceptionHandler;
        Integer corePoolSize = this.corePoolSize;
        Integer maximumPoolSize = this.maximumPoolSize;
        Long keepAliveTime = this.keepAliveTime;
        TimeUnit timeUnit = this.timeUnit;
        Integer queueCapacity = this.queueCapacity;
        RejectedExecutionHandler rejectedExecutionHandler = this.rejectedExecutionHandler;
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("pool-" + threadPoolName + "-%d").setDaemon(daemon).setPriority(priority).setUncaughtExceptionHandler(uncaughtExceptionHandler).build();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, new LinkedBlockingQueue<>(queueCapacity), threadFactory, rejectedExecutionHandler);
    }

    public void execute(int loopSize, IThreadPoolCallback iThreadPoolCallback) throws Exception {
        ThreadPoolExecutor threadPoolExecutor = this.build();
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        for (int i = 0; i < loopSize; i++) {
            threadPoolExecutor.execute(() -> {
                try {
                    iThreadPoolCallback.doThreadPool();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(this.threadPoolName + " error: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        threadPoolExecutor.shutdown();
    }

}
