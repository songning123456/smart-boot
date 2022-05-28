package com.sonin.modules.labuladong.entity;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/28 11:14
 */
public class DoubleList {

    // 头节点
    private Node head;

    // 尾节点
    private Node tail;

    private Integer size;

    public DoubleList() {
        // 初始化
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    // 添加尾节点
    public void addLast(Node x) {
        x.prev = tail.prev;
        x.next = tail;
        tail.prev.next = x;
        tail.prev = x;
        size++;
    }

    // 删除节点x
    public void remove(Node x) {
        x.prev.next = x.next;
        x.next.prev = x.prev;
    }

    // 删除第一个节点
    public Node removeFirst() {
        if (head.next == tail) {
            return null;
        }
        Node first = head.next;
        remove(first);
        return first;
    }

    // 返回链表长度
    public int size() {
        return size;
    }


}
