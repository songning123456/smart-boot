package com.sonin.modules.sys.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author sonin
 * @date 2022/3/25 10:00
 */
@Data
public class PasswordDTO implements Serializable {

    @NotBlank(message = "新密码不能为空")
    private String password;

    @NotBlank(message = "旧密码不能为空")
    private String currentPwd;

}
