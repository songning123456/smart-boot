package com.sonin.modules.sys.vo;

import com.sonin.modules.sys.entity.SysMenu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sonin
 * @date 2022/3/26 14:11
 */
@Data
public class SysMenuVO extends SysMenu {

    private List<SysMenuVO> children = new ArrayList<>();

}
