package com.sonin.modules.data.controller;

import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.data.service.IDataBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author sonin
 * @Date 2025/11/28 11:28
 */
@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private IDataBusinessService dataBusinessService;

    @PostMapping("/addBatch")
    @CustomExceptionAnno(description = "批量添加数据")
    public Result<Object> addBatchCtrl(@RequestBody List<Map<String, Object>> dataMapList) {
        String resMessage = dataBusinessService.handleRealtimeDataFunc(dataMapList);
        return Result.ok(resMessage);
    }

    @GetMapping("/aggData")
    @CustomExceptionAnno(description = "聚合实时数据")
    public Result<Object> aggDataCtrl(@RequestParam Map<String, Object> paramsMap) {
        dataBusinessService.handleAggDataFunc(paramsMap);
        return Result.ok();
    }

}
