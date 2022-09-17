package com.sonin;

import com.sonin.core.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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

    @Test
    public void testOne() {
        String sql = "select * from sys_user limit 0, 1";
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        List<Map<String, Object>> mapList = masterDB.queryForList(sql);
        System.out.println(mapList);
    }

}
