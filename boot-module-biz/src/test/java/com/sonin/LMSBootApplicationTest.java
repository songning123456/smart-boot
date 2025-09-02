package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.constant.BaseConstant;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
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
@ActiveProfiles("lms")
public class LMSBootApplicationTest {

    @Autowired
    private IBaseService baseService;

    /**
     * 插入设备
     */
    @Test
    public void insertProductTest() {
        // 查询 待插入 设备数据
        List<Map<String, Object>> queryMapList0 = DataSourceTemplate.execute("native", () -> baseService.queryForList("select * from lms_product_sheet", new QueryWrapper<>()));
        // 遍历插入
        for (Map<String, Object> item : queryMapList0) {
            String productCode = ConvertUtils.getString(item.get("id"));
            // 插入对象
            Map<String, Object> entityMap = new LinkedHashMap<>();
            entityMap.put("id", productCode);
            entityMap.put("product_code", productCode);
            entityMap.put("product_name", productCode);
            entityMap.put("product_type", "1");
            entityMap.put("tech_control_type", "DC");
            entityMap.put("tech_big_caliber", "100");
            entityMap.put("tech_small_caliber", "15");
            entityMap.put("transfer_protocol", "rtu");
            entityMap.put("rtu_sn", "0");
            entityMap.put("communication_method", "4G");
            DataSourceTemplate.execute("master", () -> {
                baseService.insert("lms_product", entityMap, BaseConstant.INSERT_IGNORE);
                return 1;
            });
        }
    }


}
