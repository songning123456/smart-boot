package com.sonin;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.sonin.utils.PropertyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

    /**
     * <pre>
     * 待修改参数信息
     * </pre>
     * @param null
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private static final Map<String, String> configMap = new HashMap<String, String>() {{
        String ymlSuffix = String.valueOf(PropertyUtils.getCommonYml("application.yml", "spring.profiles.active"));
        String masterDBUrl = String.valueOf(PropertyUtils.getCommonYml("application-" + ymlSuffix + ".yml", "spring.datasource.dynamic.datasource.master.url"));
        String masterDBDriverName = String.valueOf(PropertyUtils.getCommonYml("application-" + ymlSuffix + ".yml", "spring.datasource.dynamic.datasource.master.driver-class-name"));
        String masterDBUsername = String.valueOf(PropertyUtils.getCommonYml("application-" + ymlSuffix + ".yml", "spring.datasource.dynamic.datasource.master.username"));
        String masterDBPassword = String.valueOf(PropertyUtils.getCommonYml("application-" + ymlSuffix + ".yml", "spring.datasource.dynamic.datasource.master.password"));
        put("url", masterDBUrl);
        put("driverName", masterDBDriverName);
        put("username", masterDBUsername);
        put("password", masterDBPassword);
        // todo 待修改：项目模块
        put("globalModuleName", "boot-module-biz");
        // todo 待修改：表名
        put("tableName", "realtimedata");
        // todo 待修改：业务模块
        put("moduleName", "realtimedata");
        put("parent", "com.sonin.modules");
    }};

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        String userDir = System.getProperty("user.dir");
        globalConfig.setOutputDir(userDir + "/" + configMap.get("globalModuleName") + "/src/main/java");
        globalConfig.setAuthor("sonin");
        globalConfig.setOpen(false);
        globalConfig.setServiceName("%sService");
        autoGenerator.setGlobalConfig(globalConfig);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(configMap.get("url"));
        // dsc.setSchemaName("public");
        dataSourceConfig.setDriverName(configMap.get("driverName"));
        dataSourceConfig.setUsername(configMap.get("username"));
        dataSourceConfig.setPassword(configMap.get("password"));
        autoGenerator.setDataSource(dataSourceConfig);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setModuleName(configMap.get("moduleName"));
        packageConfig.setParent(configMap.get("parent"));
        autoGenerator.setPackageInfo(packageConfig);

        // 自定义配置
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return userDir + "/" + configMap.get("globalModuleName") + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        injectionConfig.setFileOutConfigList(focList);
        autoGenerator.setCfg(injectionConfig);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        templateConfig.setXml(null);
        autoGenerator.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(configMap.get("tableName"));
        strategy.setControllerMappingHyphenStyle(true);
        autoGenerator.setStrategy(strategy);
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        autoGenerator.execute();
    }

}