package com.sonin.modules.weixin.constant;

/**
 * <pre>
 * 微信接口信息
 * </pre>
 *
 * @author sonin
 * @version 1.0 2024/1/9 13:39
 */
public interface WeiXinConstant {

    // 获取普通access_token接口
    String GET_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    // 获取微信公众号关注用户
    String GET_USER = "https://api.weixin.qq.com/cgi-bin/user/get";

    // 模板消息推送
    String POST_TEMPLATE_SEND = "https://api.weixin.qq.com/cgi-bin/message/template/send";

}
