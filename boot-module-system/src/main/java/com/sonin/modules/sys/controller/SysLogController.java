package com.sonin.modules.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.modules.sys.dto.SysLogDTO;
import com.sonin.modules.sys.entity.SysLog;
import com.sonin.modules.sys.service.SysLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sonin
 * @since 2022-05-05
 */
@RestController
@RequestMapping("/sys/sysLog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @GetMapping("/page")
    public Result<Page<SysLog>> pageCtrl(SysLogDTO sysLogDTO) {
        Result<Page<SysLog>> result = new Result<>();
        QueryWrapper<SysLog> queryWrapper = new QueryWrapper<SysLog>()
                .eq(sysLogDTO.getLogType() != null, "log_type", sysLogDTO.getLogType())
                .like(StringUtils.isNotEmpty(sysLogDTO.getLogIp()), "log_ip", sysLogDTO.getLogIp())
                .eq(sysLogDTO.getLogPort() != null, "log_port", sysLogDTO.getLogIp())
                .eq(StringUtils.isNotEmpty(sysLogDTO.getLogLevel()), "log_level", sysLogDTO.getLogLevel())
                .like(StringUtils.isNotEmpty(sysLogDTO.getThreadName()), "thread_name", sysLogDTO.getThreadName())
                .like(StringUtils.isNotEmpty(sysLogDTO.getLogName()), "log_name", sysLogDTO.getLogName())
                .ge(StringUtils.isNotEmpty(sysLogDTO.getStartTime()), "time_stamp", sysLogDTO.getStartTime())
                .le(StringUtils.isNotEmpty(sysLogDTO.getEndTime()), "time_stamp", sysLogDTO.getEndTime())
                .like(StringUtils.isNotEmpty(sysLogDTO.getMessage()), "message", sysLogDTO.getMessage())
                .orderByDesc("time_stamp");
        Page<SysLog> sysDictPage = sysLogService.page(new Page<>(sysLogDTO.getCurrentPage(), sysLogDTO.getPageSize()), queryWrapper);
        result.setResult(sysDictPage);
        return result;
    }

    @DeleteMapping("/delete")
    public Result deleteCtrl(@RequestParam(name = "ids") String ids) {
        if (StringUtils.isEmpty(ids)) {
            transactionTemplate.execute((transactionStatus -> {
                List<SysLog> sysLogList = sysLogService.list();
                sysLogService.removeByIds(sysLogList.stream().map(SysLog::getId).collect(Collectors.toList()));
                return 1;
            }));
        } else {
            sysLogService.removeByIds(Arrays.asList(ids.split(",")));
        }
        return Result.ok();
    }

}
