package com.sonin.modules.logback;

import com.sonin.modules.sys.entity.SysLog;
import com.sonin.modules.sys.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <pre>
 * 定时任务存储sys_log日志
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/5 17:48
 */
@Configuration
@EnableScheduling
public class LogbackTask {

    @Autowired
    private SysLogService sysLogService;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Scheduled(fixedDelay = 10000)
    public void execute() {
        SysLog sysLog;
        while ((sysLog = ILogback.sysLogQueue.poll()) != null) {
            sysLogService.save(sysLog);
        }
    }

}
