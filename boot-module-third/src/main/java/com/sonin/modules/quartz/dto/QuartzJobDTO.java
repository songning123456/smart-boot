package com.sonin.modules.quartz.dto;

import com.sonin.modules.quartz.entity.QuartzJob;
import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/3 14:43
 */
@Data
public class QuartzJobDTO extends QuartzJob {

    private Long currentPage = 1L;

    private Long pageSize = 10L;

}
