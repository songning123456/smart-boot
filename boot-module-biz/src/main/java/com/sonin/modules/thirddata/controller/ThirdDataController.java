package com.sonin.modules.thirddata.controller;

import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.thirddata.service.IThirdDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author：sonin
 * @Date：2025/5/16 14:04
 */
@RestController
@RequestMapping("/thirdData")
public class ThirdDataController {

    @Autowired
    private IThirdDataService thirdDataService;

    @GetMapping(value = "/realtime")
    @CustomExceptionAnno(description = "实时数据")
    public Result<Object> realtimeCtrl(@RequestParam Map<String, Object> paramsMap) {
        Result<Object> result = new Result<>();
        thirdDataService.handleRealtimeDataFunc(paramsMap);
        return result;
    }

    @GetMapping(value = "/count")
    @CustomExceptionAnno(description = "小时数据")
    public Result<Object> countCtrl(@RequestParam Map<String, Object> paramsMap) throws Exception {
        Result<Object> result = new Result<>();
        thirdDataService.handleCountDataFunc(paramsMap);
        return result;
    }

}
