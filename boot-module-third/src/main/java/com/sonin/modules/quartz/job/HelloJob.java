package com.sonin.modules.quartz.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * <pre>
 * 测试Job
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 14:56
 */
@Slf4j
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Hello Job执行时间: " + DateUtil.now());
    }

}
