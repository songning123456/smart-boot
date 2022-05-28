package com.sonin.modules.labuladong.entity;

import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/28 11:11
 */
public class Node {

    public Integer key;

    public Integer val;

    public Node prev;

    public Node next;

    public Node(int key, int val) {
        this.key = key;
        this.val = val;
    }

}
