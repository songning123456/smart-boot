package com.sonin.modules.websocket.entity;

import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 13:45
 */
@Data
public class Websocket {

    /**
     * <pre>
     * 用户名
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: 如果是多个username, 以 ',' 分割
     */
    private String username = "";

    /**
     * <pre>
     * uuid
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String uuid = "";

    /**
     * <pre>
     * 时间戳
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String time = "";

    /**
     * <pre>
     * 组件
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String component = "";

    /**
     * <pre>
     * me: 推送给我；some: 推送给指定username；all: 所有人
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String pushType = "me";

    /**
     * <pre>
     * 响应的数据
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String resData = "";

    public Websocket() {

    }

    public Websocket(String username, String uuid, String time, String component, String pushType, String resData) {
        this.username = username;
        this.uuid = uuid;
        this.time = time;
        this.component = component;
        this.pushType = pushType;
        this.resData = resData;
    }

}
