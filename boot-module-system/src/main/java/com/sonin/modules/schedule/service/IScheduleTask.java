package com.sonin.modules.schedule.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/17 8:34
 */
public interface IScheduleTask {

    Map<String, ScheduledExecutorService> task2ScheduleMap = new ConcurrentHashMap<>();

    void startTask(String taskName);

    void stopTask(String taskName);

    void restartTask(String taskName);

}
