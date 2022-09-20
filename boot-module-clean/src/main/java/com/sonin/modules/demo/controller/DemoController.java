package com.sonin.modules.demo.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.mpp.BaseFactory;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.demo.entity.BaseMeterInfo;
import com.sonin.modules.demo.entity.SyncMeterInfo;
import com.sonin.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/9/20 14:51
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private IBaseService baseService;

    /**
     * <pre>
     * 批量更新base_meter_info中的数据
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @GetMapping("/updateMeterInfo")
    public Result<Object> updateMeterInfoCtrl() throws Exception {
        Result<Object> result = new Result<>();
        // 求出总数
        Map<String, Object> totalMap = BaseFactory.JOIN()
                .select("count(*) as total")
                .from(BaseMeterInfo.class)
                .where()
                .log()
                .selectMap("master");
        long total = Long.parseLong(String.valueOf(totalMap.get("total")));
        int pageSize = 1000;
        long pageCount = (long) Math.ceil(1.0 * total / pageSize);
        // 分页 查询更新
        for (int i = 1; i <= pageCount; i++) {
            try {
                String lastSql = "limit " + (i - 1) * pageSize + " , " + pageSize;
                // 分页查询master库
                List<Map<String, Object>> srcMapList = BaseFactory.JOIN()
                        .select(false, BaseMeterInfo.class)
                        .from(BaseMeterInfo.class)
                        .where()
                        .last(true, lastSql)
                        .log()
                        .selectMaps("master");
                // 获取当前页的meterCode
                List<String> meterCodeList = srcMapList.stream().map(item -> "" + item.get("meterCode")).collect(Collectors.toList());
                // 查询中间库相关meterCode的信息
                List<Map<String, Object>> targetMapList = BaseFactory.JOIN()
                        .select(false, SyncMeterInfo.class)
                        .from(SyncMeterInfo.class)
                        .where()
                        .in(true, SyncMeterInfo::getMeterCode, meterCodeList)
                        .log()
                        .selectMaps("sync");
                // 更新状态
                for (Map<String, Object> src : srcMapList) {
                    String id = "" + src.get("id");
                    String srcMeterCode = "" + src.get("meterCode");
                    for (Map<String, Object> target : targetMapList) {
                        String targetMeterCode = "" + target.get("meterCode");
                        String locType = "" + target.get("locType");
                        String locClassify = "" + target.get("locClassify");
                        String meterCategory = "" + target.get("meterCategory");
                        if (srcMeterCode.equals(targetMeterCode)) {
                            UpdateWrapper<?> updateWrapper = new UpdateWrapper<>();
                            updateWrapper
                                    // loc_type => meter_location
                                    .set("meter_location", locType)
                                    // loc_classify => location_category
                                    .set("location_category", locClassify)
                                    // meter_category => category_id
                                    .set("category_id", meterCategory)
                                    .eq("id", id);
                            baseService.update("base_meter_info", updateWrapper);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
