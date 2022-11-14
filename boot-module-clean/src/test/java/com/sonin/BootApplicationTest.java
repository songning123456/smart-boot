package com.sonin;

import com.sonin.core.context.SpringContext;
import com.sonin.modules.base.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

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

    @Test
    public void test() {
        String sql = "select * from sys_user limit 0, 1";
        JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
        List<Map<String, Object>> mapList = masterDB.queryForList(sql);
        System.out.println(mapList);
    }

    @Test
    public void importDictTest() {
        // 自定义报表
        Map<String, String> dictMap = new HashMap<String, String>() {{
            put("tableName", "custom_report");
            put("dictId", "f8750d09eeaca38d2863820fe3e72628");
        }};
        JdbcTemplate srcDB = (JdbcTemplate) SpringContext.getBean("ym-ssjk");
        List<Map<String, Object>> srcMapList = srcDB.queryForList("select * from " + dictMap.get("tableName"));
        List<Map<String, String>> targetMapList = new ArrayList<>();
        for (Map<String, Object> src : srcMapList) {
            targetMapList.add(new HashMap<String, String>() {{
                put("item_text", String.valueOf(src.get("item_text")));
                put("item_value", String.valueOf(src.get("item_value")));
                put("description", String.valueOf(src.get("description")));
            }});
        }
        // 排序
        // targetMapList.sort(Comparator.comparing(o -> o.get("item_text")));

        // 以下不做修改
        String dictId = dictMap.get("dictId");
        for (int i = 0; i < targetMapList.size(); i++) {
            int finalI = i;
            baseService.insert("sys_dict_item", new HashMap<String, Object>() {{
                put("dict_id", dictId);
                put("item_text", targetMapList.get(finalI).get("item_text"));
                put("item_value", targetMapList.get(finalI).get("item_value"));
                put("description", targetMapList.get(finalI).get("description"));
                put("sort_order", finalI + 1);
                put("status", 1);
            }});
        }
    }

}
