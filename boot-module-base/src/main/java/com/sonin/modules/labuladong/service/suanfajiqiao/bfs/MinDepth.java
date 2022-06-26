package com.sonin.modules.labuladong.service.suanfajiqiao.bfs;

import com.sonin.modules.labuladong.entity.TreeNode;
import com.sonin.modules.labuladong.service.Solution;

import java.util.LinkedList;
import java.util.Queue;

/**
 * <pre>
 * 111. 二叉树的最小深度
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/6/26 11:09
 */
public class MinDepth implements Solution {

    @Override
    public Object handle() {
        return null;
    }

    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode cur = queue.poll();
                if (cur == null) {
                    break;
                }
                if (cur.left == null && cur.right == null) {
                    return depth;
                }
                if (cur.left != null) {
                    queue.offer(cur.left);
                }
                if (cur.right != null) {
                    queue.offer(cur.right);
                }
            }
            depth++;
        }
        return depth;
    }

}
