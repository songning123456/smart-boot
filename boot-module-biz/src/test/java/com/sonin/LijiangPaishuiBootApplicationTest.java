package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("ljps")
public class LijiangPaishuiBootApplicationTest {

    @Autowired
    private IMPPService baseService;

    /**
     * 修改count数据
     */
    @Test
    public void updateCountDataTest() {
        String nm = "W5307000081_10451_QA";
        String tableName = "w5307000081_count";
        // 查询所有待修改的数据
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from ljps_count", new QueryWrapper<>());
        // 遍历
        for (Map<String, Object> item : queryMapList0) {
            String time = ConvertUtils.getString(item.get("ts"));
            String v = ConvertUtils.getString(item.get("new_v"));
            String ts = ConvertUtils.getString(DateUtils.dateStr2Sec(time, BusinessConstant.DATE_FORMAT));
            // 更新数据
            UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
            updateWrapper0.set("v", v)
                    .eq("ts", ts)
                    .eq("nm", nm);
            DataSourceTemplate.execute("pg-db", () -> {
                baseService.update(tableName, updateWrapper0);
                return 1;
            });
        }
    }

    /**
     * 当累计流量为0时，瞬时流量也为0
     */
    @Test
    public void updateQTest() {
        // 瞬时流量
        String nm = "W5307000081_10451_Q";
        String tableName = "w5307000081_count";
        // 查询所有待修改的数据
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from ljps_count", new QueryWrapper<>());
        // 遍历
        for (Map<String, Object> item : queryMapList0) {
            String time = ConvertUtils.getString(item.get("ts"));
            String v = ConvertUtils.getString(item.get("new_v"));
            String ts = ConvertUtils.getString(DateUtils.dateStr2Sec(time, BusinessConstant.DATE_FORMAT));
            // 总流量为0， 瞬时流量也为0
            if (Double.parseDouble(v) == 0D) {
                // 更新数据
                UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                updateWrapper0.set("v","0")
                        .eq("ts", ts)
                        .eq("nm", nm);
                DataSourceTemplate.execute("pg-db", () -> {
                    baseService.update(tableName, updateWrapper0);
                    return 1;
                });
            }
        }
    }


}
