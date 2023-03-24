package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.core.vo.Result;
import com.sonin.modules.sys.dto.SysRoleDTO;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysRoleMenu;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysRoleMenuService;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.modules.sys.service.SysUserRoleService;
import com.sonin.modules.sys.vo.SysRoleVO;
import com.sonin.utils.BeanExtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private TransactionTemplate transactionTemplate;

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

    @GetMapping("/page")
    public Result<Page<SysRole>> pageCtrl(SysRoleDTO sysRoleDTO) {
        Result<Page<SysRole>> result = new Result<>();
        String name = sysRoleDTO.getName();
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>()
                .like(StringUtils.isNotEmpty(name), "name", name)
                .orderByDesc("update_time");
        Page<SysRole> sysRolePage = sysRoleService.page(new Page<>(sysRoleDTO.getCurrentPage(), sysRoleDTO.getPageSize()), queryWrapper);
        result.setResult(sysRolePage);
        return result;
    }

    @GetMapping("/list")
    public Result<List<SysRole>> listCtrl(SysRoleDTO sysRoleDTO) {
        Result<List<SysRole>> result = new Result<>();
        String name = sysRoleDTO.getName();
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>()
                .like(StringUtils.isNotEmpty(name), "name", name)
                .orderByDesc("update_time");
        List<SysRole> sysRoleList = sysRoleService.list(queryWrapper);
        result.setResult(sysRoleList);
        return result;
    }

    @PostMapping("/save")
    public Result saveCtrl(@Validated @RequestBody SysRole sysRole) {
        sysRoleService.save(sysRole);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result updateCtrl(@Validated @RequestBody SysRole sysRole) {
        sysRoleService.updateById(sysRole);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    public Result deleteCtrl(@RequestParam(name = "ids") String ids) {
        List<String> roleIdList = Arrays.asList(ids.split(","));
        transactionTemplate.execute(transactionStatus -> {
            sysRoleService.removeByIds(roleIdList);
            // 删除中间表
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", roleIdList));
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", roleIdList));
            return 1;
        });
        return Result.ok();
    }

    @PostMapping("/menu/{roleId}")
    public Result menuCtrl(@PathVariable("roleId") String roleId, @RequestBody String[] menuIds) {
        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            sysRoleMenuList.add(roleMenu);
        });
        transactionTemplate.execute(transactionStatus -> {
            // 先删除原来的记录，再保存新的
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
            sysRoleMenuService.saveBatch(sysRoleMenuList);
            return 1;
        });
        return Result.ok();
    }

}
