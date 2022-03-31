package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.core.query.BaseFactory;
import com.sonin.modules.sys.dto.SysRoleDTO;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysRoleMenu;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysRoleMenuService;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.modules.sys.service.SysUserRoleService;
import com.sonin.modules.sys.service.SysUserService;
import com.sonin.modules.sys.vo.SysRoleVO;
import com.sonin.utils.BeanExtUtils;
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
import java.util.Map;
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
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result<SysRoleVO> infoCtrl(@PathVariable("id") String id) throws Exception {
        Result<SysRoleVO> result = new Result<>();
        SysRole sysRole = sysRoleService.getById(id);
        SysRoleVO sysRoleVO = BeanExtUtils.bean2Bean(sysRole, SysRoleVO.class);
        // 获取角色相关联的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<String> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        sysRoleVO.setMenuIds(menuIds);
        result.setResult(sysRoleVO);
        return result;
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result<Page<SysRole>> listCtrl(SysRoleDTO sysRoleDTO) {
        Result<Page<SysRole>> result = new Result<>();
        String name = sysRoleDTO.getName();
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>()
                .like(StringUtils.isNotEmpty(name), "name", name)
                .orderByDesc("update_time");
        Page<SysRole> sysRolePage = sysRoleService.page(new Page<>(sysRoleDTO.getCurrentPage(), sysRoleDTO.getPageSize()), queryWrapper);
        result.setResult(sysRolePage);
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
        sysRoleService.updateById(sysRole);
        // 缓存同步删除
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .select("sys_user.username")
                    .from(SysUser.class)
                    .innerJoin(SysUserRole.class, SysUserRole.class.getDeclaredField("userId"), SysUser.class.getDeclaredField("id"))
                    .innerJoin(SysRole.class, SysRole.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("roleId"))
                    .where()
                    .eq(true, "sys_role.id", sysRole.getId())
                    .selectMaps();
            List<String> usernameList = mapList.stream().map(item -> "" + item.get("username")).collect(Collectors.toList());
            usernameList.forEach(username -> {
                redisUtil.del("GrantedAuthority:" + username);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result<Object> deleteCtrl(@RequestParam(name = "roleIds") String roleIds) {
        // 缓存同步删除
        List<String> roleIdList = Arrays.asList(roleIds.split(","));
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .select("sys_user.username")
                    .from(SysUser.class)
                    .innerJoin(SysUserRole.class, SysUserRole.class.getDeclaredField("userId"), SysUser.class.getDeclaredField("id"))
                    .innerJoin(SysRole.class, SysRole.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("roleId"))
                    .where()
                    .in(true, "sys_role.id", roleIdList)
                    .selectMaps();
            List<String> usernameList = mapList.stream().map(item -> "" + item.get("username")).collect(Collectors.toList());
            usernameList.forEach(username -> {
                redisUtil.del("GrantedAuthority:" + username);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        transactionTemplate.execute(transactionStatus -> {
            sysRoleService.removeByIds(roleIdList);
            // 删除中间表
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", roleIdList));
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", roleIdList));
            return 1;
        });
        return Result.ok();
    }

    @PostMapping("/permission/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result<Object> info(@PathVariable("roleId") String roleId, @RequestBody String[] menuIds) {
        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            sysRoleMenuList.add(roleMenu);
        });
        // 删除缓存
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .select("sys_user.username")
                    .from(SysUserRole.class)
                    .innerJoin(SysUser.class, SysUser.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("userId"))
                    .where()
                    .eq(true, "sys_user_role.role_id", roleId)
                    .selectMaps();
            mapList.forEach(item -> {
                redisUtil.del("GrantedAuthority:" + item.get("username"));
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        transactionTemplate.execute(transactionStatus -> {
            // 先删除原来的记录，再保存新的
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
            sysRoleMenuService.saveBatch(sysRoleMenuList);
            return 1;
        });
        return Result.ok();
    }

}
