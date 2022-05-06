package com.sonin.modules.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.sonin.modules.sys.entity.SysLog;

import java.net.InetAddress;
import java.util.Date;

/**
 * <pre>
 * 自定义ConsoleAppender
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/5 16:55
 */
public class DBConsoleAppender extends ConsoleAppender<ILoggingEvent> implements ILogback {

    private InetAddress inetAddress;

    private int logPort = 8082;

    public DBConsoleAppender() {
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        SysLog sysLog = new SysLog();
        sysLog.setLogName(eventObject.getLoggerName());
        sysLog.setMessage(eventObject.getMessage());
        sysLog.setThreadName(eventObject.getThreadName());
        sysLog.setTimeStamp(simpleDateFormat.format(new Date(eventObject.getTimeStamp())));
        sysLog.setLogLevel(eventObject.getLevel().toString());
        sysLog.setLogType(1);
        sysLog.setLogIp(inetAddress.getHostAddress());
        sysLog.setLogPort(logPort);
        // 添加到阻塞队列
        sysLogQueue.add(sysLog);
    }

}
