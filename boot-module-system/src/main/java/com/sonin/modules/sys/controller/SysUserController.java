package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.constant.Const;
import com.sonin.modules.sys.dto.PasswordDTO;
import com.sonin.modules.sys.dto.SysUserDTO;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.modules.sys.service.SysRoleService;
import com.sonin.modules.sys.service.SysUserRoleService;
import com.sonin.modules.sys.service.SysUserService;
import com.sonin.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

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
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户信息接口
     *
     * @param principal
     * @return
     */
    @GetMapping("/userInfo")
    public Result<Object> userInfoCtrl(Principal principal) {
        Result<Object> result = new Result<>();
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", principal.getName()));
        Map<String, Object> resMap = new HashMap<String, Object>() {{
            put("id", sysUser.getId());
            put("username", sysUser.getUsername());
            put("avatar", sysUser.getAvatar());
        }};
        result.setResult(resMap);
        return result;
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<Object> infoCtrl(@PathVariable("id") Long id) {
        Result<Object> result = new Result<>();
        SysUser sysUser = sysUserService.getById(id);
        List<SysRole> sysRoles = sysRoleService.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + id));
        sysUser.setSysRoles(sysRoles);
        result.setResult(sysUser);
        return result;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<Object> listCtrl(SysUserDTO sysUserDTO) {
        Result<Object> result = new Result<>();
        String username = sysUserDTO.getUsername();
        Page<SysUser> page = new Page<>(sysUserDTO.getPageNo(), sysUserDTO.getPageSize());
        Page<SysUser> sysUserPage = sysUserService.page(page, new QueryWrapper<SysUser>().like(StringUtils.isNotEmpty(username), "username", username));
        sysUserPage.getRecords().forEach(item -> {
            List<SysRole> sysRoles = sysRoleService.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + item.getId()));
            item.setSysRoles(sysRoles);
        });
        result.setResult(sysUserPage);
        return result;
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result<Object> saveCtrl(@Validated @RequestBody SysUser sysUser) {
        // 默认密码
        String password = passwordEncoder.encode(Const.DEFULT_PASSWORD);
        sysUser.setPassword(password);
        // 默认头像
        sysUser.setAvatar(Const.DEFULT_AVATAR);
        sysUserService.save(sysUser);
        return Result.ok();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<Object> update(@Validated @RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<Object> delete(@RequestBody Long[] ids) {
        transactionTemplate.execute((transactionStatus -> {
            sysUserService.removeByIds(Arrays.asList(ids));
            sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", Arrays.asList(ids)));
            return 1;
        }));
        return Result.ok();
    }

    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result<Object> rolePermCtrl(@PathVariable("userId") String userId, @RequestBody String[] roleIds) {
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

    @PostMapping("/rePass")
    @PreAuthorize("hasAuthority('sys:user:repass')")
    public Result<Object> rePassCtrl(@RequestBody Long userId) {

        SysUser sysUser = sysUserService.getById(userId);
        sysUser.setPassword(passwordEncoder.encode(Const.DEFULT_PASSWORD));
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

    @PutMapping("/updatePwd")
    public Result<Object> updatePwdCtrl(@Validated @RequestBody PasswordDTO passwordDTO, Principal principal) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", principal.getName()));
        boolean matches = passwordEncoder.matches(passwordDTO.getCurrentPwd(), sysUser.getPassword());
        if (!matches) {
            return Result.error("旧密码不正确");
        }
        sysUser.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

}
