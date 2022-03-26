package com.sonin.modules.sys.service;

import com.sonin.modules.sys.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sonin
 * @since 2022-03-25
 */
public interface SysUserService extends IService<SysUser> {

    String getUserAuthorityInfo(String userId);

}
