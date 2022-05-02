package com.sonin.modules.websocket.dto;

import com.sonin.modules.websocket.entity.Websocket;
import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 13:48
 */
@Data
public class WebsocketDTO extends Websocket {

    /**
     * <pre>
     * 请求的数据
     * </pre>
     *
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private String reqData = "";

}
