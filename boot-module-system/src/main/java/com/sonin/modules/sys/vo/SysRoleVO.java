package com.sonin.modules.sys.vo;

import com.sonin.modules.sys.entity.SysRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sonin
 * @date 2022/3/30 13:38
 */
@Data
public class SysRoleVO extends SysRole {

    private List<String> menuIds = new ArrayList<>();

}
