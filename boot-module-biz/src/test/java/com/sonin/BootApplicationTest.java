package com.sonin;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.context.SpringContext;
import com.sonin.core.mpp.BaseFactory;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.sys.entity.SysRole;
import com.sonin.modules.sys.entity.SysUserRole;
import com.sonin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

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
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testLambda() {
        List<Map<String, Object>> mapList = BaseFactory.JOIN()
                .select(SysRole::getId)
                .select(SysUserRole::getRoleId)
                .from(SysRole.class)
                .innerJoin(SysUserRole.class, SysUserRole::getRoleId, SysRole::getId)
                .where()
                .eq(true, SysUserRole::getRoleId, "074fb9ddf4f24478bf06da0f5619dc78")
                .eq(true, SysRole::getId, "074fb9ddf4f24478bf06da0f5619dc78")
                .queryForList();
        System.out.println(mapList);
    }

    @Test
    public void testInsertMonitor() {
        // 指定数据源
        String dataSource = "mysql-kingtrol-dev";
        JdbcTemplate devDB = (JdbcTemplate) SpringContext.getBean(dataSource);
        // 查询count
        Map<String, Object> countMap = devDB.queryForMap("select count(*) as total from sys_metric_dict");
        int maxCount = Convert.toInt(countMap.get("total"));
        // 添加sys_monitor_metric_info id
        /*List<String> idList = new ArrayList<String>() {{
            add("YW1_YSN_Tag75");
            add("YW1_Tag189");
            add("YW1_Tag193");
            add("YW1_Tag201");
            add("YW1_Tag205");
            add("YW1_Tag209");
            add("YW1_Tag212");
            add("YW1_Tag218");
            add("YW1_Tag220");
            add("YW1_Tag197");
            add("YW1_Tag215");
            add("YW1_YSN_Tag24");
            add("YW1_YSN_Tag32");
            add("YW1_YSN_Tag19");
            add("YW1_YSN_Tag31");
            add("YW1_YSN_Tag30");
            add("YW1_YSN_Tag20");
            add("YW1_YSN_Tag22");
            add("YW1_YSN_Tag24");
            add("YW1_YSN_Tag32");
            add("YW1_YSN_Tag19");
            add("YW1_YSN_Tag31");
            add("YW1_YSN_Tag30");
        }};
        String prefixName = "碳源";
        */
        List<String> idList = new ArrayList<String>() {{
            add("YW1_PAC_Tag52");
            add("YW1_PAC_Tag43");
            add("YW1_PAC_Tag44");
            add("YW1_PAC_Tag42");
            add("YW1_PAC_Tag19");
            add("YW1_PAC_Tag21");
            add("YW1_PAC_Tag41");
            add("YW1_PAC_Tag45");
            add("YW1_PAC_Tag42");
            add("YW1_PAC_Tag19");
            add("YW1_PAC_Tag21");
            add("YW1_PAC_Tag41");
            add("YW1_PAC_Tag45");
        }};
        String prefixName = "除磷剂";

        // 查询sys_monitor_metric_info
        StringBuilder stringBuilder = new StringBuilder();
        for (String id : idList) {
            stringBuilder.append(",").append("'").append(id).append("'");
        }
        String ids = stringBuilder.toString().replaceFirst(",", "");
        List<Map<String, Object>> metricInfoMapList = devDB.queryForList("select * from sys_monitor_metric_info where 1=1 and (metric_uid_tag is null or metric_uid_tag = '') and id in (" + ids + ")");
        int index = 10;
        Date nowDate = new Date();
        String nowTime = DateUtils.date2Str(nowDate, BaseConstant.dateFormat);
        for (Map<String, Object> metricInfoMap : metricInfoMapList) {
            // 插入sys_metric_dict
            try {
                String metricName = Convert.toStr(metricInfoMap.get("metric_name"));
                // 过滤metricName空数据
                if (StringUtils.isEmpty(metricName)) {
                    continue;
                }
                // 解析中文
                String id = PinyinUtil.getFirstLetter(prefixName + metricName + " ", "").toUpperCase();
                String metricUnit = Convert.toStr(metricInfoMap.get("metric_unit"));
                String departId = Convert.toStr(metricInfoMap.get("depart_id"));
                int sortNum = maxCount + index;
                // 切换数据源
                DynamicDataSourceContextHolder.push(dataSource);
                transactionTemplate.execute(transactionStatus -> {
                    baseService.insert("sys_metric_dict", new LinkedHashMap<String, Object>() {{
                        put("id", id);
                        put("metricd_name", prefixName + metricName);
                        put("metric_unit", metricUnit);
                        put("metricd_desc", prefixName + metricName);
                        put("metricd_type", "1");
                        put("custom_analysis", "0");
                        put("wht_public", "0");
                        put("depart_id", departId);
                        put("sort_num", sortNum);
                        put("create_by", "sonin" + nowTime);
                        put("create_time", nowDate);
                        put("update_by", "sonin" + nowTime);
                        put("update_time", nowDate);
                        put("create_dept", "A01");
                        put("create_cmpy", "");
                        put("del_flag", "0");
                        put("metricd_classify", null);
                        put("process_code", null);
                    }});
                    devDB.execute("update sys_monitor_metric_info set metric_uid_tag = '" + id + "'" + " where id = '" + metricInfoMap.get("id") + "'");
                    return 1;
                });
                DynamicDataSourceContextHolder.clear();
                index += 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
