package com.sonin.utils;

import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.entity.MapDFS;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * 请求参数工具类
 *
 * @Author：sonin
 * @Date：2025/3/6 15:05
 */
public class ParamUtils {

    /**
     * 时间范围参数处理
     *
     * @param startTime
     * @param endTime
     * @param timeType
     * @return
     */
    public static List<String> timeRangeParamFunc(String startTime, String endTime, String timeType) {
        List<String> timeList = new ArrayList<>(), intervalTimeList;
        if ("day".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByDay(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 10));
            for (String intervalTime : intervalTimeList) {
                timeList.add(intervalTime + BusinessConstant.START_TIME_SUFFIX + "~" + intervalTime + BusinessConstant.END_TIME_SUFFIX);
            }
        } else if ("month".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 7));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01" + BusinessConstant.START_TIME_SUFFIX;
                int days = DateUtils.lengthOfSomeMonth(Integer.parseInt(intervalTime.split("-")[0]), Integer.parseInt(intervalTime.split("-")[1]));
                String tmpEndTime = intervalTime + "-" + days + BusinessConstant.END_TIME_SUFFIX;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            intervalTimeList = DateUtils.intervalByYear(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 4));
            for (String intervalTime : intervalTimeList) {
                String tmpStartTime = intervalTime + "-01-01" + BusinessConstant.START_TIME_SUFFIX;
                String tmpEndTime = intervalTime + "-12-31" + BusinessConstant.END_TIME_SUFFIX;
                timeList.add(tmpStartTime + "~" + tmpEndTime);
            }
        }
        return timeList;
    }

    /**
     * 机构 根据某一层级获取所有子级(包括本级)
     *
     * @param parentId
     * @return
     */
    public static Set<String> getChildDepartIdFunc(String parentId) {
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        MapDFS mapDFS = new MapDFS();
        List<Map<String, Object>> tree = mapDFS.buildTree(masterDB.queryForList("select id, parent_id as parentId from sys_depart"));
        LinkedList<LinkedList<Map<String, Object>>> routeList = mapDFS.getRouteList(tree);
        Set<String> childIdSet = new HashSet<>();
        for (LinkedList<Map<String, Object>> route : routeList) {
            for (int i = 0; i < route.size(); i++) {
                if (parentId.equals(route.get(i).get("id"))) {
                    for (int j = i; j < route.size(); j++) {
                        childIdSet.add(ConvertUtils.getString(route.get(j).get("id")));
                    }
                }
            }
        }
        return childIdSet;
    }

    /**
     * 通用 根据某一层级获取所有子级(包括本级)
     *
     * @param parentId
     * @return
     */
    public static Set<String> getChildrenIdFunc(String parentId, String tableName, Map<String, String> columnMap) {
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        List<String> columnList = new ArrayList<>();
        if (columnMap != null && !columnMap.isEmpty()) {
            for (Map.Entry<String, String> entry : columnMap.entrySet()) {
                columnList.add(entry.getKey() + " as " + entry.getValue());
            }
        } else {
            columnList.add("id");
            columnList.add("parent_id as parentId");
        }
        MapDFS mapDFS = new MapDFS();
        List<Map<String, Object>> tree = mapDFS.buildTree(masterDB.queryForList("select " + String.join(",", columnList) + " from " + tableName));
        LinkedList<LinkedList<Map<String, Object>>> routeList = mapDFS.getRouteList(tree);
        Set<String> childrenIdSet = new HashSet<>();
        for (LinkedList<Map<String, Object>> route : routeList) {
            for (int i = 0; i < route.size(); i++) {
                if (parentId.equals(route.get(i).get("id"))) {
                    for (int j = i; j < route.size(); j++) {
                        childrenIdSet.add(ConvertUtils.getString(route.get(j).get("id")));
                    }
                }
            }
        }
        return childrenIdSet;
    }

    /**
     * 统一保留小数位数
     *
     * @param paramMap
     * @param nPoint
     */
    public static void retainDecimalFunc(Map<String, Object> paramMap, int nPoint, List<String> columnList) {
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getValue() instanceof List) {
                ((List) entry.getValue()).forEach(value -> {
                    if (value instanceof Map) {
                        retainDecimalFunc((Map<String, Object>) value, nPoint, columnList);
                    } else {
                        if (columnList.contains(entry.getKey())) {
                            paramMap.put(entry.getKey(), ConvertUtils.nPoint(value, nPoint));
                        }
                    }
                });
            } else {
                if (columnList.contains(entry.getKey())) {
                    paramMap.put(entry.getKey(), ConvertUtils.nPoint(entry.getValue(), nPoint));
                }
            }
        }
    }

    /**
     * 根据id查找子树
     *
     * @param tree 树形结构列表
     * @param id   目标节点的id
     * @return 匹配节点及其子树，如果未找到则返回 null
     */
    public static List<Map<String, Object>> findSubtreeById(List<Map<String, Object>> tree, String id) {
        if (tree == null || tree.isEmpty()) {
            return null;
        }
        for (Map<String, Object> node : tree) {
            // 先判断当前节点是否匹配
            if (id.equals(node.get("id"))) {
                // 找到匹配节点，返回以该节点为根的子树（包含自身）
                List<Map<String, Object>> result = new ArrayList<>();
                // 复制节点，避免修改原数据
                result.add(new HashMap<>(node));
                return result;
            }
            // 递归查找子节点
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            List<Map<String, Object>> subtree = findSubtreeById(children, id);
            if (subtree != null) {
                return subtree;
            }
        }
        // 未找到匹配节点
        return null;
    }

    /**
     * 过滤树形结构
     *
     * @param tree 原始树形结构
     * @return 过滤后的树形结构
     */
    public static List<Map<String, Object>> filterTreeByDepartType(List<Map<String, Object>> tree, List<String> ignoreDepartTypeList) {
        if (tree == null || tree.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> filteredTree = new ArrayList<>();
        for (Map<String, Object> node : tree) {
            // 检查当前节点的departType
            String departType = ConvertUtils.getString(node.get("departType"));
            // 如果是需要过滤的类型，则跳过当前节点及其子树
            if (ignoreDepartTypeList.contains(departType)) {
                continue;
            }
            // 复制当前节点，避免修改原数据
            Map<String, Object> filteredNode = new HashMap<>(node);
            // 递归处理子节点
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            List<Map<String, Object>> filteredChildren = filterTreeByDepartType(children, ignoreDepartTypeList);
            filteredNode.put("children", filteredChildren);
            // 将处理后的节点添加到结果集中
            filteredTree.add(filteredNode);
        }
        return filteredTree;
    }

}
