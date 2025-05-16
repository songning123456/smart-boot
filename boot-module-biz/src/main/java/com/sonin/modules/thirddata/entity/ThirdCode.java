package com.sonin.modules.thirddata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/7/5 10:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdCode {

    // 第三方厂家的sensorId
    private String srcCode;

    // metric_info中的id(nm)
    private String targetCode;

    private String type;

}
