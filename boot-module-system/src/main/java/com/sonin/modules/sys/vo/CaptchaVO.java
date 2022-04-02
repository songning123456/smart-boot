package com.sonin.modules.sys.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author sonin
 * @date 2022/4/2 9:43
 */
@Data
@Builder
public class CaptchaVO {

    private String token;

    private String captchaImg;

}
