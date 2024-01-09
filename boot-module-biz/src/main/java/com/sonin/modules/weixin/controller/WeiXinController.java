package com.sonin.modules.weixin.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.weixin.constant.WeiXinConstant;
import com.sonin.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2024/1/9 13:44
 */
@Slf4j
@RestController
@RequestMapping("/weixin")
public class WeiXinController {

    @PostMapping(value = "/sendTemplateMessage")
    @CustomExceptionAnno(description = "发送模板消息")
    public Result<Object> sendTemplateMessageCtrl(@RequestParam String templateId, @RequestParam(defaultValue = "") String openId, @RequestBody JSONObject paramsMap) {
        Result<Object> result = new Result<>();
        if (StringUtils.isEmpty(templateId)) {
            result.error500("templateId为空");
        } else {
            JSONObject srcJsonObj = paramsMap.getJSONObject("data");
            JSONObject targetJsonObj = new JSONObject();
            for (String key : srcJsonObj.keySet()) {
                targetJsonObj.put(key, new HashMap<String, String>() {{
                    put("value", srcJsonObj.getJSONObject(key).getString("value"));
                }});
            }
            result.setResult(sendTemplateMessageFunc(templateId, openId, targetJsonObj));
        }
        return result;
    }

    private String sendTemplateMessageFunc(String templateTd, String openId, JSONObject data) {
        // 0. 获取token
        Map<String, String> paramMap0 = new LinkedHashMap<String, String>() {{
            put("grant_type", "client_credential");
            put("appid", "wx0112041d08ab0771");
            put("secret", "068ec61a35337ad5cf2c41140f5da036");
        }};
        StringBuilder paramSB0 = new StringBuilder();
        for (Map.Entry<String, String> entry : paramMap0.entrySet()) {
            paramSB0.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        String url0 = WeiXinConstant.GET_TOKEN + paramSB0.toString().replaceFirst("&", "?");
        log.info("获取 token URL: {}", url0);
        String resStr0 = HttpUtil.get(url0);
        log.info("获取 token 结果: {}", resStr0);
        String accessToken = JSONObject.parseObject(resStr0).getString("access_token");
        String finalOpenId;
        // 未传入openId, 则群发所有人
        if (StringUtils.isEmpty(openId)) {
            // 1. 获取微信公众号关注用户，最多1w条，超过则分页请求
            String url1 = WeiXinConstant.GET_USER + "?access_token=" + accessToken;
            log.info("获取 微信公众号关注用户 URL: {}", url1);
            String resStr1 = HttpUtil.get(url1);
            log.info("获取 微信公众号关注用户 结果: {}", resStr1);
            JSONArray openIdArray = JSONObject.parseObject(resStr1).getJSONObject("data").getJSONArray("openid");
            StringBuilder openIdSB = new StringBuilder();
            for (Object item : openIdArray) {
                openIdSB.append(",").append(item);
            }
            finalOpenId = openIdSB.toString().replaceFirst(",", "");
        } else {
            // 发送给指定人
            finalOpenId = openId;
        }
        log.info("当前openId: {}", openId);
        // 2. 模板消息推送(无小程序)
        Map<String, Object> paramsMap2 = new LinkedHashMap<>();
        paramsMap2.put("touser", finalOpenId);
        paramsMap2.put("template_id", templateTd);
        paramsMap2.put("data", data);
        String url2 = WeiXinConstant.POST_TEMPLATE_SEND + "?access_token=" + accessToken;
        log.info("获取 模板消息推送 URL: {}", url2);
        String paramsStr2 = JSONObject.toJSONString(paramsMap2);
        log.info("获取 模板消息推送 参数: {}", paramsStr2);
        return HttpUtils.doPost(url2, paramsMap2);
    }

}
