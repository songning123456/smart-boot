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
    private String logLevel;

    /**
     * 类的全路径名称
     */
    private String logName;

    /**
     * 打印的消息
     */
    private String message;

    /**
     * 日志类型: 1:logback,2:log4j2
     */
    private Integer logType;

    /**
     * IP地址
     */
    private String logIp;

    /**
     * 端口号
     */
    private Integer logPort;

}
