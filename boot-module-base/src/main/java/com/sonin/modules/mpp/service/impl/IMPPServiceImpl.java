package com.sonin.modules.mpp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.CaseFormat;
import com.sonin.modules.mpp.component.UniqueIdService;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.mapper.MPPMapper;
import com.sonin.modules.mpp.service.IMPPService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author sonin
 * @date 2022/3/16 15:08
 */
@Service
public class IMPPServiceImpl implements IMPPService {

    @Resource
    private MPPMapper MPPMapper;
    @Resource
    private UniqueIdService uniqueIdService;

    @Override
    public String queryForString(String sqlSelect, Wrapper<?> wrapper) {
        return MPPMapper.queryForString(sqlSelect, wrapper);
    }

    @Override
    public Map<String, Object> queryForMap(String sqlSelect, Wrapper<?> wrapper) {
        return MPPMapper.queryForMap(sqlSelect, wrapper);
    }

    @Override
    public IPage<Map<String, Object>> queryForPage(IPage<?> page, String sqlSelect, Wrapper<?> wrapper) {
        return MPPMapper.queryForPage(page, sqlSelect, wrapper);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sqlSelect, Wrapper<?> wrapper) {
        return MPPMapper.queryForList(sqlSelect, wrapper);
    }

    @Override
    public Integer update(String tableName, Wrapper<?> wrapper) {
        return MPPMapper.update(tableName, wrapper);
    }

    @Override
    public Integer delete(String tableName, Wrapper<?> wrapper) {
        return MPPMapper.delete(tableName, wrapper);
    }

    @Override
    public Integer insert(String tableName, Map<String, Object> ew) {
        // 设置主键ID
        IDFunc(ew);
        return insert(tableName, ew, MPPConstant.INSERT);
    }

    @Override
    public Integer insert(String tableName, Map<String, Object> ew, String insertType) {
        // 设置主键ID
        IDFunc(ew);
        return MPPMapper.insert(tableName, ew, insertType);
    }

    @Override
    public <S> Integer save(String tableName, S entity) {
        return save(tableName, entity, MPPConstant.INSERT);
    }

    @Override
    public <S> Integer save(String tableName, S entity, String insertType) {
        Map<String, Object> ew = new HashMap<>();
        // entity => map
        try {
            Class<?> clazz = entity.getClass();
            Field[] fields;
            while (!MPPConstant.OBJECT_CLASS_NAME.equals(clazz.getName())) {
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
        IDFunc(ew);
        return MPPMapper.insert(tableName, ew, insertType);
    }

    @Override
    public Integer insertBatch(String tableName, List<Map<String, Object>> dataList) {
        return insertBatch(tableName, dataList, MPPConstant.INSERT);
    }

    @Override
    public Integer insertBatch(String tableName, List<Map<String, Object>> dataList, String insertType) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }
        // 获取所有key
        Set<String> keysSet = new HashSet<>();
        dataList.forEach(map -> keysSet.addAll(map.keySet()));
        List<String> keys = new ArrayList<>(keysSet);
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
            IDFunc(ew);
            ewList.add(ew);
        }
        return MPPMapper.insertBatch(tableName, keys, ewList, insertType);
    }

    @Override
    public <S> Integer saveBatch(String tableName, List<S> dataList) {
        return saveBatch(tableName, dataList, MPPConstant.INSERT);
    }

    @Override
    public <S> Integer saveBatch(String tableName, List<S> dataList, String insertType) {
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
                while (!MPPConstant.OBJECT_CLASS_NAME.equals(clazz.getName())) {
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
                IDFunc(ew);
                ewList.add(ew);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MPPMapper.insertBatch(tableName, keys, ewList, insertType);
    }

    /**
     * ID插入处理
     *
     * @param ew
     * @return
     */
    private void IDFunc(Map<String, Object> ew) {
        if (ew.containsKey(MPPConstant.ID) && (ew.get(MPPConstant.ID) == null || "".equals(ew.get(MPPConstant.ID)))) {
            ew.put(MPPConstant.ID, uniqueIdService.nextId());
        }
    }

}
