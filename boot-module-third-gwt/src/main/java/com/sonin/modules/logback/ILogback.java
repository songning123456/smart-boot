package com.sonin.modules.logback;

import com.sonin.modules.sys.entity.SysLog;

import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/5 16:57
 */
public interface ILogback {

    BlockingQueue<SysLog> sysLogQueue = new LinkedBlockingQueue<>();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

}
