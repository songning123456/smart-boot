package com.sonin.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.query.BaseFactory;
import com.sonin.modules.sys.entity.*;
import com.sonin.modules.sys.mapper.SysUserMapper;
import com.sonin.modules.sys.service.SysMenuService;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sonin
 * @since 2022-03-25
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getUserAuthorityInfo(Long userId) {

        SysUser sysUser = sysUserMapper.selectById(userId);
        //  ROLE_admin,ROLE_normal,sys:user:list,....
        String authority = "";
        if (redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername())) {
            authority = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());
        } else {
            // 获取角色编码
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + userId));
            if (roles.size() > 0) {
                String roleCodes = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");
            }
            // 获取菜单操作编码
            List<Long> menuIds = new ArrayList<>();
            try {
                List<Map<String, Object>> menuIdMapList = BaseFactory.join()
                        .select("sys_role_menu.menu_id as menuId")
                        .from(SysUserRole.class)
                        .innerJoin(SysRoleMenu.class, SysRoleMenu.class.getDeclaredField("roleId"), SysUserRole.class.getDeclaredField("roleId"))
                        .where()
                        .selectMaps();
                menuIds = menuIdMapList.stream().map(item -> Long.parseLong("" + item.get("menuId"))).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (menuIds.size() > 0) {
                List<SysMenu> menus = sysMenuService.listByIds(menuIds);
                String menuPerms = menus.stream().map(SysMenu::getPermission).collect(Collectors.joining(","));
                authority = authority.concat(menuPerms);
            }
            redisUtil.set("GrantedAuthority:" + sysUser.getUsername(), authority, 60 * 60);
        }
        return authority;
    }

}
