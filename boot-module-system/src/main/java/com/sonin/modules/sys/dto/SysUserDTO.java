package com.sonin.modules.sys.dto;

import lombok.Data;

/**
 * @author sonin
 * @date 2022/3/26 9:49
 */
@Data
public class SysUserDTO {

    private String username;

    private Integer pageNo = 1;

    private Integer pageSize = 10;

}
