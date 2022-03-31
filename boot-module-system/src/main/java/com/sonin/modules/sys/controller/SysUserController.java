package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.core.query.BaseFactory;
import com.sonin.modules.sys.dto.SysUserDTO;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysUserRoleService;
import com.sonin.modules.sys.service.SysUserService;
import com.sonin.modules.sys.vo.SysUserVO;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
@RequestMapping("/sys/sysUser")
public class SysUserController {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户信息接口
     *
     * @return
     */
    @GetMapping("/myInfo")
    public Result<SysUserVO> myInfoCtrl() throws Exception {
        Result<SysUserVO> result = new Result<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authentication.getName()));
        SysUserVO sysUserVO = BeanExtUtils.bean2Bean(sysUser, SysUserVO.class);
        result.setResult(sysUserVO);
        return result;
    }

    @GetMapping("/info/{id}")
    public Result<SysUserVO> infoCtrl(@PathVariable("id") String id) {
        Result<SysUserVO> result = new Result<>();
        SysUserVO sysUserVO = new SysUserVO();
        try {
            List<Map<String, Object>> mapList = BaseFactory.join()
                    .from(SysUser.class)
                    .innerJoin(SysUserRole.class, SysUserRole.class.getDeclaredField("userId"), SysUser.class.getDeclaredField("id"))
                    .innerJoin(SysRole.class, SysRole.class.getDeclaredField("id"), SysUserRole.class.getDeclaredField("roleId"))
                    .where()
                    .eq(true, "sys_user.id", id)
                    .selectMaps();
            List<SysRole> sysRoleList = BaseFactory.result().maps2Beans(mapList, SysRole.class);
            List<String> roleIds = sysRoleList.stream().map(SysRole::getId).collect(Collectors.toList());
            sysUserVO.setRoleIds(roleIds);
        } catch (Exception e) {
            e.printStackTrace();
            return result.error500(e.getMessage());
        }
        result.setResult(sysUserVO);
        return result;
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<Page<SysUser>> pageCtrl(SysUserDTO sysUserDTO) throws Exception {
        Result<Page<SysUser>> result = new Result<>();
        String username = sysUserDTO.getUsername();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>()
                .like(StringUtils.isNotEmpty(username), "username", username)
                .orderByDesc("update_time");
        Page<SysUser> sysUserPage = sysUserService.page(new Page<>(sysUserDTO.getPageNo(), sysUserDTO.getPageSize()), queryWrapper);
        result.setResult(sysUserPage);
        return result;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result saveCtrl(@Validated @RequestBody SysUser sysUser) {
        // 默认密码
        String password = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setPassword(password);
        sysUserService.save(sysUser);
        return Result.ok();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@Validated @RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@RequestParam(name = "ids") String ids) {
        List<String> userIdList = Arrays.asList(ids.split(","));
        transactionTemplate.execute((transactionStatus -> {
            sysUserService.removeByIds(userIdList);
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", userIdList));
            return 1;
        }));
        return Result.ok();
    }

    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result rolePermCtrl(@PathVariable("userId") String userId, @RequestBody String[] roleIds) {
        List<SysUserRole> userRoles = new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);
            userRoles.add(sysUserRole);
        });
        transactionTemplate.execute(transactionStatus -> {
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id", userId));
            sysUserRoleService.saveBatch(userRoles);
            return 1;
        });
        // 删除缓存
        SysUser sysUser = sysUserService.getById(userId);
        redisUtil.del("GrantedAuthority:" + sysUser.getUsername());
        return Result.ok();
    }

    @PutMapping("/updatePassword")
    public Result updatePwdCtrl(@Validated @RequestBody SysUserDTO sysUserDTO) {
        if (StringUtils.isEmpty(sysUserDTO.getOldPassword())) {
            return Result.error("旧密码不能为空");
        }
        if (StringUtils.isEmpty(sysUserDTO.getNewPassword())) {
            return Result.error("新密码不能为空");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authentication.getName()));
        boolean matches = passwordEncoder.matches(sysUserDTO.getOldPassword(), sysUser.getPassword());
        if (!matches) {
            return Result.error("旧密码不正确");
        }
        sysUser.setPassword(passwordEncoder.encode(sysUserDTO.getNewPassword()));
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

}
