package com.sonin.modules.data.controller;

import com.sonin.aop.annotation.CustomExceptionAnno;
import com.sonin.core.vo.Result;
import com.sonin.modules.data.entity.DataLog;
import com.sonin.modules.data.service.DataLogService;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 数据日志 前端控制器
 * </p>
 *
 * @author sonin
 * @since 2023-03-24
 */
@RestController
@RequestMapping("/gwt/data/dataLog")
public class DataLogController {

    @Autowired
    private DataLogService dataLogService;

    @PostMapping("/add")
    @CustomExceptionAnno(description = "数据添加")
    public Result<Object> addCtrl(@RequestBody Map<String, Object> paramsMap) throws Exception {
        DataLog dataLog = BeanExtUtils.map2Bean(paramsMap, DataLog.class);
        dataLog.setDataSourceIp(String.join(",", IpUtils.getLocalIPList()));
        dataLogService.save(dataLog);
        return Result.ok();
    }

}
