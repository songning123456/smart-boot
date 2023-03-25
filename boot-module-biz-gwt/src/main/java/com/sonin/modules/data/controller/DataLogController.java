package com.sonin.modules.data.controller;

import cn.hutool.http.HttpRequest;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.data.entity.DataLog;
import com.sonin.modules.data.service.DataLogService;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.HttpUtils;
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
import java.util.LinkedHashMap;
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
    private String baseUrl;

    private static String android_url = "click_id=__CLICK_ID__&click_time=__CLICK_TIME_" +
            "_&campaign_id=_CAMPAIGN_ID__&adgroup_id=__ADGROUP_ID__&ad_id=__AD_I" +
            "D__&muid=__MUID__&hash_android_id=__HASH_ANDROID_ID__&hash_mac=__H" +
            "ASH_MAC__&oaid=__OAID__&hash_oaid=__HASH_OAID__&ip=__IP__&user_agent" +
            "=__USER_AGENT__&account_id=__ACCOUNT_ID__&promoted_object_type=__PRO" +
            "MOTED_OBJECT_TYPE__&device_os_type=__DEVICE_OS_TYPE__&callback=__CALLB" +
            "ACK__";

    private static String ios_url = "click_id=__CLICK_ID__&click_time=__CLICK_TIME_" +
            "_&campaign_id=_CAMPAIGN_ID__&adgroup_id=__ADGROUP_ID__&ad_id=__AD_I" +
            "D__&muid=__MUID__&ip=__IP__&user_agent=__USER_AGENT__&account_id=__AC" +
            "COUNT_ID__&promoted_object_type=__PROMOTED_OBJECT_TYPE__&device_os_t" +
            "ype=__DEVICE_OS_TYPE__&callback=__CALLBACK__";
    //大航海回调url
    private static String thirdUrl = "https://wcp.taobao.com/adstrack/track.json";

    @PostMapping("/add")
    @CustomExceptionAnno(description = "数据添加")
    public Result<Object> addCtrl(@RequestBody Map<String, Object> paramsMap, HttpServletRequest request) throws Exception {
        DataLog dataLog = BeanExtUtils.map2Bean(paramsMap, DataLog.class);
        dataLog.setDataSourceIp(String.join(",", IpUtils.getLocalIPList()));
        dataLogService.save(dataLog);
        // todo url待修改
        //"https://wcp.taobao.com/adstrack/track.json?action=2&channel=渠道" +
        //            "id&advertisingSpaceId=广告位 id&taskId =任务 id&adid=投放计划 id&cid=投放创意" +
        //            "id&imeiMd5=设备号 Md5&app=1&dpaId=动态素材库 ID&dpaType=动态素材库类型" +
        //            "&callbackUrl=回调 URL";
//channel String 是 大航海渠道 ID
//advertisingSpaceId String 是 大航海广告位 ID
//taskId String 是 大航海的任务 ID
//adAgent String 建议传 代理商
//adGroupId String 建议传 广告组 ID
//adname String 建议传 广告计划名
//adid String 建议传 渠道侧投放计划 id, 用来追踪投放计划转
//化效果
//cid String 建议传 渠道侧投放创意 id， 用来追踪投放创意转化效果
        //imei String 否，蓝色六选一 imei 的原生值
        /*String queryResultStr = HttpUtils.doPost(thirdUrl + "/appinterface/api/external/uploadCopyData", null, new LinkedHashMap<String, Object>() {{
            put("appId", appId);
        }});*/
        String channel = request.getParameter("");
        String advertisingSpaceId = request.getParameter("");
        String taskId = request.getParameter("");
        String adAgent = request.getParameter("");
        String adGroupId = request.getParameter("");
        String adname = request.getParameter("");
        String adid = request.getParameter("");
        String cid = request.getParameter("");
        String imei = request.getParameter("");
        String queryResultStr = HttpUtils.doPost(thirdUrl, null, new LinkedHashMap<String, Object>() {{
            put("channel", channel);
            put("advertisingSpaceId", advertisingSpaceId);
            put("taskId", taskId);
            put("adAgent", adAgent);
            put("adGroupId", adGroupId);
            put("adname", adname);
            put("adid", adid);
            put("cid", cid);
            put("imei", imei);
            put("action", 2);


        }});
        System.out.println(paramsMap);
        System.out.println(request.getParameterNames());
        return Result.ok();
    }

    @GetMapping("/makeUrl")
    @CustomExceptionAnno(description = "数据添加")
    public Result<Object> makeUrl(String deviceType, String advertisingId, String operationType, HttpServletRequest request) throws Exception {
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

    public String change(String deviceType, String advertisingId, String operationType) {

        String appendUrl = "/gwt/data/dataLog/add?";
        if (StringUtils.isBlank(deviceType)) {
            deviceType = "android";
        }
        if (StringUtils.isBlank(operationType)) {
            operationType = "1";
        }
        String requestUrl = "";

        if ("android".equals(deviceType)) {
            requestUrl = baseUrl + appendUrl + android_url;

        } else if ("ios".equals(deviceType)) {
            requestUrl = baseUrl + appendUrl + ios_url;
        }


        return requestUrl;
    }


    public String getUrl() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String localAddr = request.getLocalAddr();
        int serverPort = request.getServerPort();
        return "http://" + localAddr + ":" + serverPort;
    }

}
