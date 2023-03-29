package com.sonin;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.sonin.modules.base.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;

/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class BootApplicationTest {

    @Autowired
    private IBaseService baseService;

    /**
    * <pre>
    * 动态数据源切换测试
    * </pre>
     * @param
    * @author sonin
    * @Description: TODO(这里描述这个方法的需求变更情况)
    */
    @Test
    public void DSTest() {
        // 切换test数据源
        DynamicDataSourceContextHolder.push("test");
        baseService.insert("sys_log", new LinkedHashMap<String, Object>(){{
            put("id", "1");
        }});
        DynamicDataSourceContextHolder.poll();
        // 切换test2数据源
        DynamicDataSourceContextHolder.push("test2");
        baseService.insert("sys_log", new LinkedHashMap<String, Object>(){{
            put("id", "2");
        }});
        DynamicDataSourceContextHolder.poll();
        // 默认执行master数据源
        baseService.insert("sys_log", new LinkedHashMap<String, Object>(){{
            put("id", "3");
        }});
    }

}
