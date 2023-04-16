package com.sonin.core.dfs;

import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/16 8:54
 */
public class MapDFS {

    // 定义基础常量

    private String EMPTY = "";

    private String id = "id";

    private String parentId = "parentId";

    private String name = "name";

    private String type = "type";

    private String children = "children";

    private String orderNum = "orderNum";

    private List<String> parentList = new ArrayList<String>() {{
        add("");
        add("null");
    }};

    public MapDFS() {
    }

    public MapDFS(String name, String type, String orderNum, List<String> parentList) {
        if (name != null && name.length() != 0) {
            this.name = name;
        }
        if (type != null && type.length() != 0) {
            this.type = type;
        }
        if (orderNum != null && orderNum.length() != 0) {
            this.orderNum = orderNum;
        }
        if (parentList != null && !parentList.isEmpty()) {
            this.parentList = parentList;
        }
    }

    private LinkedList<LinkedList<Map<String, Object>>> routeList = new LinkedList<>();

    private void backtrack(List<Map<String, Object>> tree, LinkedList<Map<String, Object>> track) {
        if (!track.isEmpty() && ((List) track.getLast().get(children)).isEmpty()) {
            routeList.add(new LinkedList<>(track));
        }
        for (Map<String, Object> item : tree) {
            track.addLast(item);
            backtrack((List) item.get(children), track);
            track.removeLast();
        }
    }

    public LinkedList<LinkedList<Map<String, Object>>> getRouteList(List<Map<String, Object>> tree) {
        this.backtrack(tree, new LinkedList<>());
        return routeList;
    }

    public List<Map<String, Object>> buildTree(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map<String, Object> k : mapList) {
            k.putIfAbsent(children, new ArrayList<>());
            for (Map<String, Object> v : mapList) {
                if (k.get(id).equals(v.get(parentId))) {
                    ((List) k.get(children)).add(v);
                }
            }
            // 提取出父节点
            String parentIdStr = EMPTY + k.get(parentId);
            if (parentList.contains(parentIdStr)) {
                tree.add(k);
            }
        }
        // 排序
        sortFunc(tree);
        return tree;
    }

    /**
     * <pre>
     * 先按照orderNum升序排序，再按照name排序
     * </pre>
     * @param list
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void sortFunc(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            list.sort((o1, o2) -> {
                if (o1.get(orderNum) == null || o2.get(orderNum) == null) {
                    return (EMPTY + o1.get(name)).compareTo(EMPTY + o2.get(name));
                } else {
                    if (o1.get(orderNum).equals(o2.get(orderNum))) {
                        return (EMPTY + o1.get(name)).compareTo(EMPTY + o2.get(name));
                    } else {
                        return (Integer.valueOf(EMPTY + o1.get(orderNum))).compareTo(Integer.valueOf(EMPTY + o2.get(orderNum)));
                    }
                }
            });
            for (Map<String, Object> item : list) {
                if (item.get(children) instanceof List) {
                    sortFunc((List) item.get(children));
                }
            }
        }
    }

}
