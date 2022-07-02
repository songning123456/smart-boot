package com.sonin.uuid.helper;

import com.sonin.uuid.entity.IdGeneratorOptions;
import com.sonin.uuid.exception.IdGeneratorException;
import com.sonin.uuid.service.IIdGenerator;
import com.sonin.uuid.service.impl.DefaultIdGenerator;

/**
 * <pre>
 * unique id生成器
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/2 15:52
 */
public class UniqueIdHelper {

    private static IIdGenerator idGenInstance = null;


    /**
     * 设置参数，建议程序初始化时执行一次
     */
    public static void setIdGenerator(IdGeneratorOptions options) throws IdGeneratorException {
        idGenInstance = new DefaultIdGenerator(options);
    }

    /**
     * 生成新的Id
     * 调用本方法前，请确保调用了 SetIdGenerator 方法做初始化。
     *
     * @return
     */
    public static long nextId() throws IdGeneratorException {
        if (idGenInstance == null) {
            idGenInstance = new DefaultIdGenerator(new IdGeneratorOptions((short) 1));
        }
        return idGenInstance.newLong();
    }

}
