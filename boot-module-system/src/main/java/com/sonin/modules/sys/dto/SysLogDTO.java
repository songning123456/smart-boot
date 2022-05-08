package com.sonin.modules.sys.dto;

import com.sonin.modules.sys.entity.SysLog;
import lombok.Data;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/8 10:07
 */
@Data
public class SysLogDTO extends SysLog {

    private Long currentPage = 1L;

    private Long pageSize = 10L;

    private String startTime;

    private String endTime;

}
