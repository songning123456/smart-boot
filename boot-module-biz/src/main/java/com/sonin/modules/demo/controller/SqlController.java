package com.sonin.modules.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.core.vo.Result;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * @version 1.0 2024/1/12 15:33
 */
@Slf4j
@RestController
@RequestMapping("/sql")
public class SqlController {

    @Autowired
    private IBaseService baseService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @GetMapping(value = "/syncData")
    @CustomExceptionAnno(description = "同步数据")
    public Result<Object> syncDataCtrl(@RequestParam Map<String, Object> paramsMap) {
        Result<Object> result = new Result<>();
        String pageSize = ConvertUtils.getString(paramsMap.getOrDefault("pageSize", "1000"));
        String startTime = ConvertUtils.getString(paramsMap.get("startTime"));
        String endTime = ConvertUtils.getString(paramsMap.get("endTime"));
        String srcTableName = "ten_minute_count2023";
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            result.error500("请输入时间范围");
        } else {
            List<String> timeList = DateUtils.intervalByMonth(startTime, endTime, BusinessConstant.DATE_FORMAT.substring(0, 7));
            for (String time : timeList) {
                String startTs = ConvertUtils.getString(DateUtils.dateStr2Sec(time + "-01 00:00:00", BusinessConstant.DATE_FORMAT));
                int days = DateUtils.lengthOfSomeMonth(Integer.parseInt(time.split("-")[0]), Integer.parseInt(time.split("-")[1]));
                String endTs = ConvertUtils.getString(DateUtils.dateStr2Sec(time + "-" + days + " 23:59:59", BusinessConstant.DATE_FORMAT));
                String targetTableName = "ten_minute_count" + time.replaceAll("-", "");
                while (true) {
                    QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
                    queryWrapper0.ge("ts", startTs)
                            .le("ts", endTs)
                            .last("limit " + pageSize);
                    List<Map<String, Object>> queryMapList = DataSourceTemplate.execute("pg-db", () -> baseService.queryForList("select * from " + srcTableName, queryWrapper0));
                    if (queryMapList.isEmpty()) {
                        break;
                    }
                    log.info("同步{}数据开始!", time);
                    List<Integer> idList = queryMapList.stream().map(item -> Integer.parseInt(ConvertUtils.getString(item.get("id")))).collect(Collectors.toList());
                    DataSourceTemplate.execute("pg-db", () -> {
                        transactionTemplate.execute(transactionStatus -> {
                            baseService.insertBatch(targetTableName, queryMapList);
                            baseService.delete(srcTableName, new QueryWrapper<>().in("id", idList));
                            return 1;
                        });
                        return 1;
                    });
                }
                log.info("同步{}数据结束!", time);
            }
        }
        return result;
    }

}
