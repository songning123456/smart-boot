package com.sonin.modules.constant;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/6/9 17:56
 */
public interface IDataQueue {

    BlockingQueue<byte[]> DATA_QUEUE = new LinkedBlockingQueue<>();

}
