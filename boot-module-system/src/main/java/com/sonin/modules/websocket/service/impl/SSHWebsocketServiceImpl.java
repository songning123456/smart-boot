package com.sonin.modules.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sonin.jedis.template.JedisTemplate;
import com.sonin.modules.websocket.entity.Websocket;
import com.sonin.ssh.command.SshTemplate;
import com.sonin.ssh.pojo.SshResult;
import com.sonin.utils.CryptoUtils;
import com.sonin.utils.JwtUtil;
import com.sonin.websocket.service.IWebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * Websocket SSH实现
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/2 14:07
 */
@Service("SSH")
public class SSHWebsocketServiceImpl implements IWebsocketService {

    @Value("${boot.encryption.crypto.secretKey:}")
    private String secretKey;
    @Autowired
    private SshTemplate sshTemplate;
    @Autowired
    private JedisTemplate jedisTemplate;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void handle(JSONObject jsonObject) {
        SshResult sshResult;
        try {
            // 密码解密
            String remotePassword = CryptoUtils.aesDecrypt(jsonObject.getString("remotePassword"), secretKey);
            sshResult = sshTemplate.execute(jsonObject.getString("remoteIp"), jsonObject.getInteger("remotePort"), jsonObject.getString("remoteUsername"), remotePassword, sshSession -> sshSession.executeCommand(jsonObject.getString("command")));
        } catch (Exception e) {
            e.printStackTrace();
            sshResult = new SshResult(e);
        }
        // 设置用户名
        String username = jwtUtil.getClaimByToken(jsonObject.getString("token")).getSubject();
        Websocket websocket = new Websocket(username, jsonObject.getString("uuid"), jsonObject.getString("time"), "SSH", "me", sshResult.getResult());
        jedisTemplate.publish("websocket", JSON.toJSONString(websocket));
    }

}
