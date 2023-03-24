package com.sonin.modules.quartz.entity;

import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 16:52
 */
@Data
public class QrtzCronTriggers {

    private String schedName;

    private String triggerName;

    private String triggerGroup;

    private String cronExpression;

    private String timeZoneId;

}
