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

    Map<String, Object> queryForMap(@Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> ew);

    IPage<Map<String, Object>> queryForPage(IPage<?> page, @Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> ew);

    List<Map<String, Object>> queryForList(@Param("sqlSelect") String sqlSelect, @Param(Constants.WRAPPER) Wrapper<?> ew);

    int update(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper<?> ew);

    int delete(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Wrapper<?> ew);

    int insert(@Param("tableName") String tableName, @Param(Constants.WRAPPER) Map ew, @Param("insertType") String insertType);

    int insertBatch(@Param("tableName") String tableName, @Param("keys") List<String> keys, @Param("ewList") List<Map> ewList, @Param("insertType") String insertType);

}
