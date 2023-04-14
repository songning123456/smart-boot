package com.sonin.modules.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.core.vo.Result;
import com.sonin.core.mpp.BaseFactory;
import com.sonin.modules.quartz.dto.QuartzJobDTO;
import com.sonin.modules.quartz.entity.QrtzCronTriggers;
import com.sonin.modules.quartz.entity.QrtzJobDetails;
import com.sonin.modules.quartz.entity.QrtzTriggers;
import com.sonin.modules.quartz.entity.QuartzJob;
import com.sonin.utils.BeanExtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 14:56
 */
@Slf4j
@RestController
@RequestMapping("/quartzJob")
public class QuartzJobController {

    @Autowired
    private Scheduler scheduler;

    @GetMapping("/page")
    public Result<Object> pageCtrl(QuartzJobDTO quartzJobDTO) {
        Result<Object> result = new Result<>();
        IPage<Map<String, Object>> mapIPage = null;
        try {
            mapIPage = BaseFactory.WHERE()
                    .from(QrtzTriggers.class, QrtzCronTriggers.class, QrtzJobDetails.class)
                    .where()
                    .eq(true, QrtzTriggers.class.getDeclaredField("triggerGroup"), QrtzCronTriggers.class.getDeclaredField("triggerGroup"))
                    .eq(true, QrtzTriggers.class.getDeclaredField("triggerName"), QrtzCronTriggers.class.getDeclaredField("triggerName"))
                    .eq(true, QrtzTriggers.class.getDeclaredField("jobGroup"), QrtzJobDetails.class.getDeclaredField("jobGroup"))
                    .eq(true, QrtzTriggers.class.getDeclaredField("jobName"), QrtzJobDetails.class.getDeclaredField("jobName"))
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getJobGroup()), "qrtz_triggers.job_group", quartzJobDTO.getJobGroup())
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getJobName()), "qrtz_triggers.job_name", quartzJobDTO.getJobName())
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getJobClassName()), "qrtz_job_details.job_class_name", quartzJobDTO.getJobClassName())
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getTriggerGroup()), "qrtz_triggers.trigger_group", quartzJobDTO.getTriggerGroup())
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getTriggerName()), "qrtz_triggers.trigger_name", quartzJobDTO.getTriggerName())
                    .like(StringUtils.isNotEmpty(quartzJobDTO.getCronExpression()), "qrtz_cron_triggers.cron_expression", quartzJobDTO.getCronExpression())
                    .queryForPage(new Page<>(quartzJobDTO.getCurrentPage(), quartzJobDTO.getPageSize()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("分页查询Job失败: {}", e.getMessage());
        }
        result.setResult(mapIPage);
        return result;
    }

    @PostMapping("/save")
    public Result saveCtrl(@RequestBody QuartzJobDTO quartzJobDTO) {
        try {
            QuartzJob quartzJob = BeanExtUtils.bean2Bean(quartzJobDTO, QuartzJob.class);
            // 生成 Class<? extends Job> jobClass
            Class<?> jobClass = Class.forName(quartzJob.getJobClassName());
            Job job = (Job) jobClass.newInstance();
            // 构建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(job.getClass())
                    .withIdentity(quartzJob.getJobName(), quartzJob.getJobGroup())
                    .build();
            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(quartzJob.getTriggerName(), quartzJob.getTriggerGroup())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression()))
                    .build();
            // 启动调度器
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("新增Job失败: {}", e.getMessage());
            return Result.error("新增Job失败: " + e.getMessage());
        }
        return Result.ok();
    }

    @GetMapping("/pause")
    public Result pauseCtrl(QuartzJobDTO quartzJobDTO) {
        try {
            scheduler.pauseJob(JobKey.jobKey(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("暂停Job失败: {}", e.getMessage());
            return Result.error("暂停Job失败: " + e.getMessage());
        }
        return Result.ok();
    }

    @GetMapping("/resume")
    public Result resumeCtrl(QuartzJobDTO quartzJobDTO) {
        try {
            scheduler.resumeJob(JobKey.jobKey(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("恢复Job失败: {}", e.getMessage());
            return Result.error("恢复Job失败: " + e.getMessage());
        }
        return Result.ok();
    }

    @GetMapping("/reschedule")
    public Result rescheduleCtrl(QuartzJobDTO quartzJobDTO) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(quartzJobDTO.getTriggerName(), quartzJobDTO.getTriggerGroup());
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJobDTO.getCronExpression());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行，重启触发器
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("重启Job失败: {}", e.getMessage());
            return Result.error("重启Job失败: " + e.getMessage());
        }
        return Result.ok();
    }

    @DeleteMapping("/delete")
    public Result deleteCtrl(QuartzJobDTO quartzJobDTO) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()));
            scheduler.unscheduleJob(TriggerKey.triggerKey(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()));
            scheduler.deleteJob(JobKey.jobKey(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup()));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除Job失败: {}", e.getMessage());
            return Result.error("删除Job失败: " + e.getMessage());
        }
        return Result.ok();
    }

}
