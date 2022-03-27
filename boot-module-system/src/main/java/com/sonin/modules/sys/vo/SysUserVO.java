package com.sonin.modules.sys.vo;

import com.sonin.modules.sys.entity.SysRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sonin
 * @date 2022/3/27 10:34
 */
@Data
public class SysUserVO {

    private String id;

    private String username;

    private String password;

    private String avatar;

    private String email;

    private String city;

    private Date createTime;

    private Date updateTime;

    private Date lastLogin;

    private List<SysRole> sysRoles = new ArrayList<>();

}
