package com.sonin.modules.schedule.controller;

import com.sonin.api.vo.Result;
import com.sonin.modules.schedule.service.IScheduleTask;
import com.sonin.modules.schedule.service.impl.SampleScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * 定时任务
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/17 8:52
 */
@Slf4j
@RestController
@RequestMapping("/scheduleTask")
public class ScheduleTaskController {

    private String prefix = "com.sonin.modules.schedule.service.impl.";

    @GetMapping("/start")
    public Result<Object> startScheduleTask(@RequestParam String taskName) throws Exception {
        IScheduleTask iScheduleTask = null;
        if (Class.forName(prefix + taskName) == SampleScheduleTask.class) {
            iScheduleTask = SampleScheduleTask.getInstance();
        }
        assert iScheduleTask != null;
        iScheduleTask.startTask(prefix + taskName);
        return Result.ok();
    }

    @GetMapping("/stop")
    public Result<Object> stopScheduleTask(@RequestParam String taskName) throws Exception {
        IScheduleTask iScheduleTask = null;
        if (Class.forName(prefix + taskName) == SampleScheduleTask.class) {
            iScheduleTask = SampleScheduleTask.getInstance();
        }
        assert iScheduleTask != null;
        iScheduleTask.stopTask(prefix + taskName);
        return Result.ok();
    }

    @GetMapping("/restart")
    public Result<Object> restartScheduleTask(@RequestParam String taskName) throws Exception {
        IScheduleTask iScheduleTask = null;
        if (Class.forName(prefix + taskName) == SampleScheduleTask.class) {
            iScheduleTask = SampleScheduleTask.getInstance();
        }
        assert iScheduleTask != null;
        iScheduleTask.restartTask(prefix + taskName);
        return Result.ok();
    }

}
