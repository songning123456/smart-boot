package com.sonin.modules.quartz.entity;

import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 16:18
 */
@Data
public class QrtzTriggers {

    private String schedName;

    private String triggerName;

    private String triggerGroup;

    private String jobName;

    private String jobGroup;

    private String description;

    private Integer nextFireTime;

    private Integer prevFireTime;

    private Integer priority;

    private String triggerState;

    private String triggerType;

    private Integer startTime;

    private Integer endTime;

    private String calendarName;

    private Integer misfireInstr;

    private String jobData;

}
