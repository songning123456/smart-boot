package com.sonin.modules.sys.pojo;

import cn.hutool.core.util.StrUtil;
import com.sonin.modules.sys.entity.SysMenu;
import com.sonin.modules.sys.vo.SysMenuVO;
import com.sonin.utils.BeanExtUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * SysMenu的DFS算法
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/30 8:52
 */
public class SysMenuDFS {

    /**
     * <pre>
     * 构建树结构
     * </pre>
     *
     * @param sysMenuList
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public List<SysMenuVO> buildTree(List<SysMenu> sysMenuList) {
        List<SysMenuVO> tree = new ArrayList<>();
        List<SysMenuVO> sysMenuVOList;
        try {
            sysMenuVOList = BeanExtUtils.beans2Beans(sysMenuList, SysMenuVO.class);
        } catch (Exception e) {
            e.printStackTrace();
            sysMenuVOList = new ArrayList<>();
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
                tree.add(k);
            }
        }
        return tree;
    }

    private void backtrack(List<SysMenuVO> tree, LinkedList<SysMenuVO> track, LinkedList<LinkedList<SysMenu>> chain) {
        if (!track.isEmpty() && track.getLast().getChildren().isEmpty()) {
            List<SysMenu> tmp;
            try {
                tmp = BeanExtUtils.beans2Beans(track, SysMenu.class);
            } catch (Exception e) {
                tmp = new ArrayList<>();
            }
            chain.add(new LinkedList<>(tmp));
        }
        for (SysMenuVO sysDepartVO : tree) {
            track.addLast(sysDepartVO);
            backtrack(sysDepartVO.getChildren(), track, chain);
            track.removeLast();
        }
    }

    /**
     * <pre>
     * 获取每一条完整的链路
     * </pre>
     *
     * @param tree
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public LinkedList<LinkedList<SysMenu>> result(List<SysMenuVO> tree) {
        LinkedList<LinkedList<SysMenu>> chain = new LinkedList<>();
        this.backtrack(tree, new LinkedList<>(), chain);
        return chain;
    }

}
