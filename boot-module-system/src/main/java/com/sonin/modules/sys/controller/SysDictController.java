package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.modules.sys.dto.SysDictDTO;
import com.sonin.modules.sys.entity.SysDict;
import com.sonin.modules.sys.entity.SysDictItem;
import com.sonin.modules.sys.service.SysDictItemService;
import com.sonin.modules.sys.service.SysDictService;
import com.sonin.utils.BeanExtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sonin
 * @since 2022-04-10
 */
@RestController
@RequestMapping("/sys/sysDict")
public class SysDictController {

    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private SysDictItemService sysDictItemService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @GetMapping("/page")
    public Result<Page<SysDict>> pageCtrl(SysDictDTO sysDictDTO) {
        Result<Page<SysDict>> result = new Result<>();
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>()
                .like(StringUtils.isNotEmpty(sysDictDTO.getDictCode()), "dict_code", sysDictDTO.getDictCode())
                .like(StringUtils.isNotEmpty(sysDictDTO.getDictName()), "dict_name", sysDictDTO.getDictName())
                .orderByDesc("update_time");
        Page<SysDict> sysDictPage = sysDictService.page(new Page<>(sysDictDTO.getCurrentPage(), sysDictDTO.getPageSize()), queryWrapper);
        result.setResult(sysDictPage);
        return result;
    }

    @PostMapping("/save")
    public Result saveCtrl(@RequestBody SysDictDTO sysDictDTO) throws Exception {
        SysDict sysDict = BeanExtUtils.bean2Bean(sysDictDTO, SysDict.class);
        sysDictService.save(sysDict);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result updateCtrl(@RequestBody SysDictDTO sysDictDTO) throws Exception {
        SysDict sysDict = BeanExtUtils.bean2Bean(sysDictDTO, SysDict.class);
        sysDictService.updateById(sysDict);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    public Result deleteCtrl(@RequestParam(name = "ids") String ids) {
        List<String> sysDictIds = Arrays.asList(ids.split(","));
        transactionTemplate.execute((transactionStatus -> {
            sysDictService.removeByIds(sysDictIds);
            sysDictItemService.remove(new QueryWrapper<SysDictItem>().in("sys_dict_id", sysDictIds));
            return 1;
        }));
        return Result.ok();
    }

}
