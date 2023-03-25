package com.sonin.modules.data.controller;

import cn.hutool.http.HttpRequest;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.data.entity.DataLog;
import com.sonin.modules.data.service.DataLogService;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * <p>
 * 数据日志 前端控制器
 * </p>
 *
 * @author sonin
 * @since 2023-03-24
 */
@RestController
@RequestMapping("/gwt/data/dataLog")
public class DataLogController {

    @Autowired
    private DataLogService dataLogService;
    @Value("${boot.baseUrl}")
    private static String baseUrl;

    private static String android_url = "click_id=__CLICK_ID__&click_time=__CLICK_TIME_\n" +
            "_&campaign_id=_CAMPAIGN_ID__&adgroup_id=__ADGROUP_ID__&ad_id=__AD_I\n" +
            "D__&muid=__MUID__&hash_android_id=__HASH_ANDROID_ID__&hash_mac=__H\n" +
            "ASH_MAC__&oaid=__OAID__&hash_oaid=__HASH_OAID__&ip=__IP__&user_agent\n" +
            "=__USER_AGENT__&account_id=__ACCOUNT_ID__&promoted_object_type=__PRO\n" +
            "MOTED_OBJECT_TYPE__&device_os_type=__DEVICE_OS_TYPE__&callback=__CALLB\n" +
            "ACK__";

    private static String ios_url = "click_id=__CLICK_ID__&click_time=__CLICK_TIME_\n" +
            "_&campaign_id=_CAMPAIGN_ID__&adgroup_id=__ADGROUP_ID__&ad_id=__AD_I\n" +
            "D__&muid=__MUID__&ip=__IP__&user_agent=__USER_AGENT__&account_id=__AC\n" +
            "COUNT_ID__&promoted_object_type=__PROMOTED_OBJECT_TYPE__&device_os_t\n" +
            "ype=__DEVICE_OS_TYPE__&callback=__CALLBACK__";

    @PostMapping("/add")
    @CustomExceptionAnno(description = "数据添加")
    public Result<Object> addCtrl(@RequestBody Map<String, Object> paramsMap,HttpServletRequest request) throws Exception {
        DataLog dataLog = BeanExtUtils.map2Bean(paramsMap, DataLog.class);
        dataLog.setDataSourceIp(String.join(",", IpUtils.getLocalIPList()));
        dataLogService.save(dataLog);
        // todo url待修改
        /*String queryResultStr = HttpUtils.doPost(thirdUrl + "/appinterface/api/external/uploadCopyData", null, new LinkedHashMap<String, Object>() {{
            put("appId", appId);
        }});*/
        return Result.ok();
    }
    @GetMapping("/makeUrl")
    @CustomExceptionAnno(description = "数据添加")
    public Result<Object> makeUrl(String deviceType, String advertisingId, String operationType ,HttpServletRequest request) throws Exception {
        String url = change(deviceType, "1", operationType);
        // todo url待修改
        return Result.ok(url);
    }

    /**
     * <pre>
     * <请输入描述信息>
     * </pre>
     *
     * @param deviceType    设备类型 ios ,android
     * @param advertisingId 广告id
     * @param operationType 用户操作类型 0:展示 1 点击
     * @author Gao Ran,
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */

    public static String change(String deviceType, String advertisingId, String operationType) {

        String appendUrl = "/gwt/data/dataLog/add?";
        if (StringUtils.isBlank(deviceType)) {
            deviceType = "android";
        }
        if (StringUtils.isBlank(operationType)) {
            operationType = "1";
        }
        String requestUrl = "";

        if ("android".equals(deviceType)) {
            requestUrl = baseUrl + appendUrl+android_url;

        } else if ("ios".equals(deviceType)) {
            requestUrl = baseUrl + appendUrl+ios_url;
        }


        return requestUrl;
    }


    public static void main(String[] args) throws UnknownHostException {
       // System.out.println(getUrl());
        System.out.println(change("","111","0"));
    }
    public static String getUrl(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String localAddr = request.getLocalAddr();
        int serverPort = request.getServerPort();
        return "http://"+localAddr +":"+ serverPort;
    }

}
