package com.sonin.modules.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sonin.modules.base.mapper.BaseMapper;
import com.sonin.modules.base.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author sonin
 * @date 2022/3/16 15:08
 */
@Service
public class IBaseServiceImpl implements IBaseService {

    @Autowired
    private BaseMapper baseMapper;

    @Override
    public Map<String, Object> selectMap(String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.selectMap(sqlSelect, queryWrapper);
    }

    @Override
    public IPage<Map<String, Object>> selectMapsPage(IPage<?> page, String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.selectMapsPage(page, sqlSelect, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.selectMaps(sqlSelect, queryWrapper);
    }

    @Override
    public Integer update(String tableName, Wrapper<?> updateWrapper) {
        return baseMapper.update(tableName, updateWrapper);
    }

    @Override
    public Integer delete(String tableName, Wrapper<?> wrapper) {
        return baseMapper.delete(tableName, wrapper);
    }

}
