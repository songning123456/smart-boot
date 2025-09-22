package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.*;
import java.util.stream.Collectors;

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
@ActiveProfiles("bishuiyuan")
public class BishuiyuanBootApplicationTest {

    @Autowired
    private IMPPService baseService;

    /**
     * 删除重复的点位
     */
    @Test
    public void deleteMulNmTest() {
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0
                .groupBy("depart_id", "metric_name")
                .having("count(*) > 1");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select depart_id, metric_name, GROUP_CONCAT(id) as ids, count(*) as total from sys_monitor_metric_info", queryWrapper0);
        String startTime = "2022-01-01 00:00:00";
        String endTime = "2022-12-31 23:59:59";
        Set<String> nmSet = new HashSet<>();
        // 查询 >1 的点位数据
        for (Map<String, Object> item : queryMapList0) {
            String ids = (String) item.get("ids");
            String[] idArray = ids.split(",");
            QueryWrapper<?> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.ge("CAST(ts AS bigint)", DateUtils.dateStr2Sec(startTime, BusinessConstant.DATE_FORMAT))
                    .le("CAST(ts AS bigint)", DateUtils.dateStr2Sec(endTime, BusinessConstant.DATE_FORMAT))
                    .in("nm", Arrays.asList(idArray));
            List<Map<String, Object>> dataList = DataSourceTemplate.execute("pg-db", () -> baseService.queryForList("select * from realtimedata", queryWrapper1));
            nmSet.addAll(dataList.stream().map(it -> ConvertUtils.getString(it.get("nm"))).collect(Collectors.toList()));
        }
        List<Map<String, Object>> toDeleteMapList = baseService.queryForList("select * from sys_monitor_metric_info", new QueryWrapper<>().in("id", nmSet));
        if (!toDeleteMapList.isEmpty()) {
            baseService.insertBatch("sys_monitor_metric_info_dump", toDeleteMapList);
        }
    }


}
