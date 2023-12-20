package com.sonin.modules.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/12/20 15:16
 */
@Data
public class ModelOutDTO {

    private String type = "FeatureCollection";

    private List<ModelInDTO> features = new ArrayList<>();

}
