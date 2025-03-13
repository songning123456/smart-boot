package com.sonin.modules.freport.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import com.sonin.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：sonin
 * @Date：2025/3/13 14:18
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "biz.scheduled", name = "enable", havingValue = "true")
public class ScheduleJob {

    @Autowired
    private IBaseService baseService;

    /**
     * 报表重复数据 钉钉推送
     */
    @Scheduled(cron = "${biz.scheduled.task00}")
    public void reportDuplicateDingdingTask() {
        // 查询最近一个月的重复数据
        Date now = new Date();
        String dataTime = DateUtils.date2Str(DateUtils.prevMonth(now), BusinessConstant.dateFormat).substring(0, 10);
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0
                .ge("data_time", dataTime)
                .groupBy("concat(reit_id, '=>',factory_id, '=>',data_time)")
                .having("count(*) > 1");
        List<Map<String, Object>> queryMapList0 = baseService.queryForList("select concat(reit_id, '=>',factory_id, '=>',data_time) as key0, count(*) as value0 from f_report_itemv", queryWrapper0);
        // 翻译
        List<Map<String, Object>> sysDepartMapList = baseService.queryForList("select * from sys_depart", new QueryWrapper<>());
        Map<String, String> sysDepartDictMap = sysDepartMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("id")), item -> ConvertUtils.getString(item.get("depart_name"))));
        // 推送钉钉消息
        Set<String> departId2DataTimeSet = queryMapList0.stream().map(item -> {
            String[] key0Array = ConvertUtils.getString(item.get("key0")).split("=>");
            return key0Array[1] + "=>" + key0Array[2];
        }).collect(Collectors.toSet());
        for (String departId2DataTime : departId2DataTimeSet) {
            String[] departId2DataTimeArr = departId2DataTime.split("=>");
            Map<String, Object> innerParamMap = new HashMap<>();
            innerParamMap.put("content", sysDepartDictMap.get(departId2DataTimeArr[0]) + "在" + departId2DataTimeArr[1] + "存在重复数据，请删除");
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("msgtype", "text");
            paramMap.put("text", innerParamMap);
            String resStr = HttpUtils.doPost("https://oapi.dingtalk.com/robot/send?access_token=73f294d1ec744b73d7c6f260dd738983a48db7127edd3ac8d4bc78f9d8fa1018", paramMap);
            log.info("钉钉推送结果：{}", resStr);
        }
    }
}
