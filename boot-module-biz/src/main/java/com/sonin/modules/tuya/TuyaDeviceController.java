package com.sonin.modules.tuya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author sonin
 * @Date 2025/11/27 17:14
 */
@RestController
@RequestMapping("/tuya")
public class TuyaDeviceController {

    @Autowired
    private TuyaDeviceService deviceService;

    /**
     * 获取设备列表
     */
    @GetMapping("/devices")
    public ResponseEntity<String> getDevices() {
        try {
            String result = deviceService.getDeviceList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取设备列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取设备状态
     */
    @GetMapping("/devices/{deviceId}/status")
    public ResponseEntity<String> getDeviceStatus(@PathVariable String deviceId) {
        try {
            String result = deviceService.getDeviceStatus(deviceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取设备状态失败：" + e.getMessage());
        }
    }

    /**
     * 控制灯的开关
     */
    @PostMapping("/devices/{deviceId}/light/switch")
    public ResponseEntity<String> controlLightSwitch(
            @PathVariable String deviceId,
            @RequestParam boolean isOn) {
        try {
            String result = deviceService.controlLightSwitch(deviceId, isOn);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("控制灯开关失败：" + e.getMessage());
        }
    }

    /**
     * 调节灯的亮度
     */
    @PostMapping("/devices/{deviceId}/light/brightness")
    public ResponseEntity<String> adjustBrightness(
            @PathVariable String deviceId,
            @RequestParam int brightness) {
        try {
            String result = deviceService.adjustBrightness(deviceId, brightness);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("调节亮度失败：" + e.getMessage());
        }
    }

}
