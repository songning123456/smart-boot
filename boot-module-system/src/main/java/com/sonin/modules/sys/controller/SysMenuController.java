package com.sonin.modules.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.api.vo.Result;
import com.sonin.core.query.BaseFactory;
import com.sonin.modules.sys.entity.SysMenu;
import com.sonin.modules.sys.entity.SysRoleMenu;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysMenuService;
import com.sonin.modules.sys.service.SysRoleMenuService;
import com.sonin.modules.sys.service.SysUserService;
import com.sonin.modules.sys.vo.SysMenuVO;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
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
@RequestMapping("/sys/sysMenu")
public class SysMenuController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户当前用户的菜单和权限信息
     *
     * @param principal
     * @return
     */
    @GetMapping("/nav")
    public Result<Object> navCtrl(Principal principal) {
        Result<Object> result = new Result<>();
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", principal.getName()));
        // 获取权限信息
        // ROLE_admin,ROLE_normal,sys:user:list,....
        String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());
        String[] authorities = StringUtils.tokenizeToStringArray(authorityInfo, ",");
        // 获取导航栏信息
        List<SysMenuVO> navs = getCurrentUserNav();
        Map<String, Object> resMap = new HashMap<String, Object>() {{
            put("authorities", authorities);
            put("navs", navs);
        }};
        result.setResult(resMap);
        return result;
    }

    private List<SysMenuVO> getCurrentUserNav() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        List<String> menuIds = new ArrayList<>();
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .select("DISTINCT sys_role_menu.menu_id as menuId")
                    .from(SysUserRole.class)
                    .innerJoin(SysRoleMenu.class, SysUserRole.class.getDeclaredField("roleId"), SysRoleMenu.class.getDeclaredField("roleId"))
                    .where()
                    .eq(true, "sys_user_role.user_id", sysUser.getId())
                    .selectMaps();
            menuIds = mapList.stream().map(item -> "" + item.get("menuId")).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SysMenu> sysMenuList = sysMenuService.listByIds(menuIds);
        // 转树状结构
        return buildTreeMenu(sysMenuList);
    }

    private List<SysMenuVO> buildTreeMenu(List<SysMenu> sysMenuList) {
        List<SysMenuVO> finalMenus = new ArrayList<>();
        // 先各自寻找到各自的孩子
        for (SysMenu k : sysMenuList) {
            for (SysMenu v : sysMenuList) {
                if (k.getId().equals(v.getParentId())) {
                    k.getChildren().add(v);
                }
            }
            // 提取出父节点
            if (StrUtil.isBlank(k.getParentId())) {
                try {
                    SysMenuVO sysMenuVO = BeanExtUtils.bean2Bean(k, SysMenuVO.class);
                    finalMenus.add(sysMenuVO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return finalMenus;
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<Object> infoCtrl(@PathVariable(name = "id") Long id) {
        Result<Object> result = new Result<>();
        result.setResult(sysMenuService.getById(id));
        return result;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<List<SysMenuVO>> listCtrl() {
        Result<List<SysMenuVO>> result = new Result<>();
        // 获取所有菜单信息
        List<SysMenu> sysMenus = sysMenuService.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        // 转成树状结构
        List<SysMenuVO> sysMenuVOList = buildTreeMenu(sysMenus);
        result.setResult(sysMenuVOList);
        return result;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result<Object> saveCtrl(@Validated @RequestBody SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return Result.ok();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result<Object> update(@Validated @RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        // 清除所有与该菜单相关的权限缓存
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .from(SysUserRole.class)
                    .innerJoin(SysRoleMenu.class, SysRoleMenu.class.getDeclaredField("roleId"), SysRoleMenu.class.getDeclaredField("roleId"))
                    .innerJoin(SysUser.class, SysUser.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("userId"))
                    .where()
                    .eq(true, "sys_role_menu.menu_id", sysMenu.getId())
                    .selectMaps();
            mapList.forEach(item -> {
                redisUtil.del("GrantedAuthority:" + item.get("SysUser_username"));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") Long id) {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return Result.error("请先删除子菜单");
        }
        // 清除所有与该菜单相关的权限缓存
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .from(SysUserRole.class)
                    .innerJoin(SysRoleMenu.class, SysRoleMenu.class.getDeclaredField("roleId"), SysRoleMenu.class.getDeclaredField("roleId"))
                    .innerJoin(SysUser.class, SysUser.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("userId"))
                    .where()
                    .eq(true, "sys_role_menu.menu_id", id)
                    .selectMaps();
            mapList.forEach(item -> {
                redisUtil.del("GrantedAuthority:" + item.get("SysUser_username"));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 同步删除中间关联表
        transactionTemplate.execute((transactionStatus -> {
            sysMenuService.removeById(id);
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));
            return 1;
        }));
        return Result.ok();
    }

}
