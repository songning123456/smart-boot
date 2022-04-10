package com.sonin.modules.sys.dto;

import com.sonin.modules.sys.entity.SysDictItem;
import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/10 9:14
 */
@Data
public class SysDictItemDTO extends SysDictItem {

    private Long currentPage = 1L;

    private Long pageSize = 10L;

}
