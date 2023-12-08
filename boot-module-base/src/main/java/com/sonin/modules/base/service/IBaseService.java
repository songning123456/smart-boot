package com.sonin.modules.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * @author sonin
 * @date 2022/3/16 15:08
 */
public interface IBaseService {

    Map<String, Object> queryForMap(String sqlSelect, Wrapper<?> queryWrapper);

    IPage<Map<String, Object>> queryForPage(IPage<?> page, String sqlSelect, Wrapper<?> queryWrapper);

    List<Map<String, Object>> queryForList(String sqlSelect, Wrapper<?> queryWrapper);

    Integer update(String tableName, Wrapper<?> updateWrapper);

    Integer delete(String tableName, Wrapper<?> wrapper);

    Integer insert(String tableName, Map<String, Object> ew);

    Integer insert(String tableName, Map<String, Object> ew, String insertType);

    <S> Integer save(String tableName, S entity);

    <S> Integer save(String tableName, S entity, String insertType);

    Integer insertBatch(String tableName, List<Map<String, Object>> dataList);

    Integer insertBatch(String tableName, List<Map<String, Object>> dataList, String insertType);

    <S> Integer saveBatch(String tableName, List<S> dataList);

    <S> Integer saveBatch(String tableName, List<S> dataList, String insertType);

}
