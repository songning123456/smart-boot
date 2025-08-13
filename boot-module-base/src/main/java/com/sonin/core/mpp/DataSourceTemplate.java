package com.sonin.core.mpp;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.sonin.core.callback.IDataSourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 数据源模板
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/4/28 15:02
 */
public class DataSourceTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceTemplate.class);

    public static <T> T execute(String dbName, IDataSourceCallback<T> iDataSourceCallback) {
        // 参数校验（增强空值检查）
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库名称不能为空");
        }
        if (iDataSourceCallback == null) {
            throw new IllegalArgumentException("回调函数不能为空");
        }
        T result = null;
        long startTime = System.currentTimeMillis();
        String originalDbName = null;
        boolean isOriginalEmpty = true;
        boolean isDataSourcePushed = false;
        try {
            // 安全获取原始数据源（防止peek()返回null导致的问题）
            originalDbName = DynamicDataSourceContextHolder.peek();
            isOriginalEmpty = (originalDbName == null || originalDbName.trim().isEmpty());
            logger.debug("当前数据源: {}", isOriginalEmpty ? "空" : originalDbName);
            // 切换数据源并标记状态
            logger.debug("准备切换数据源至: {}", dbName);
            DynamicDataSourceContextHolder.push(dbName);
            // 标记数据源已成功推入
            isDataSourcePushed = true;
            logger.debug("已切换至数据源: {}", dbName);
            // 执行回调操作
            result = iDataSourceCallback.execute();
            logger.info("数据源[{}]操作成功，耗时: {}ms", dbName, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("数据源[{}]操作失败，耗时: {}ms", dbName, System.currentTimeMillis() - startTime, e);
        } finally {
            // 只有在数据源成功推入的情况下才需要恢复
            if (isDataSourcePushed) {
                if (isOriginalEmpty) {
                    DynamicDataSourceContextHolder.clear();
                    logger.debug("已清除数据源上下文");
                } else {
                    DynamicDataSourceContextHolder.push(originalDbName);
                    logger.debug("已恢复数据源至: {}", originalDbName);
                }
            }
            logger.debug("数据源操作流程结束，总耗时: {}ms", System.currentTimeMillis() - startTime);
        }
        return result;
    }

}
