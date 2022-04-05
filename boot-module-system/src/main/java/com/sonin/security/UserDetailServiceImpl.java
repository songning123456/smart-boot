package com.sonin.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.modules.sys.entity.SysUser;
import com.sonin.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new AccountUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), null);
    }

}
