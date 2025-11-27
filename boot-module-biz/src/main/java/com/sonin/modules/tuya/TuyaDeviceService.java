package com.sonin.modules.tuya;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/11/27 17:13
 */
@Service
public class TuyaDeviceService {

    @Autowired
    private TuyaHttpClient httpClient;

    /**
     * 获取设备列表
     */
    public String getDeviceList() throws Exception {
        // 分页获取设备列表，page_size 最大为 50
        return httpClient.doGet("/v1.0/devices?page_no=1&page_size=50");
    }

    /**
     * 获取设备状态
     *
     * @param deviceId 设备 ID
     */
    public String getDeviceStatus(String deviceId) throws Exception {
        return httpClient.doGet("/v1.0/devices/" + deviceId + "/status");
    }

    /**
     * 下发设备控制指令
     *
     * @param deviceId 设备 ID
     * @param commands 控制指令
     */
    public String sendCommand(String deviceId, List<Map<String, Object>> commands) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("commands", commands);
        String body = JSON.toJSONString(requestBody);
        return httpClient.doPost("/v1.0/devices/" + deviceId + "/commands", body);
    }

    /**
     * 开关控制（示例：控制灯的开关）
     */
    public String controlLightSwitch(String deviceId, boolean isOn) throws Exception {
        List<Map<String, Object>> commands = new ArrayList<>();
        Map<String, Object> command = new HashMap<>();
        command.put("code", "switch_led"); // 功能点 code，需根据设备实际情况修改
        command.put("value", isOn);
        commands.add(command);
        return sendCommand(deviceId, commands);
    }

    /**
     * 调节亮度（示例：调节灯的亮度）
     */
    public String adjustBrightness(String deviceId, int brightness) throws Exception {
        List<Map<String, Object>> commands = new ArrayList<>();
        Map<String, Object> command = new HashMap<>();
        command.put("code", "bright_value"); // 功能点 code，需根据设备实际情况修改
        command.put("value", brightness); // 亮度值范围：1-1000
        commands.add(command);
        return sendCommand(deviceId, commands);
    }

}
