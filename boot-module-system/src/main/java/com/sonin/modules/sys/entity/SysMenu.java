package com.sonin.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author sonin
 * @since 2022-03-25
 */
@Data
public class SysMenu {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 父菜单ID，一级菜单为0
     */
    private String parentId;


    /**
     * 菜单标题
     */
    private String metaTitle;

    /**
     * 菜单URL
     */
    private String path;

    /**
     * 授权(多个用逗号分隔，如:user:list,user:create)
     */
    private String permission;

    /**
     * 类型 0:目录;1:菜单; 2:按钮;
     */
    private Integer type;

    /**
     * 菜单图标
     */
    private String metaIcon;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
