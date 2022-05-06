package com.sonin.modules.sys.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author sonin
 * @since 2022-05-05
 */
@Data
public class SysLog implements Serializable {

    private String id;

    /**
     * 日志打印的时间
     */
    private String timeStamp;

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 日志打印级别
     */
    private String loggerLevel;

    /**
     * 类的全路径名称
     */
    private String loggerName;

    /**
     * 打印的消息
     */
    private String message;

    /**
     * 日志类型: 1:logback,2:log4j2
     */
    private Integer loggerType;

    /**
     * IP地址
     */
    private String loggerIp;

    /**
     * 端口号
     */
    private Integer loggerPort;

}
