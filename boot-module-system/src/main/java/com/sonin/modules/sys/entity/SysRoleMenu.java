package com.sonin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author sonin
 * @since 2022-03-25
 */
@Data
public class SysRoleMenu {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String roleId;

    private String menuId;

}
