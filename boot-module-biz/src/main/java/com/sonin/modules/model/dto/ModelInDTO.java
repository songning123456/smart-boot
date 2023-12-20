package com.sonin.modules.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/12/20 15:16
 */
@Data
public class ModelInDTO {

    private String type = "Feature";

    private Integer id;

    private Map<String, Object> geometry;

    private Map<String, Object> properties;

}
