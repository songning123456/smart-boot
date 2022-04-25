package com.sonin.ssh.enums;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * @author sonin
 * @date 2020/10/1 10:50
 */
@Slf4j
public enum ConstantEnum {

    /**
     * 核心线程大小
     */
    threadPool_corePoolSize("threadPool", "corePoolSize", 20),

    /**
     * 最大线程池大小
     */
    threadPool_maximumPoolSize("threadPool", "maximumPoolSize", 50),

    /**
     * 队列连接时长
     */
    threadPool_keepAliveTime("threadPool", "keepAliveTime", 0L),

    /**
     * 队列容量
     */
    threadPool_capacity("threadPool", "capacity", 1000),

    /**
     * 默认端口号
     */
    ssh_defaultPort("ssh", "defaultPort", 22),

    /**
     * 用户名
     */
    ssh_username("ssh", "username", "sonin"),

    /**
     * 密码
     */
    ssh_password("ssh", "password", "123456"),


    /**
     * connection超时时长
     */
    ssh_connectionTimeout("ssh", "connectionTimeout", 6000),

    /**
     * command超时时长
     */
    ssh_cmdTimeout("ssh", "cmdTimeout", 12000);


    private String type;

    private String key;

    private Object value;

    ConstantEnum(String type, String key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    private String getType() {
        return this.type;
    }

    private String getKey() {
        return this.key;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String type, String key) {
        for (ConstantEnum item : ConstantEnum.values()) {
            if (type.equals(item.getType()) && key.equals(item.getKey())) {
                return convert(item.value);
            }
        }
        return (T) "";
    }

    private static <T> T convert(Object obj) {
        T result = null;
        try {
            Class<?> clazz = obj.getClass();
            Constructor constructor = clazz.getConstructor(String.class);
            result = (T) constructor.newInstance(obj.toString());
        } catch (Exception e) {
            log.error("转换出错: {}", e.getMessage());
        }
        return result;
    }

}
