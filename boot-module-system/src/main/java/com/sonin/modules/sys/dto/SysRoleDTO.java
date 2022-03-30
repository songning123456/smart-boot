package com.sonin.modules.sys.dto;

import com.sonin.modules.sys.entity.SysRole;
import lombok.Data;

/**
 * @author sonin
 * @date 2022/3/27 15:33
 */
@Data
public class SysRoleDTO extends SysRole {

    private String roleIds;

    private Integer currentPage = 1;

    private Integer pageSize = 10;

}
