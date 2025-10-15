package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.QueueUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
@ActiveProfiles("zhongtie")
public class ZhongtieBootApplicationTest {

    @Autowired
    private IMPPService baseService;

    @Test
    public void updateSysDepartTest() {
        String step = "3";
        // 1. 更新机构
        if ("1".equals(step)) {
            // 查询为空的机构
            QueryWrapper<?> queryWrapper0_0 = new QueryWrapper<>();
            queryWrapper0_0.apply("(depart_name_en is null or depart_name_en = '')");
            List<Map<String, Object>> queryMapList0_0 = baseService.queryForList("select * from sys_depart", queryWrapper0_0);
            // 查询不为空的总数
            QueryWrapper<?> queryWrapper0_1 = new QueryWrapper<>();
            queryWrapper0_1.apply("(depart_name_en is not null and depart_name_en != '')");
            String countStr0_1 = baseService.queryForString("select count(*) from sys_depart", queryWrapper0_1);
            int count0_1 = Integer.parseInt(countStr0_1);
            int index = 1;
            for (Map<String, Object> item : queryMapList0_0) {
                String id = ConvertUtils.getString(item.get("id"));
                UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                updateWrapper0.set("depart_name_en", count0_1 + index)
                        .eq("id", id);
                baseService.update("sys_depart", updateWrapper0);
                index++;
            }
        } else if ("2".equals(step)) {
            // 2. 更新 equip_info 中的 attr3
            String updateSql = "update equip_info, sys_depart set equip_info.attr3 = sys_depart.depart_name_en where equip_info.factory_id = sys_depart.id";
            JdbcTemplate masterDB = (JdbcTemplate) SpringContext.getBean("master");
            masterDB.execute(updateSql);
        } else if ("3".equals(step)) {
            // 3. 找出设备台账里equip_code以null开始的数据并更新
            QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
            queryWrapper0.like("equip_code", "null-");
            List<Map<String, Object>> queryMapList0 = baseService.queryForList("select * from equip_info", queryWrapper0);
            for (Map<String, Object> item : queryMapList0) {
                String id = ConvertUtils.getString(item.get("id"));
                String attr3 = ConvertUtils.getString(item.get("attr3"));
                String equipCode = ConvertUtils.getString(item.get("equip_code"));
                String tempEquipCode = equipCode.replace("null", attr3);
                UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                updateWrapper0.set("equip_code", tempEquipCode)
                        .eq("id", id);
                baseService.update("equip_info", updateWrapper0);
            }
        }
    }

    @Test
    public void updateCountData() {
        List<Map<String, Object>> srcDataMapList = DataSourceTemplate.execute("native", () -> baseService.queryForList("select * from baoxie_data", new QueryWrapper<>()));
        for (Map<String, Object> item : srcDataMapList) {
            String nm = ConvertUtils.getString(item.get("nm"));
            String time = ConvertUtils.getString(item.get("time"));
            String ts = DateUtils.dateStr2Sec(time, BusinessConstant.DATE_FORMAT).toString();
            double v = ConvertUtils.getDouble(item.get("v"), 0D);
            // 查询数据
            QueryWrapper<?> tmpQueryWrapper = new QueryWrapper<>();
            tmpQueryWrapper.eq("nm", nm)
                    .eq("ts", ts);
            Map<String, Object> tmpMap = DataSourceTemplate.execute("pg-db", () -> baseService.queryForMap("select * from bxwsclc_count", tmpQueryWrapper));
            if (tmpMap != null) {
                String tmpId = ConvertUtils.getString(tmpMap.get("id"));
                String tmpV = ConvertUtils.getString(tmpMap.get("v"));
                double tmpV0 = ConvertUtils.getDouble(tmpV, 0D);
                if (tmpV0 != v) {
                    // 更新数据
                    UpdateWrapper<?> tmpUpdateWrapper = new UpdateWrapper<>();
                    tmpUpdateWrapper.set("v", v)
                            .eq("id", Integer.parseInt(tmpId));
                    DataSourceTemplate.execute("pg-db", () -> baseService.update("bxwsclc_count", tmpUpdateWrapper));
                }
            }
        }
    }

}
