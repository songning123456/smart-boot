package com.sonin.modules.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.CaseFormat;
import com.sonin.modules.base.mapper.BaseMapper;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.UniqIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
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

    @Override
    public <S> Integer insert(String tableName, S entity) {
        Map<String, Object> ew = new HashMap<>();
        // entity => map
        try {
            Class clazz = entity.getClass();
            Field[] fields;
            while (!"java.lang.Object".equals(clazz.getName())) {
                fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    // 过滤null
                    if (field.get(entity) != null) {
                        ew.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()), field.get(entity));
                    }
                    field.setAccessible(false);
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置主键ID
        if (ew.get("id") == null) {
            ew.put("id", UniqIdUtils.getInstance().getUniqID());
        }
        return baseMapper.insert(tableName, ew);
    }

}
