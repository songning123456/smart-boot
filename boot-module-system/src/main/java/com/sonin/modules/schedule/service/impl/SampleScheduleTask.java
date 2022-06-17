package com.sonin.modules.schedule.service.impl;

import com.sonin.modules.schedule.service.IScheduleTask;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/17 8:36
 */
public class SampleScheduleTask extends TimerTask implements IScheduleTask {

    private static final Log log = LogFactory.getLog(SampleScheduleTask.class);

    private static IScheduleTask instance = null;

    public static IScheduleTask getInstance() {
        if (instance == null) {
            synchronized (SampleScheduleTask.class) {
                if (instance == null) {
                    instance = new SampleScheduleTask();
                }
            }
        }
        return instance;
    }

    @Override
    public void startTask(String taskName) {
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getName() + " task start...");
        }
        if (task2ScheduleMap.get(taskName) != null) {
            log.info(this.getClass().getName() + " task is already start!");
        } else {
            ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("schedule-task-pool-%d").daemon(true).build());
            // initialDelay: 第一次开始任务时要延迟的时间
            // period: 每次多少秒执行
            scheduledExecutorService.scheduleAtFixedRate(this, 100, 10, TimeUnit.SECONDS);
            task2ScheduleMap.put(this.getClass().getName(), scheduledExecutorService);
        }
    }

    @Override
    public void stopTask(String taskName) {
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getName() + " task stop...");
        }
        if (task2ScheduleMap.get(taskName) != null) {
            task2ScheduleMap.remove(taskName).shutdown();
        } else {
            log.info(this.getClass().getName() + " task is already stop!");
        }
    }

    @Override
    public void restartTask(String taskName) {
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getName() + " task restart...");
        }
        stopTask(taskName);
        startTask(taskName);
    }

    @Override
    public void run() {
        log.info(this.getClass().getName() + " begin to run...");
    }

}
