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

    Map<String, Object> selectMap(String sqlSelect, Wrapper<?> queryWrapper);

    IPage<Map<String, Object>> selectMapsPage(IPage<?> page, String sqlSelect, Wrapper<?> queryWrapper);

    List<Map<String, Object>> selectMaps(String sqlSelect, Wrapper<?> queryWrapper);

    Integer update(String tableName, Wrapper<?> updateWrapper);

    Integer delete(String tableName, Wrapper<?> wrapper);

}
