package com.sonin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class SysRole {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String code;

    /**
     * 备注
     */
    private String remark;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private List<String> menuIds = new ArrayList<>();

}
