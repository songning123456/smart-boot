package com.sonin.utils;

import java.io.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 17:05
 */
public class CloneUtils implements Serializable {

    /**
     * <pre>
     * 对象深拷贝
     * </pre>
     *
     * @param object
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Object deepClone(Object object) throws Exception {
        // 将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(object);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (oi.readObject());
    }

}
