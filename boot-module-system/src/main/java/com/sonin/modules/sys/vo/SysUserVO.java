package com.sonin.modules.sys.vo;

import com.sonin.modules.sys.entity.SysUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sonin
 * @date 2022/3/27 10:34
 */
@Data
public class SysUserVO extends SysUser {

    private List<String> roleIds = new ArrayList<>();

}
