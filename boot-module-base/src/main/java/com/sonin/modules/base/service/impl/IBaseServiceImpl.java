package com.sonin.modules.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.CaseFormat;
import com.sonin.modules.base.mapper.BaseMapper;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.sequence.service.impl.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author sonin
 * @date 2022/3/16 15:08
 */
@Service
public class IBaseServiceImpl implements IBaseService {

    @Autowired
    private BaseMapper baseMapper;
    @Autowired
    private SequenceService sequenceService;

    @Override
    public Map<String, Object> queryForMap(String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.queryForMap(sqlSelect, queryWrapper);
    }

    @Override
    public IPage<Map<String, Object>> queryForPage(IPage<?> page, String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.queryForPage(page, sqlSelect, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sqlSelect, Wrapper<?> queryWrapper) {
        return baseMapper.queryForList(sqlSelect, queryWrapper);
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
    public Integer insert(String tableName, Map<String, Object> ew) {
        // 设置主键ID
        if (ew.get("id") == null) {
            ew.put("id", sequenceService.nextId());
        }
        return baseMapper.insert(tableName, ew);
    }

    @Override
    public <S> Integer save(String tableName, S entity) {
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
            ew.put("id", sequenceService.nextId());
        }
        return baseMapper.insert(tableName, ew);
    }

    @Override
    public Integer insertBatch(String tableName, List<Map<String, Object>> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        // 获取第一条数据的keys
        List<String> keys = new ArrayList<>(dataList.get(0).keySet());
        // 添加主键id
        if (!keys.contains("id")) {
            keys.add("id");
        }
        // 排序
        keys.sort(String::compareTo);
        List<Map> ewList = new ArrayList<>();
        Map ew;
        for (Map<String, Object> data : dataList) {
            ew = new LinkedHashMap();
            for (String key : keys) {
                ew.put(key, data.get(key));
            }
            // 设置主键ID
            if (ew.get("id") == null) {
                ew.put("id", sequenceService.nextId());
            }
            ewList.add(ew);
        }
        return baseMapper.insertBatch(tableName, keys, ewList);
    }

    @Override
    public <S> Integer saveBatch(String tableName, List<S> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        List<String> keys = new ArrayList<>();
        List<Map> ewList = new ArrayList<>();
        Map ew;
        Class clazz;
        Field[] fields;
        S entity;
        try {
            for (int i = 0; i < dataList.size(); i++) {
                clazz = dataList.get(i).getClass();
                entity = dataList.get(i);
                ew = new LinkedHashMap();
                while (!"java.lang.Object".equals(clazz.getName())) {
                    fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        // 第一条数据，获取key
                        if (i == 0) {
                            keys.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
                        }
                        ew.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()), field.get(entity));
                        field.setAccessible(false);
                    }
                    clazz = clazz.getSuperclass();
                }
                // 设置主键ID
                if (ew.get("id") == null) {
                    ew.put("id", sequenceService.nextId());
                }
                ewList.add(ew);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseMapper.insertBatch(tableName, keys, ewList);
    }

    @Override
    public List<Map<String, Object>> querySql(String sql) {
        if ("select".equals(sql.split(" ")[0].toLowerCase())) {
            return baseMapper.querySql(sql);
        }
        return null;
    }

    @Override
    public Integer insertSql(String sql) {
        if ("insert".equals(sql.split(" ")[0].toLowerCase())) {
            return baseMapper.insertSql(sql);
        }
        return null;
    }

    @Override
    public Integer deleteSql(String sql) {
        if ("delete".equals(sql.split(" ")[0].toLowerCase())) {
            return baseMapper.deleteSql(sql);
        }
        return null;
    }

    @Override
    public Integer updateSql(String sql) {
        if ("update".equals(sql.split(" ")[0].toLowerCase())) {
            return baseMapper.updateSql(sql);
        }
        return null;
    }

}
