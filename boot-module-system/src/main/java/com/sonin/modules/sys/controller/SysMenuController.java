package com.sonin.modules.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.api.vo.Result;
import com.sonin.core.query.BaseFactory;
import com.sonin.modules.sys.entity.*;
import com.sonin.modules.sys.service.SysMenuService;
import com.sonin.modules.sys.service.SysRoleMenuService;
import com.sonin.modules.sys.vo.SysMenuVO;
import com.sonin.utils.BeanExtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 用户当前用户的菜单和权限信息
     *
     * @return
     */
    @GetMapping("/myMenu")
    public Result<List<SysMenuVO>> navigationBarCtrl() {
        Result<List<SysMenuVO>> result = new Result<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .from(SysUser.class)
                    .innerJoin(SysUserRole.class, SysUserRole.class.getDeclaredField("userId"), SysUser.class.getDeclaredField("id"))
                    .innerJoin(SysRole.class, SysRole.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("roleId"))
                    .innerJoin(SysRoleMenu.class, SysRoleMenu.class.getDeclaredField("roleId"), SysRole.class.getDeclaredField("id"))
                    .innerJoin(SysMenu.class, SysMenu.class.getDeclaredField("id"), SysRoleMenu.class.getDeclaredField("menuId"))
                    .where()
                    .eq(true, "sys_user.username", authentication.getName())
                    .orderBy(true, true, "order_num")
                    .selectMaps();
            List<SysMenu> sysMenuList = BaseFactory.result().maps2Beans(mapList, SysMenu.class);
            List<SysMenuVO> sysMenuVOList = buildTree(sysMenuList);
            result.setResult(sysMenuVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return result.error500(e.getMessage());
        }
        return result;
    }

    private List<SysMenuVO> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenuVO> finalMenus = new ArrayList<>();
        List<SysMenuVO> sysMenuVOList = new ArrayList<>();
        try {
            sysMenuVOList = BeanExtUtils.beans2Beans(sysMenuList, SysMenuVO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 先各自寻找到各自的孩子
        for (SysMenuVO k : sysMenuVOList) {
            for (SysMenuVO v : sysMenuVOList) {
                if (k.getId().equals(v.getParentId())) {
                    k.getChildren().add(v);
                }
            }
            // 提取出父节点
            if (StrUtil.isBlank(k.getParentId())) {
                finalMenus.add(k);
            }
        }
        return finalMenus;
    }

    @GetMapping("/list")
    public Result<List<SysMenuVO>> listCtrl() {
        Result<List<SysMenuVO>> result = new Result<>();
        // 获取所有菜单信息
        List<SysMenu> sysMenus = sysMenuService.list(new QueryWrapper<SysMenu>().orderByAsc("order_num"));
        // 转成树状结构
        List<SysMenuVO> sysMenuVOList = buildTree(sysMenus);
        result.setResult(sysMenuVOList);
        return result;
    }

    @PostMapping("/save")
    public Result saveCtrl(@Validated @RequestBody SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result updateCtrl(@Validated @RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteCtrl(@PathVariable("id") String id) {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return Result.error("请先删除子菜单");
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
