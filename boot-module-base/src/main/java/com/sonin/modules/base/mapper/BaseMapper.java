package com.sonin.modules.base.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author sonin
 * @date 2022/3/16 14:19
 */
public interface BaseMapper {

    Map<String, Object> selectMap(@Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> queryWrapper);

    IPage<Map<String, Object>> selectMapsPage(IPage<?> page, @Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> queryWrapper);

    List<Map<String, Object>> selectMaps(@Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> queryWrapper);

    int update(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper<?> updateWrapper);

    int delete(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper<?> wrapper);

}
