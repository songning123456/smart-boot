package com.sonin.modules.quartz.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @Author: CJ
 * @Date: 2021-11-2 11:33
 */
@Slf4j
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Hello Job执行时间: " + DateUtil.now());
    }

}
