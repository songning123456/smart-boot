package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysRoleMenu;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysRoleMenuService;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.modules.sys.service.SysUserRoleService;
import com.sonin.modules.sys.service.SysUserService;
import com.sonin.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sonin
 * @since 2022-03-25
 */
@RestController
@RequestMapping("/sys/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result<Object> infoCtrl(@PathVariable("id") Long id) {
        Result<Object> result = new Result<>();
        SysRole sysRole = sysRoleService.getById(id);
        // 获取角色相关联的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<String> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        sysRole.setMenuIds(menuIds);
        result.setResult(sysRole);
        return result;
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result<Object> listCtrl(String name,
                                   @RequestParam(defaultValue = "1") Integer pageNo,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Result<Object> result = new Result<>();
        Page<SysRole> pageData = sysRoleService.page(new Page<>(pageNo, pageSize), new QueryWrapper<SysRole>().like(StringUtils.isNotEmpty(name), "name", name));
        result.setResult(pageData);
        return result;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result<Object> save(@Validated @RequestBody SysRole sysRole) {
        sysRoleService.save(sysRole);
        return Result.ok();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result<Object> updateCtrl(@Validated @RequestBody SysRole sysRole) {
        Result<Object> result = new Result<>();
        sysRoleService.updateById(sysRole);
        // 更新缓存
        List<SysUser> sysUsers = sysUserService.list(new QueryWrapper<SysUser>().inSql("id", "select user_id from sys_user_role where role_id = " + sysRole.getId()));
        sysUsers.forEach(item -> {
            redisUtil.del("GrantedAuthority:" + item.getUsername());
        });
        result.setResult(sysRole);
        return result;
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result<Object> deleteCtrl(@RequestBody Long[] ids) {
        transactionTemplate.execute(transactionStatus -> {
            sysRoleService.removeByIds(Arrays.asList(ids));
            // 删除中间表
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", Arrays.asList(ids)));
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", Arrays.asList(ids)));
            return 1;
        });
        // 缓存同步删除
        Arrays.stream(ids).forEach(id -> {
            // 更新缓存
            List<SysUser> sysUsers = sysUserService.list(new QueryWrapper<SysUser>().inSql("id", "select user_id from sys_user_role where role_id = " + id));
            sysUsers.forEach(item -> {
                redisUtil.del("GrantedAuthority:" + item.getUsername());
            });
        });
        return Result.ok();
    }

    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result<Object> info(@PathVariable("roleId") String roleId, @RequestBody String[] menuIds) {
        Result<Object> result = new Result<>();
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            sysRoleMenus.add(roleMenu);
        });
        transactionTemplate.execute(transactionStatus -> {
            // 先删除原来的记录，再保存新的
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
            sysRoleMenuService.saveBatch(sysRoleMenus);
            return 1;
        });
        // 删除缓存
        List<SysUser> sysUsers = sysUserService.list(new QueryWrapper<SysUser>().inSql("id", "select user_id from sys_user_role where role_id = " + roleId));
        sysUsers.forEach(item -> {
            redisUtil.del("GrantedAuthority:" + item.getUsername());
        });
        result.setResult(menuIds);
        return result;
    }

}
