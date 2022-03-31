package com.sonin.modules.sys.dto;

import com.sonin.modules.sys.entity.SysUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sonin
 * @date 2022/3/26 9:49
 */
@Data
public class SysUserDTO extends SysUser {

    private String oldPassword;

    private String newPassword;

    private Integer pageNo = 1;

    private Integer pageSize = 10;

}
