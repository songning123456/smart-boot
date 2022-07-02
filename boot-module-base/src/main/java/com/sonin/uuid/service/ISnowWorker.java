package com.sonin.uuid.service;

import com.sonin.uuid.exception.IdGeneratorException;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:35
 */
public interface ISnowWorker {

    long nextId() throws IdGeneratorException;

}
