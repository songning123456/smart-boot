package com.sonin.modules.sys.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sonin
 * @date 2022/3/26 14:11
 */
@Data
public class SysMenuVO {

    private String id;

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


    private List<SysMenuVO> children = new ArrayList<>();
}
