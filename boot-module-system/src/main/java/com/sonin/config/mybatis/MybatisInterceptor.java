package com.sonin.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author sonin
 * @date 2022/3/27 9:38
 * mybatis拦截器，自动注入创建时间、更新时间
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }
        if (SqlCommandType.INSERT == sqlCommandType) {
            forEachObj(parameter, new HashMap<String, Object>() {{
                put("createTime", new Date());
            }});
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            if (parameter instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameter;
                //update-begin-author:scott date:20190729 for:批量更新报错issues/IZA3Q--
                if (paramMap.containsKey("et")) {
                    parameter = paramMap.get("et");
                } else {
                    parameter = paramMap.get("param1");
                }
            }
            forEachObj(parameter, new HashMap<String, Object>() {{
                put("updateTime", new Date());
            }});
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 遍历类对象，赋值
     *
     * @param object
     * @param name2ValMap
     * @throws Exception
     */
    private void forEachObj(Object object, Map<String, Object> name2ValMap) throws Exception {
        Class<?> clazz = object.getClass();
        while (!"java.lang.Object".equals(clazz.getName())) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (name2ValMap.containsKey(field.getName())) {
                    field.setAccessible(true);
                    field.set(object, name2ValMap.get(field.getName()));
                    field.setAccessible(false);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

}
