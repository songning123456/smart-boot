package com.sonin.modules.thirddata.config;

import com.sonin.modules.thirddata.entity.ThirdCode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/7/5 10:23
 */
@Data
@Component
@ConfigurationProperties(prefix = "third.bean-list")
public class BeanListConfig {

    List<ThirdCode> value;

}
