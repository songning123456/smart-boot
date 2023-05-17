package com.sonin.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * <pre>
 * 解析yml文件
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/17 15:23
 */
public class PropertyUtils {

    /**
    * <pre>
    * <请输入描述信息>
    * </pre>
     * @param propertyName e.g: application-dev.yml
     * @param key e.g: spring.profiles.active
    * @author sonin
    * @Description: TODO(这里描述这个方法的需求变更情况)
    */
    public static Object getCommonYml(String propertyName, Object key){
        ClassPathResource resource = new ClassPathResource(propertyName);
        Properties properties;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties =  yamlFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        assert properties != null;
        return properties.get(key);
    }

}
