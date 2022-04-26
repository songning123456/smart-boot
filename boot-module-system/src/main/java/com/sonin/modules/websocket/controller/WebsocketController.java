package com.sonin.modules.websocket.controller;

import com.alibaba.fastjson.JSON;
import com.sonin.api.vo.Result;
import com.sonin.jedis.template.JedisTemplate;
import com.sonin.modules.websocket.dto.WebsocketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/26 13:46
 */
@RestController
@RequestMapping("/websocket")
public class WebsocketController {

    @Autowired
    private JedisTemplate jedisTemplate;

    @GetMapping("/send")
    public Result<Object> sendCtrl(WebsocketDTO websocketDTO) {
        // 可以结合具体的业务逻辑设置data值
        websocketDTO.setData("处理的结果: " + websocketDTO.getData());
        String message = JSON.toJSONString(websocketDTO);
        // jedis发布消息
        jedisTemplate.publish("websocket", message);
        return Result.ok();
    }

}
