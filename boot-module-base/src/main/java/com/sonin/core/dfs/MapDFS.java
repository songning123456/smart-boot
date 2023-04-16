package com.sonin.core.dfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            // 先按照orderNum排序，再按照name排序
            ((List) k.get(children)).sort((o1, o2) -> {
                if (((Map) o1).get(orderNum) == null || ((Map) o2).get(orderNum) == null) {
                    return (EMPTY + ((Map) o1).get(name)).compareTo(EMPTY + ((Map) o2).get(name));
                } else {
                    if (((Map) o1).get(orderNum).equals(((Map) o2).get(orderNum))) {
                        return (EMPTY + ((Map) o2).get(orderNum)).compareTo(EMPTY + ((Map) o2).get(orderNum));
                    } else {
                        return (EMPTY + ((Map) o1).get(orderNum)).compareTo(EMPTY + ((Map) o2).get(orderNum));
                    }
                }
            });
            // 提取出父节点
            String parentIdStr = EMPTY + k.get(parentId);
            if (parentList.contains(parentIdStr)) {
                tree.add(k);
            }
        }
        return tree;
    }

}
