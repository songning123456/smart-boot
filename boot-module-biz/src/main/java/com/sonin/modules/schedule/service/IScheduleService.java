package com.sonin.modules.schedule.service;

/**
 * @Author：sonin
 * @Date：2024/4/17 10:52
 */
public interface IScheduleService {

    void generateViewFunc(String endTime);

    void generateDataFunc(String startTime, String endTime);

}
