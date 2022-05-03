package com.sonin.modules.quartz.entity;

import lombok.Data;

/**
 * <pre>
 * Quartz Job
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 14:48
 */
@Data
public class QuartzJob {

    private String jobClassName;

    private String jobName;

    private String jobGroup;

    private String triggerName;

    private String triggerGroup;

    private String cronExpression;

}
