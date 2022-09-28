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
        // 用水类型
        Map<String, String> dictMap = new HashMap<String, String>() {{
            put("tableName", "tb_water_type");
            put("srcText", "INFO");
            put("srcValue", "CODE");
            put("dictId", "188c078117eed1856ee3da4b1d0086bf");
        }};
        // 特殊处理类型
        dictMap = new HashMap<String, String>() {{
            put("tableName", "tb_special_handle_type");
            put("srcText", "描述");
            put("srcValue", "处理编码");
            put("dictId", "d6f8277b096076d316df244d06718497");
        }};
        JdbcTemplate srcDB = (JdbcTemplate) SpringContext.getBean("ym-ssjk");
        List<Map<String, Object>> srcMapList = srcDB.queryForList("select * from " + dictMap.get("tableName"));
        List<Map<String, String>> targetMapList = new ArrayList<>();
        for (Map<String, Object> src : srcMapList) {
            Map<String, String> finalDictMap = dictMap;
            targetMapList.add(new HashMap<String, String>() {{
                put("item_text", "" + src.get(finalDictMap.get("srcText")));
                put("item_value", ("" + src.get(finalDictMap.get("srcValue"))).split("\\.")[0]);
            }});
        }
        // 排序
        targetMapList.sort(Comparator.comparing(o -> o.get("item_text")));

        // 以下不做修改
        String dictId = dictMap.get("dictId");
        for (int i = 0; i < targetMapList.size(); i++) {
            int finalI = i;
            baseService.insert("sys_dict_item", new HashMap<String, Object>() {{
                put("dict_id", dictId);
                put("item_text", targetMapList.get(finalI).get("item_text"));
                put("item_value", targetMapList.get(finalI).get("item_value"));
                put("description", targetMapList.get(finalI).get("item_text"));
                put("sort_order", finalI + 1);
                put("status", 1);
            }});
        }
    }

}
