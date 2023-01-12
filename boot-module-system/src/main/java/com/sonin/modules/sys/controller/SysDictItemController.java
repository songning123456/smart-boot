package com.sonin.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sonin.api.vo.Result;
import com.sonin.core.mpp.BaseFactory;
import com.sonin.modules.sys.dto.SysDictItemDTO;
import com.sonin.modules.sys.entity.SysDict;
import com.sonin.modules.sys.entity.SysDictItem;
import com.sonin.modules.sys.service.SysDictItemService;
import com.sonin.utils.BeanExtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/10 9:21
 */
@RestController
@RequestMapping("/sys/sysDictItem")
public class SysDictItemController {

    @Autowired
    private SysDictItemService sysDictItemService;

    @GetMapping("/page")
    public Result<Page<SysDictItem>> pageCtrl(SysDictItemDTO sysDictItemDTO) {
        Result<Page<SysDictItem>> result = new Result<>();
        QueryWrapper<SysDictItem> queryWrapper = new QueryWrapper<SysDictItem>()
                .eq("sys_dict_id", sysDictItemDTO.getSysDictId())
                .like(StringUtils.isNotEmpty(sysDictItemDTO.getItemText()), "item_text", sysDictItemDTO.getItemText())
                .like(StringUtils.isNotEmpty(sysDictItemDTO.getItemValue()), "item_value", sysDictItemDTO.getItemValue())
                .orderByAsc("order_num");
        Page<SysDictItem> sysDictItemPage = sysDictItemService.page(new Page<>(sysDictItemDTO.getCurrentPage(), sysDictItemDTO.getPageSize()), queryWrapper);
        result.setResult(sysDictItemPage);
        return result;
    }

    @GetMapping("/dictItem/{dictCode}")
    public Result<List<SysDictItem>> itemCtrl(@PathVariable("dictCode") String dictCode) {
        Result<List<SysDictItem>> result = new Result<>();
        List<SysDictItem> sysDictItemList = new ArrayList<>();
        try {
            List<Map<String, Object>> mapList = BaseFactory.JOIN()
                    .from(SysDict.class)
                    .innerJoin(SysDictItem.class, SysDictItem.class.getDeclaredField("sysDictId"), SysDict.class.getDeclaredField("id"))
                    .where()
                    .eq(true, "sys_dict.dict_code", dictCode)
                    .orderBy(true, true, "sys_dict_item.order_num")
                    .queryForList();
            sysDictItemList = BaseFactory.RESULT().maps2Beans(mapList, SysDictItem.class);
        } catch (Exception e) {
            e.printStackTrace();
            result.error500(e.getMessage());
        }
        result.setResult(sysDictItemList);
        return result;
    }

    @PostMapping("/save")
    public Result saveCtrl(@RequestBody SysDictItemDTO sysDictItemDTO) throws Exception {
        SysDictItem sysDictItem = BeanExtUtils.bean2Bean(sysDictItemDTO, SysDictItem.class);
        sysDictItemService.save(sysDictItem);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result updateCtrl(@RequestBody SysDictItemDTO sysDictItemDTO) throws Exception {
        SysDictItem sysDictItem = BeanExtUtils.bean2Bean(sysDictItemDTO, SysDictItem.class);
        sysDictItemService.updateById(sysDictItem);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    public Result deleteCtrl(@RequestParam(name = "ids") String ids) {
        List<String> sysDictItemIds = Arrays.asList(ids.split(","));
        sysDictItemService.removeByIds(sysDictItemIds);
        return Result.ok();
    }

}
