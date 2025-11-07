package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.core.entity.MapDFS;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.DateUtils;
import com.sonin.utils.ExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
@ActiveProfiles("yizhuang")
public class YizhuangBootApplicationTest {

    @Autowired
    private IMPPService baseService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 插入填报数据项
     */
    @Test
    public void insertDataItemTest() {
        List<String> reportNameList = new ArrayList<String>() {{
//             add("南区污水厂物料能耗日报");
//             add("材料使用情况表");
//             add("运行记录报表");
//             add("生产周报");
//             add("化验报表");
//             add("阻垢还原浓度报表");
//            add("滤芯更换再生记录");
//             add("反渗透清洗记录表");
//             add("微滤清洗记录表");
//             add("变电室运行记录表");
//             add("工作交接班记录");
//            add("标厂汇总总表");
//             add("中控运行填报报表");
//            add("中控运行记录表");
//             add("MF清洗报表");
//             add("RO清洗报表");
//            add("能耗日数据");
//            add("能耗日报表(标厂)");
//            add("水质水量日数据(标厂)");
//            add("生产药剂填报报表");
            add("水质水量日数据(污水厂)");
        }};
        Map<String, List<String>> reportName2DataItemListMap = new LinkedHashMap<>();
        reportName2DataItemListMap.put("南区污水厂物料能耗日报", new ArrayList<String>() {{
//            add("4点除磷絮凝剂1#");
//            add("16点除磷絮凝剂1#");
//            add("日除磷絮凝剂1#");
//            add("4点除磷絮凝剂2#");
//            add("16点除磷絮凝剂2#");
//            add("日除磷絮凝剂2#");
//            add("4点除磷絮凝剂3#");
//            add("16点除磷絮凝剂3#");
//            add("日除磷絮凝剂3#");
//            add("4点葡萄糖1#");
//            add("16点葡萄糖1#");
//            add("日葡萄糖1#");
//            add("4点葡萄糖2#");
//            add("16点葡萄糖2#");
//            add("日葡萄糖2#");
//            add("4点葡萄糖3#");
//            add("16点葡萄糖3#");
//            add("日葡萄糖3#");
//            add("4点次氯酸钠反冲洗水池");
//            add("16点次氯酸钠反冲洗水池");
//            add("日次氯酸钠反冲洗水池");
//            add("4点次氯酸钠膜池1#");
//            add("16点次氯酸钠膜池1#");
//            add("日次氯酸钠膜池1#");
//            add("4点次氯酸钠膜池2#");
//            add("16点次氯酸钠膜池2#");
//            add("日次氯酸钠膜池2#");
//            add("4点次氯酸钠膜池3#");
//            add("16点次氯酸钠膜池3#");
//            add("日次氯酸钠膜池3#");
//            add("4点次氯酸钠膜池4#");
//            add("16点次氯酸钠膜池4#");
//            add("日次氯酸钠膜池4#");
//            add("4点柠檬酸钠膜池1#");
//            add("16点柠檬酸钠膜池1#");
//            add("日柠檬酸钠膜池1#");
//            add("4点柠檬酸钠膜池2#");
//            add("16点柠檬酸钠膜池2#");
//            add("日柠檬酸钠膜池2#");
//            add("4点柠檬酸钠膜池3#");
//            add("16点柠檬酸钠膜池3#");
//            add("日柠檬酸钠膜池3#");
//            add("4点柠檬酸钠膜池4#");
//            add("16点柠檬酸钠膜池4#");
//            add("日柠檬酸钠膜池4#");
//            add("4点用电情况401");
//            add("16点用电情况401");
//            add("日用电情况401");
//            add("4点用电情况402");
//            add("16点用电情况402");
//            add("日用电情况402");
//            add("4点用电情况201");
//            add("16点用电情况201");
//            add("日用电情况201");
//            add("4点用电情况202");
//            add("16点用电情况202");
//            add("日用电情况202");
//            add("4点用电情况光伏发电");
//            add("16点用电情况光伏发电");
//            add("日用电情况光伏发电");
            add("脱泥药剂");
            add("PAM");
        }});
        reportName2DataItemListMap.put("材料使用情况表", new ArrayList<String>() {{
            add("日进水量(吨）");
            add("日生产用药剂氢氧化钠使用量");
            add("日生产用药剂氢氧化钠出库单价");
            add("日生产用药剂氢氧化钠金额");
            add("日生产用药剂次氯酸钠使用量");
            add("日生产用药剂次氯酸钠出库单价");
            add("日生产用药剂次氯酸钠金额");
            add("日生产用药剂亚硫酸氢钠使用量");
            add("日生产用药剂亚硫酸氢钠出库单价");
            add("日生产用药剂亚硫酸氢钠金额");
            add("日生产用药剂非氧化杀菌剂使用量");
            add("日生产用药剂非氧化杀菌剂出库单价");
            add("日生产用药剂非氧化杀菌剂钠金额");
            add("日生产用药剂阻垢剂使用量");
            add("日生产用药剂阻垢剂出库单价");
            add("日生产用药剂阻垢剂钠金额");
            add("日生产药剂金额小计");
            add("日清洗用药剂氢氧化钠使用量");
            add("日清洗用药剂氢氧化钠出库单价");
            add("日清洗用药剂氢氧化钠金额");
            add("日清洗用药剂次氯酸钠使用量");
            add("日清洗用药剂次氯酸钠出库单价");
            add("日清洗用药剂次氯酸钠金额");
            add("日清洗用药剂盐酸使用量");
            add("日清洗用药剂盐酸出库单价");
            add("日清洗用药剂盐酸金额");
            add("日清洗用药剂柠檬酸使用量");
            add("日清洗用药剂柠檬酸出库单价");
            add("日清洗用药剂柠檬酸金额");
            add("日清洗用药剂十二烷基苯磺酸钠使用量");
            add("日清洗用药剂十二烷基苯磺酸钠出库单价");
            add("日清洗用药剂十二烷基苯磺酸钠金额");
            add("日清洗用药剂EDTA使用量");
            add("日清洗用药剂EDTA出库单价");
            add("日清洗用药剂EDTA金额");
            add("日清洗用药剂草酸使用量");
            add("日清洗用药剂草酸出库单价");
            add("日清洗用药剂草酸金额");
            add("日洗膜及其他用药剂金额小计");
            add("日滤芯使用量");
            add("日滤芯出库单价");
            add("日滤芯金额");
            add("日滤芯氢氧化钠使用量");
            add("日滤芯氢氧化钠出库单价");
            add("日滤芯氢氧化钠金额");
            add("日滤芯使用金额小计");
            add("日药剂及滤芯使用金额合计");
            add("日产水量（吨）");
        }});
        reportName2DataItemListMap.put("运行记录报表", new ArrayList<String>() {{
            add("风机累计运行时间MAB401");
            add("风机累计运行时间MAB402");
            add("风机累计运行时间MAB403");
            add("风机累计运行时间AB701");
            add("风机累计运行时间AB702");
            add("风机累计运行时间AB703");
            add("风机累计运行时间RB101");
            add("风机累计运行时间RB102");
            add("提升泵运行时间P101A");
            add("提升泵运行时间P101B");
            add("提升泵运行时间P101C");
            add("污泥处置情况板框污泥处理量");
            add("污泥处置情况拉泥车数（车次）");
            add("污泥处置情况PAM使用量");
            add("污泥处置情况PAM剩余量");
            add("污水处理情况进水累计");
            add("污水处理情况出水累计");
            add("污水处理情况外供水累计");
            add("污水处理情况厂区回用水累计");
            add("光伏发电总发电量");
            add("用电情况主配低压401");
            add("用电情况主配低压402");
            add("401+402耗电量");
            add("201+202总耗电量");
            add("201路有功读数总");
            add("201路有功读数峰");
            add("201路有功读数平");
            add("201路有功读数谷");
            add("202路有功读数总");
            add("202路有功读数峰");
            add("202路有功读数平");
            add("202路有功读数谷");
            add("药剂使用情况_多效高分子除磷絮凝剂消耗累计(L)_1#");
            add("药剂使用情况_多效高分子除磷絮凝剂消耗累计(L)_2#");
            add("药剂使用情况_多效高分子除磷絮凝剂消耗累计(L)_3#");
            add("药剂使用情况_葡萄糖消耗累计(L)_1#");
            add("药剂使用情况_葡萄糖消耗累计(L)_2#");
            add("药剂使用情况_葡萄糖消耗累计(L)_3#");
            add("次氯酸钠消毒消耗量（L）_反冲洗水池");
            add("膜池次氯酸钠消耗量（L）_1#");
            add("膜池次氯酸钠消耗量（L）_2#");
            add("膜池次氯酸钠消耗量（L）_3#");
            add("膜池次氯酸钠消耗量（L）_4#");
            add("膜池柠檬酸消耗量（L）_1#");
            add("膜池柠檬酸消耗量（L）_2#");
            add("膜池柠檬酸消耗量（L）_3#");
            add("膜池柠檬酸消耗量（L）_4#");
        }});
        reportName2DataItemListMap.put("生产周报", new ArrayList<String>() {{
            add("日COD总进");
            add("日COD总出");
            add("日BOD5总进");
            add("日BOD5总出");
            add("日SS总进");
            add("日SS总出");
            add("日NH3-N总进");
            add("日NH3-N总出");
            add("日TN总进");
            add("日TN总出");
            add("日TP总进");
            add("日TP总出");
            add("日pH总进");
            add("日pH总出");
            add("日色度总出");
            add("日进水量");
            add("日出水量");
            add("日产泥饼量");
            add("日耗电量");
            add("日吨水电耗");
        }});
        reportName2DataItemListMap.put("化验报表", new ArrayList<String>() {{
            add("MLSS生物池");
            add("MLVSS生物池");
        }});
        reportName2DataItemListMap.put("阻垢还原浓度报表", new ArrayList<String>() {{
            //
//            add("产水量");
//            add("一期产水量");
//            add("二期产水量");
//            add("一期阻垢剂用量");
            add("一期阻垢剂浓度");
//            add("二期阻垢剂用量");
            add("二期阻垢剂浓度");
//            add("一期还原剂用量");
            add("一期还原剂浓度");
//            add("二期还原剂用量");
            add("二期还原剂浓度");
            add("阻垢剂平均");
            add("还原剂平均");
        }});
        reportName2DataItemListMap.put("滤芯更换再生记录", new ArrayList<String>() {{
            add("清洗设备");
            add("再生加碱(kg）");
            add("再生次氯酸钠（kg）");
            add("再生浸泡时间(h)");
            add("更换数量（支）");
            add("执行班组");
            add("R01号");
            add("R02号");
            add("R03号");
            add("R04号");
            add("R05号");
            add("R06号");
            add("R07号");
            add("R08号");
            add("R09号");
            add("R10号");
            add("R11号");
            add("R12号");
        }});
        reportName2DataItemListMap.put("反渗透清洗记录表", new ArrayList<String>() {{
            add("清洗设备套次(10套)");
            add("清洗液PH");
            add("清洗方式");
            add("药剂_液碱");
            add("药剂_EDTA");
            add("药剂_十二烷基苯磺酸钠");
            add("药剂_柠檬酸");
            add("药剂_盐酸");
            add("清洗前压力_一段进水P");
            add("清洗前压力_一段浓水P");
            add("清洗前压力_二段进水P");
            add("清洗前压力_二段浓水P");
            add("清洗后压力_一段进水P");
            add("清洗后压力_一段浓水P");
            add("清洗后压力_二段进水P");
            add("清洗后压力_二段浓水P");
            add("执行班组");
            add("冲洗班组");
        }});
        reportName2DataItemListMap.put("微滤清洗记录表", new ArrayList<String>() {{
            add("清洗设备套次");
            add("清洗方式");
            add("药剂_草酸");
            add("药剂_次氯酸钠");
            add("药剂_液碱");
            add("清洗前产水压力");
            add("清洗前进水P");
            add("清洗前浓水P");
            add("清洗后产水压力");
            add("清洗后进水P");
            add("清洗后浓水P");
            add("记录人");
        }});
        reportName2DataItemListMap.put("变电室运行记录表", new ArrayList<String>() {{
//            add("1#计量柜电量kWh");
//            add("2#计量柜电量kWh");
//            add("401_1#变压器温度");
//            add("401_电量kWh");
//            add("401_总电流（A）");
//            add("401_电压（V）");
//            add("401_电流A");
//            add("401_电流B");
//            add("401_电流C");
//            add("402_2#变压器温度");
//            add("402_电量kWh");
//            add("402_总电流（A）");
//            add("402_电压（V）");
//            add("402_电流A");
//            add("402_电流B");
//            add("402_电流C");
//            add("记录人");
//            add("班长");
            add("生活用电模块");
            add("水源热泵用电量");
            add("综合楼用电量");
        }});
        reportName2DataItemListMap.put("工作交接班记录", new ArrayList<String>() {{
            add("总进水");
            add("总出水");
            add("外供水");
            add("厂区回用水");
            add("光伏发电量");
            add("1#计量柜");
            add("2#计量柜");
            add("葡萄糖消耗");
            add("多效消耗");
            add("次氯酸钠消耗");
            add("柠檬酸消耗");
        }});
        reportName2DataItemListMap.put("标厂汇总总表", new ArrayList<String>() {{
//            add("进水量");
//            add("用电量");
//            add("总产水量");
//            add("一期产水量");
//            add("二期产水量");
//            add("供水量");
//            add("RO1#运行时间");
//            add("RO2#运行时间");
//            add("RO3#运行时间");
//            add("RO4#运行时间");
//            add("RO5#运行时间");
//            add("RO6#运行时间");
//            add("RO7#运行时间");
//            add("RO8#运行时间");
//            add("RO9#运行时间");
//            add("RO10#运行时间");
//            add("总时间");
//            add("生产药剂_次氯酸钠");
//            add("生产药剂_非氧化杀菌剂");
//            add("生产药剂_（一期）阻垢剂");
//            add("生产药剂_（二期）阻垢剂");
//            add("生产药剂_（一期）还原剂");
//            add("生产药剂_（二期）还原剂");
//            add("生产药剂_液碱");
//            add("膜清洗情况_次氯酸钠");
//            add("膜清洗情况_液碱");
//            add("膜清洗情况_草酸");
//            add("膜清洗情况_柠檬酸");
//            add("膜清洗情况_EDTA");
//            add("保安过滤器更换");
//            add("保安过滤器浸泡");
//            add("盐酸");
            add("吨水药剂费");
            add("吨水耗电量");
            add("回收率");
        }});
        reportName2DataItemListMap.put("中控运行填报报表", new ArrayList<String>() {{
//            add("外供水累计");
//            add("厂区回用水累计");
//            add("一期除磷絮凝剂1#泵");
//            add("一期除磷絮凝剂2#泵");
//            add("一期除磷絮凝剂3#泵");
//            add("一期葡萄糖1#泵");
//            add("一期葡萄糖2#泵");
//            add("一期葡萄糖3#泵");
//            add("一期次氯酸钠反冲洗水池");
//            add("一期次氯酸钠膜池1#");
//            add("一期次氯酸钠膜池2#");
//            add("一期次氯酸钠膜池3#");
//            add("一期次氯酸钠膜池4#");
//            add("一期柠檬酸膜池1#");
//            add("一期柠檬酸膜池2#");
//            add("一期柠檬酸膜池3#");
//            add("一期柠檬酸膜池4#");
//            add("二期除磷絮凝剂1#泵");
//            add("二期除磷絮凝剂2#泵");
//            add("二期除磷絮凝剂3#泵");
//            add("二期葡萄糖1#泵");
//            add("二期葡萄糖2#泵");
//            add("二期葡萄糖3#泵");
//            add("二期次氯酸钠反冲洗水池");
//            add("二期次氯酸钠膜池1#");
//            add("二期次氯酸钠膜池2#");
//            add("二期次氯酸钠膜池3#");
//            add("二期次氯酸钠膜池4#");
//            add("二期次氯酸钠膜池5#");
//            add("二期次氯酸钠膜池6#");
//            add("二期柠檬酸膜池1#");
//            add("二期柠檬酸膜池2#");
//            add("二期柠檬酸膜池3#");
//            add("二期柠檬酸膜池4#");
//            add("二期柠檬酸膜池5#");
//            add("二期柠檬酸膜池6#");
//            add("脱泥药剂");
//            add("PAM");
//            add("能耗日报表1#计量柜");
//            add("能耗日报表2#计量柜");
//            add("能耗日报表光伏发电");
//            add("备注");
//            add("接班人");
//            add("班长");
//            add("班次");
//            add("进水累计");
//            add("出水累计");
            add("泥量");
        }});
        reportName2DataItemListMap.put("中控运行记录表", new ArrayList<String>() {{
//            add("一期膜风机运行时间MAB401");
//            add("一期膜风机运行时间MAB402");
//            add("一期膜风机运行时间MAB403");
//            add("二期膜风机运行时间MAB501");
//            add("二期膜风机运行时间MAB502");
//            add("二期膜风机运行时间MAB503");
//            add("一期生物池风机运行时间AB701");
//            add("一期生物池风机运行时间AB702");
//            add("一期生物池风机运行时间AB703");
//            add("二期生物池风机运行时间AB801");
//            add("二期生物池风机运行时间AB802");
//            add("二期生物池风机运行时间AB803");
//            add("曝气风机运行时间RB101");
//            add("曝气风机运行时间RB102");
//            add("一期粗格栅间提升泵运行时间P101A");
//            add("一期粗格栅间提升泵运行时间P101B");
//            add("一期粗格栅间提升泵运行时间P101C");
//            add("二期粗格栅间提升泵运行时间P201A");
//            add("二期粗格栅间提升泵运行时间P201B");
//            add("二期粗格栅间提升泵运行时间P201C");
//            add("进水累计");
//            add("出水累计");
//            add("外供水累计");
//            add("厂区回用水累计");
//            add("光伏发电");
//            add("1#计量柜");
//            add("2#计量柜");
//            add("201+202总耗电量");
//            add("一期除磷絮凝剂使用量合计");
//            add("一期葡萄糖使用量合计");
//            add("一期次氯酸钠使用量合计");
//            add("一期柠檬酸使用量合计");
//            add("二期除磷絮凝剂使用量合计");
//            add("二期葡萄糖使用量合计");
//            add("二期次氯酸钠使用量合计");
//            add("二期柠檬酸使用量合计");
//            add("脱泥药剂");
//            add("PAM");
//            add("备注");
//            add("接班人");
//            add("班长");
//            add("班次");
//            add("泥量");
            add("一期除磷絮凝剂密度");
            add("一期葡萄糖密度");
            add("一期次氯酸钠密度");
            add("一期柠檬酸密度");
            add("二期除磷絮凝剂密度");
            add("二期葡萄糖密度");
            add("二期次氯酸钠密度");
            add("二期柠檬酸密度");
        }});
        reportName2DataItemListMap.put("MF清洗报表", new ArrayList<String>() {{
            add("MF1#");
            add("MF2#");
            add("MF3#");
            add("MF4#");
            add("MF5#");
            add("MF6#");
            add("MF7#");
            add("MF8#");
            add("MF9#");
            add("MF10#");
        }});
        reportName2DataItemListMap.put("RO清洗报表", new ArrayList<String>() {{
            add("RO1#");
            add("RO2#");
            add("RO3#");
            add("RO4#");
            add("RO5#");
            add("RO6#");
            add("RO7#");
            add("RO8#");
            add("RO9#");
            add("RO10#");
        }});
        reportName2DataItemListMap.put("能耗日数据", new ArrayList<String>() {{
            add("外供水累计");
            add("厂区回用水累计");
            add("一期除磷絮凝剂(L)_1#泵");
            add("一期除磷絮凝剂(L)_2#泵");
            add("一期除磷絮凝剂(L)_3#泵");
            add("一期葡萄糖(L)_1#泵");
            add("一期葡萄糖(L)_2#泵");
            add("一期葡萄糖(L)_3#泵");
            add("一期次氯酸钠(L)_反冲洗水池");
            add("一期次氯酸钠(L)_膜池1#");
            add("一期次氯酸钠(L)_膜池2#");
            add("一期次氯酸钠(L)_膜池3#");
            add("一期次氯酸钠(L)_膜池4#");
            add("一期柠檬酸(L)_膜池1#");
            add("一期柠檬酸(L)_膜池2#");
            add("一期柠檬酸(L)_膜池3#");
            add("一期柠檬酸(L)_膜池4#");
            add("二期除磷絮凝剂(L)_1#泵");
            add("二期除磷絮凝剂(L)_2#泵");
            add("二期除磷絮凝剂(L)_3#泵");
            add("二期葡萄糖(L)_1#泵");
            add("二期葡萄糖(L)_2#泵");
            add("二期葡萄糖(L)_3#泵");
            add("二期次氯酸钠(L)_反冲洗水池");
            add("二期次氯酸钠(L)_膜池1#");
            add("二期次氯酸钠(L)_膜池2#");
            add("二期次氯酸钠(L)_膜池3#");
            add("二期次氯酸钠(L)_膜池4#");
            add("二期次氯酸钠(L)_膜池5#");
            add("二期次氯酸钠(L)_膜池6#");
            add("二期柠檬酸(L)_膜池1#");
            add("二期柠檬酸(L)_膜池2#");
            add("二期柠檬酸(L)_膜池3#");
            add("二期柠檬酸(L)_膜池4#");
            add("二期柠檬酸(L)_膜池5#");
            add("二期柠檬酸(L)_膜池6#");
            add("脱泥药剂");
            add("PAM");
            add("能耗日报表_1#计量柜");
            add("能耗日报表_2#计量柜");
            add("能耗日报表_光伏发电");
            add("备注");
            add("接班人");
            add("班长");
            add("班次");
        }});
        reportName2DataItemListMap.put("能耗日报表(标厂)", new ArrayList<String>() {{
            add("次氯酸钠");
            add("用电量");
            add("非氧化杀菌剂");
            add("阻垢剂");
            add("液碱");
            add("盐酸");
            add("亚硫酸氢钠");
            add("氢氧化钠");
            add("柠檬酸");
            add("十二烷基苯磺酸钠");
            add("EDTA");
            add("草酸");
        }});
        reportName2DataItemListMap.put("水质水量日数据(标厂)", new ArrayList<String>() {{
//            add("进水余氯");
//            add("进水流量");
//            add("进水温度");
//            add("进水TOC");
//            add("进水PH");
//            add("进水流量累计值");
//            add("总出水TOC");
//            add("总出水余氯");
//            add("总出水PH");
//            add("总出水管A流量累计值");
//            add("总出水管B流量累计值");
//            add("进水量");
//            add("出水量");
//            add("进水电导");
//            add("出水电导");
            add("出水流量累计值");
        }});
        reportName2DataItemListMap.put("生产药剂填报报表", new ArrayList<String>() {{
//            add("次氯酸钠");
//            add("非氧化杀菌剂");
//            add("（一期）阻垢剂");
//            add("（二期）阻垢剂");
//            add("（一期）还原剂");
//            add("（二期）还原剂");
//            add("液碱");
//            add("盐酸");
        }});
        reportName2DataItemListMap.put("水质水量日数据(污水厂)", new ArrayList<String>() {{
            add("外供水量");
        }});
        long curSec = System.currentTimeMillis() / 1000;
        // 封装结果集
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, List<String>> entry : reportName2DataItemListMap.entrySet()) {
            String reportName = entry.getKey();
            if (!reportNameList.contains(reportName)) {
                continue;
            }
            // 插入数据
            for (String dataName : entry.getValue()) {
                Map<String, Object> entityMap = new HashMap<>();
                String id = ConvertUtils.UUID(reportName + dataName);
                entityMap.put("id", id);
                entityMap.put("data_name", dataName);
                entityMap.put("data_code", id);
                entityMap.put("data_type", "JTGD");
                entityMap.put("data_desc", reportName);
                entityMap.put("depart_id", "af880d6a13404a67825e94bc0f2f3808");
                entityMap.put("text_type", "Input");
                entityMap.put("sort_num", "0");
                entityMap.put("create_by", "sonin");
                entityMap.put("create_time", DateUtils.sec2DateStr(curSec + index, BusinessConstant.DATE_FORMAT));
                entityMap.put("create_dept", "A01");
                entityMap.put("del_flag", "1");
                entityMapList.add(entityMap);
                index++;
            }
        }
        List<List<Map<String, Object>>> partitionList = ListUtils.partition(entityMapList, 1000);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for (List<Map<String, Object>> partition : partitionList) {
                baseService.insertBatch("f_data_item", partition, MPPConstant.REPLACE);
                log.info("批量插入数据项: {}", partition.size());
            }
        });
    }

    @Test
    public void equipmentInfoImportTest() throws Exception {
        // String filePath = "E:\\Company\\kingtrol\\037-亦庄\\亦庄设备台账导入模板v1.xlsx";
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\标厂设备v1.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet0 = workbook.getSheetAt(0);
        // 1. 获取所有字段
        Map<Integer, String> colIndex2ColumnMap = new LinkedHashMap<>();
        Row headRow = sheet0.getRow(1);
        for (int j = 0; j <= headRow.getLastCellNum(); j++) {
            Cell curCell = headRow.getCell(j);
            String cellValue = ConvertUtils.getString(curCell);
            if (StringUtils.isNotEmpty(cellValue)) {
                colIndex2ColumnMap.put(j, cellValue);
            }
        }
        List<Map<String, Object>> equipmentInfoMapList = new ArrayList<>();
        // 2. 从第二行开始，读取数据
        for (int i = 2; i <= sheet0.getLastRowNum(); i++) {
            Row curRow = sheet0.getRow(i);
            // 如果departId为空，则过滤
            if (StringUtils.isEmpty(ConvertUtils.getString(curRow.getCell(1)))) {
                continue;
            }
            Map<String, Object> equipmentInfoMap = new HashMap<>();
            for (int j = 0; j <= curRow.getLastCellNum(); j++) {
                String column = colIndex2ColumnMap.get(j);
                if (column != null && !column.equals("")) {
                    String cellValue = ConvertUtils.getString(curRow.getCell(j));
                    equipmentInfoMap.put(column, cellValue);
                }
            }
            equipmentInfoMapList.add(equipmentInfoMap);
        }
        // 翻译
        // 翻译 表部分
        List<Map<String, Object>> sysDepartMapList = baseService.queryForList("select * from sys_depart", new QueryWrapper<>());
        Map<String, String> sysDepartMap = sysDepartMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("depart_name")), item -> ConvertUtils.getString(item.get("id")), (v1, v2) -> v1));
        List<Map<String, Object>> equipmentCategoryMapList = baseService.queryForList("select id, fid as parentId, des as name from equipment_category", new QueryWrapper<>());
        MapDFS mapDFS = new MapDFS();
        LinkedList<LinkedList<Map<String, Object>>> routeList = mapDFS.getRouteList(mapDFS.buildTree(equipmentCategoryMapList));
        Map<String, String> equipmentCategoryMap = new HashMap<>();
        for (LinkedList<Map<String, Object>> route : routeList) {
            String key = route.stream().map(item -> ConvertUtils.getString(item.get("name"))).collect(Collectors.joining("/"));
            String value = route.stream().map(item -> ConvertUtils.getString(item.get("id"))).collect(Collectors.joining("/"));
            equipmentCategoryMap.put(key, value);
        }
        List<Map<String, Object>> sysStructDictMapList = baseService.queryForList("select * from sys_struct_dict", new QueryWrapper<>());
        Map<String, String> sysStructDictMap = sysStructDictMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("struct_name")), item -> ConvertUtils.getString(item.get("id")), (v1, v2) -> v1));
        // 翻译 字典部分
        String energyLevelDictCode = "energy_level";
        List<Map<String, Object>> energyLevelSysDictMapList = baseService.queryForList("select sys_dict_item.item_text, sys_dict_item.item_value from sys_dict_item left join sys_dict on sys_dict_item.dict_id = sys_dict.id", new QueryWrapper<>().eq("sys_dict.dict_code", energyLevelDictCode));
        Map<String, String> energyLevelSysDictMap = energyLevelSysDictMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("item_text")), item -> ConvertUtils.getString(item.get("item_value")), (v1, v2) -> v1));
        String ynDictCode = "yn";
        List<Map<String, Object>> ynSysDictMapList = baseService.queryForList("select sys_dict_item.item_text, sys_dict_item.item_value from sys_dict_item left join sys_dict on sys_dict_item.dict_id = sys_dict.id", new QueryWrapper<>().eq("sys_dict.dict_code", ynDictCode));
        Map<String, String> ynSysDictMap = ynSysDictMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("item_text")), item -> ConvertUtils.getString(item.get("item_value")), (v1, v2) -> v1));
        String equipmentStatusDictCode = "equipment-equipment_status";
        List<Map<String, Object>> equipmentStatusSysDictMapList = baseService.queryForList("select sys_dict_item.item_text, sys_dict_item.item_value from sys_dict_item left join sys_dict on sys_dict_item.dict_id = sys_dict.id", new QueryWrapper<>().eq("sys_dict.dict_code", equipmentStatusDictCode));
        Map<String, String> equipmentStatusSysDictMap = equipmentStatusSysDictMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("item_text")), item -> ConvertUtils.getString(item.get("item_value")), (v1, v2) -> v1));
        String equipmentLevelDictCode = "equipment-equipment_level";
        List<Map<String, Object>> equipmentLevelSysDictMapList = baseService.queryForList("select sys_dict_item.item_text, sys_dict_item.item_value from sys_dict_item left join sys_dict on sys_dict_item.dict_id = sys_dict.id", new QueryWrapper<>().eq("sys_dict.dict_code", equipmentLevelDictCode));
        Map<String, String> equipmentLevelSysDictMap = equipmentLevelSysDictMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("item_text")), item -> ConvertUtils.getString(item.get("item_value")), (v1, v2) -> v1));
        // 组装数据
        if (!equipmentInfoMapList.isEmpty()) {
            List<Map<String, Object>> equipmentAssetMapList = new ArrayList<>();
            List<Map<String, Object>> equipmentExtMapList = new ArrayList<>();
            for (Map<String, Object> equipmentInfoMap : equipmentInfoMapList) {
                String equipmentCode = ConvertUtils.getString(equipmentInfoMap.get("equipment_code"));
                String infoId = ConvertUtils.UUID(equipmentCode);
                String assetId = ConvertUtils.UUID(equipmentCode + "1");
                String extId = ConvertUtils.UUID(equipmentCode + "2");
                equipmentInfoMap.put("id", infoId);
                equipmentInfoMap.put("asset_id", assetId);
                // 翻译部分
                equipmentInfoMap.put("depart_id", sysDepartMap.get(ConvertUtils.getString(equipmentInfoMap.get("depart_id"))));
                String equipmentType = ConvertUtils.getString(equipmentInfoMap.get("equipment_type"));
                if (StringUtils.isNotEmpty(equipmentType)) {
                    for (String key : equipmentCategoryMap.keySet()) {
                        if (key.contains(equipmentType)) {
                            String value = equipmentCategoryMap.get(key);
                            List<String> keyList = Arrays.asList(key.split("/"));
                            List<String> valueList = Arrays.asList(value.split("/"));
                            int index = findIndex(keyList, equipmentType);
                            LinkedList<String> tmpTypeList = new LinkedList<>();
                            for (int i = 0; i <= index; i++) {
                                tmpTypeList.add(valueList.get(i));
                            }
                            equipmentInfoMap.put("equipment_type", tmpTypeList.getLast());
                            equipmentInfoMap.put("equipment_type_tree_path", String.join(",", tmpTypeList));
                            equipmentInfoMap.put("equipment_top_type", tmpTypeList.getFirst());
                            break;
                        }
                    }
                }
                equipmentInfoMap.put("energy_level", energyLevelSysDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("energy_level"))));
                equipmentInfoMap.put("is_special", ynSysDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("is_special"))));
                equipmentInfoMap.put("is_meterage", ynSysDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("is_meterage"))));
                equipmentInfoMap.put("structures", sysStructDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("structures"))));
                equipmentInfoMap.put("equipment_status", equipmentStatusSysDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("equipment_status"))));
                equipmentInfoMap.put("equipment_level", equipmentLevelSysDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("equipment_level"))));
                equipmentInfoMap.put("structures", sysStructDictMap.get(ConvertUtils.getString(equipmentInfoMap.get("structures"))));
                equipmentInfoMap.put("spare_tag", "0");
                // 设置null
                for (String key : equipmentInfoMap.keySet()) {
                    if (equipmentInfoMap.get(key) != null && "".equals(String.valueOf(equipmentInfoMap.get(key)))) {
                        equipmentInfoMap.put(key, null);
                    }
                }
                // 扩展部分
                Map<String, Object> equipmentAssetMap = new LinkedHashMap<String, Object>() {{
                    put("id", assetId);
                }};
                equipmentAssetMapList.add(equipmentAssetMap);
                Map<String, Object> equipmentExtMap = new LinkedHashMap<String, Object>() {{
                    put("id", extId);
                    put("info_id", infoId);
                }};
                equipmentExtMapList.add(equipmentExtMap);
            }
            transactionTemplate.execute(transactionStatus -> {
                baseService.insertBatch("equipment_info", equipmentInfoMapList, MPPConstant.REPLACE);
                baseService.insertBatch("equipment_asset", equipmentAssetMapList, MPPConstant.REPLACE);
                baseService.insertBatch("equipment_ext", equipmentExtMapList, MPPConstant.REPLACE);
                return 1;
            });
        }
        workbook.close();
        fileInputStream.close();
    }

    /**
     * 查找列表中指定元素的序号（从0开始）
     *
     * @param list   要搜索的列表
     * @param target 要查找的目标元素
     * @return 元素在列表中的索引，未找到则返回-1
     */
    private <T> int findIndex(List<T> list, T target) {
        if (list == null || target == null) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {
            // 使用equals比较，避免空指针异常
            if (target.equals(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Test
    public void reportItemvSyncExcelTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据同步\\报表数据同步v4.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        List<String> sheetNameList = new ArrayList<String>() {{
//            add("能耗日报表");
//            add("中控运行记录");
//            add("汇总报表");
//            add("成本控制表");
//            add("能耗日报表(污水厂)");
//            add("能耗日报表(标厂)");
            add("水务局报表");
        }};
        for (String curSheetName : sheetNameList) {
            XSSFSheet curSheet = workbook.getSheet(curSheetName);
            // 从第1行开始，过滤标题行
            List<Map<String, Object>> entityMapList = new ArrayList<>();
            for (int i = 1; i <= curSheet.getLastRowNum(); i++) {
                Row curRow = curSheet.getRow(i);
                // String srcReportId = ConvertUtils.getString(curRow.getCell(1));
                String srcItemId = ConvertUtils.getString(curRow.getCell(2));
                if (StringUtils.isEmpty(srcItemId)) {
                    continue;
                }
                if (!srcItemId.contains("{")) {
                    srcItemId = "{" + srcItemId + "}";
                }
                // 查询src_report_id
                List<String> tmpSrcItemIdList = ExpressionUtils.parseExpression(srcItemId);
                List<Map<String, Object>> tmpReportIdQueryMapList = baseService.queryForList("select distinct report_id from f_report_item", new QueryWrapper<>().in("id", tmpSrcItemIdList));
                String srcReportId = tmpReportIdQueryMapList.stream().map(item -> ConvertUtils.getString(item.get("report_id"))).collect(Collectors.joining(","));
                String srcDateFormat = ConvertUtils.getString(curRow.getCell(3));
                String srcSyncType = ConvertUtils.getString(curRow.getCell(4));
                String targetItemId = ConvertUtils.getString(curRow.getCell(5));
                String targetDateFormat = ConvertUtils.getString(curRow.getCell(6));
                String syncFlag = ConvertUtils.getString(curRow.getCell(7));
                if (!syncFlag.equals("是")) {
                    continue;
                }
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", targetItemId);
                entityMap.put("src_report_id", srcReportId);
                entityMap.put("src_item_id", srcItemId);
                entityMap.put("src_date_format", srcDateFormat);
                entityMap.put("src_sync_type", srcSyncType);
                entityMap.put("target_item_id", targetItemId);
                entityMap.put("target_date_format", targetDateFormat);
                entityMap.put("create_by", curSheetName);
                entityMapList.add(entityMap);
            }
            if (!entityMapList.isEmpty()) {
                baseService.insertBatch("f_report_itemv_sync", entityMapList, MPPConstant.REPLACE);
            }
        }
    }

    @Test
    public void reportItemvConvertExcelTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据同步\\数据同步PG转Mysql.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        List<String> sheetNameList = new ArrayList<String>() {{
            add("中控运行记录表");
        }};
        for (String curSheetName : sheetNameList) {
            XSSFSheet curSheet = workbook.getSheet(curSheetName);
            // 从第1行开始，过滤标题行
            List<Map<String, Object>> entityMapList = new ArrayList<>();
            for (int i = 1; i <= curSheet.getLastRowNum(); i++) {
                Row curRow = curSheet.getRow(i);
                if (curRow == null) {
                    continue;
                }
                // 源PG数据项ID
                String srcItemId = ConvertUtils.getString(curRow.getCell(1));
                if (StringUtils.isEmpty(srcItemId)) {
                    continue;
                }
                // 目标数据项ID
                String targetItemId = ConvertUtils.getString(curRow.getCell(5));
                // 查询target_report_id
                List<Map<String, Object>> tmpReportIdQueryMapList = baseService.queryForList("select distinct report_id from f_report_item", new QueryWrapper<>().eq("id", targetItemId));
                String targetReportId = tmpReportIdQueryMapList.stream().map(item -> ConvertUtils.getString(item.get("report_id"))).collect(Collectors.joining(","));
                String targetDateFormat = ConvertUtils.getString(curRow.getCell(7));
                String syncFlag = ConvertUtils.getString(curRow.getCell(8));
                if (!syncFlag.equals("是")) {
                    continue;
                }
                // 原始同步类型
                String srcConvertType = ConvertUtils.getString(curRow.getCell(3));
                // 源表类型
                String srcTableType = ConvertUtils.getString(curRow.getCell(2));
                if (StringUtils.isEmpty(srcTableType)) {
                    srcTableType = "count";
                }
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", targetItemId);
                entityMap.put("src_item_id", srcItemId);
                entityMap.put("src_table_type", srcTableType);
                entityMap.put("src_convert_type", srcConvertType);
                entityMap.put("target_item_id", targetItemId);
                entityMap.put("target_report_id", targetReportId);
                entityMap.put("target_date_format", targetDateFormat);
                entityMap.put("create_by", curSheetName);
                entityMapList.add(entityMap);
            }
            Date now = new Date();
            if (!entityMapList.isEmpty()) {
                baseService.insertBatch("f_report_itemv_convert", entityMapList, MPPConstant.INSERT);
            }
        }
    }

    @Test
    public void reportItemvDiffExcelTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据同步\\数据同步(diff).xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        List<String> sheetNameList = new ArrayList<String>() {{
            // add("水质水量日数据(污水厂)");
            // add("中控运行记录表");
            add("能耗日数据");
        }};
        for (String curSheetName : sheetNameList) {
            XSSFSheet curSheet = workbook.getSheet(curSheetName);
            // 从第1行开始，过滤标题行
            List<Map<String, Object>> entityMapList = new ArrayList<>();
            for (int i = 1; i <= curSheet.getLastRowNum(); i++) {
                Row curRow = curSheet.getRow(i);
                if (curRow == null) {
                    continue;
                }
                String srcItemId = ConvertUtils.getString(curRow.getCell(0));
                // 重新解析表达式
                if ("中控运行记录表".equals(curSheetName)) {
                    Map<String, String> src2TargetSrcItemIdMap = new HashMap<>();
                    String[] itemIdArr = srcItemId.split("\\*");
                    // 左侧 体积数据
                    String leftItemId = itemIdArr[0];
                    List<String> leftExpressionIdList = ExpressionUtils.parseExpression(leftItemId);
                    for (String leftExpressionId : leftExpressionIdList) {
                        String key = "\\{" + leftExpressionId + "}";
                        String value = "(\\{val=>next_day 16}-\\{val=>next_day 04})+(\\{val=>cur_day 16}-\\{val=>cur_day 04})";
                        value = value.replaceAll("val", leftExpressionId);
                        src2TargetSrcItemIdMap.put(key, value);
                    }
                    String rightItemId = itemIdArr[1];
                    for (Map.Entry<String, String> entry : src2TargetSrcItemIdMap.entrySet()) {
                        srcItemId = srcItemId.replaceAll(entry.getKey(), entry.getValue());
                    }
                }
                String targetReportId = ConvertUtils.getString(curRow.getCell(1));
                String targetItemId = ConvertUtils.getString(curRow.getCell(2));
                String targetDepartId = ConvertUtils.getString(curRow.getCell(3));
                String updateFlag = ConvertUtils.getString(curRow.getCell(4));
                if (!"是".equals(updateFlag)) {
                    continue;
                }
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", targetItemId);
                entityMap.put("src_item_id", srcItemId);
                entityMap.put("target_item_id", targetItemId);
                entityMap.put("target_report_id", targetReportId);
                entityMap.put("target_depart_id", targetDepartId);
                entityMap.put("create_by", curSheetName);
                entityMapList.add(entityMap);
            }
            Date now = new Date();
            if (!entityMapList.isEmpty()) {
                baseService.insertBatch("f_report_itemv_diff", entityMapList, MPPConstant.INSERT);
            }
        }
    }

    /**
     * 反渗透清洗记录
     */
    @Test
    public void fanshentouqingxiTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据\\反渗透清洗记录-标厂.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet0 = workbook.getSheetAt(0);
        // 解析数据
        Map<String, String> cleaningDeviceMap = new HashMap<String, String>() {{
            put("A#", "1");
            put("B#", "2");
            put("C#", "3");
            put("D#", "4");
            put("E#", "5");
            put("F#", "6");
            put("G#", "7");
            put("H#", "8");
            put("I#", "9");
            put("J#", "10");
        }};
        Map<String, String> cleaningMethodMap = new HashMap<String, String>() {{
            put("碱", "1");
            put("酸", "2");
        }};
        Map<String, String> reitIdMap = new HashMap<String, String>() {{
            put("碱", "1947533247603740673");
            put("EDTA", "1947533238678261761");
            put("柠檬酸", "1947533220256878594");
        }};
        Map<String, LinkedList<String>> usedDevice2TimeMap = new HashMap<>();
        // 标厂高品质再生水厂
        String departId = "1945296425003417600";
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        for (Row row : sheet0) {
            // 清洗设备套次
            String cell1 = ConvertUtils.getString(row.getCell(1));
            if (!cleaningDeviceMap.containsKey(cell1)) {
                continue;
            }
            // 时间
            String cell2 = DateUtils.date2Str(row.getCell(2).getDateCellValue(), BusinessConstant.DATE_FORMAT).substring(0, 10);
            String dataTime = cell2.substring(0, 10) + BusinessConstant.START_TIME_SUFFIX.substring(0, 6);
            if (usedDevice2TimeMap.containsKey(cell1 + "=>" + dataTime.substring(0, 10))) {
                dataTime = DateUtils.date2Str(DateUtils.nextHour(DateUtils.strToDate(usedDevice2TimeMap.get(cell1 + "=>" + dataTime.substring(0, 10)).getLast() + ":00", BusinessConstant.DATE_FORMAT)), BusinessConstant.DATE_FORMAT).substring(0, 16);
            } else {
                usedDevice2TimeMap.put(cell1 + "=>" + dataTime.substring(0, 10), new LinkedList<>());
                usedDevice2TimeMap.get(cell1 + "=>" + dataTime.substring(0, 10)).add(dataTime);
            }
            String finalDataTime = dataTime;
            // 清洗设备套次
            String reitId1 = "1947533274216599553";
            entityMapList.add(new HashMap<String, Object>() {{
                put("id", ConvertUtils.UUID(reitId1 + finalDataTime + departId));
                put("reit_id", reitId1);
                put("item_value", cleaningDeviceMap.get(cell1));
                put("data_id", ConvertUtils.UUID(finalDataTime));
                put("data_time", finalDataTime);
                put("depart_id", departId);
            }});
            // 清洗液PH
            String cell3 = ConvertUtils.getString(row.getCell(3));
            if (StringUtils.isNotEmpty(cell3)) {
                String reitId3 = "1947533265727328257";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId3 + finalDataTime + departId));
                    put("reit_id", reitId3);
                    put("item_value", cell3);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 清洗方式
            String cell4 = ConvertUtils.getString(row.getCell(4));
            if (cleaningMethodMap.containsKey(cell4)) {
                String reitId4 = "1947533255262539777";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId4 + finalDataTime + departId));
                    put("reit_id", reitId4);
                    put("item_value", cleaningMethodMap.get(cell4));
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 加药量
            String cell5 = ConvertUtils.getString(row.getCell(5));
            if (StringUtils.isNotEmpty(cell5)) {
                String[] tmpArr = cell5.split("、");
                for (String item : tmpArr) {
                    String[] tmpItemArr = item.split(" ");
                    String tmpKey = tmpItemArr[0];
                    String tmpValue = tmpItemArr[tmpItemArr.length - 1].replaceAll("kg", "");
                    if (reitIdMap.containsKey(tmpKey)) {
                        entityMapList.add(new HashMap<String, Object>() {{
                            put("id", ConvertUtils.UUID(reitIdMap.get(tmpKey) + finalDataTime + departId));
                            put("reit_id", reitIdMap.get(tmpKey));
                            put("item_value", tmpValue);
                            put("data_id", ConvertUtils.UUID(finalDataTime));
                            put("data_time", finalDataTime);
                            put("depart_id", departId);
                        }});
                    }
                }
            }
            // 清洗前压力（Mpa）
            // 一段进水
            String cell6 = ConvertUtils.getString(row.getCell(6));
            if (StringUtils.isNotEmpty(cell6)) {
                String reitId6 = "1947533185075056641";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId6 + finalDataTime + departId));
                    put("reit_id", reitId6);
                    put("item_value", cell6);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 一段浓水
            String cell7 = ConvertUtils.getString(row.getCell(7));
            if (StringUtils.isNotEmpty(cell7)) {
                String reitId7 = "1947533184806621186";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId7 + finalDataTime + departId));
                    put("reit_id", reitId7);
                    put("item_value", cell7);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 二段进水
            String cell8 = ConvertUtils.getString(row.getCell(8));
            if (StringUtils.isNotEmpty(cell8)) {
                String reitId8 = "1947533184609488898";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId8 + finalDataTime + departId));
                    put("reit_id", reitId8);
                    put("item_value", cell8);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 二段浓水
            String cell9 = ConvertUtils.getString(row.getCell(9));
            if (StringUtils.isNotEmpty(cell9)) {
                String reitId9 = "1947533184416550913";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId9 + finalDataTime + departId));
                    put("reit_id", reitId9);
                    put("item_value", cell9);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 清洗后压力（Mpa）
            // 一段进水
            String cell10 = ConvertUtils.getString(row.getCell(10));
            if (StringUtils.isNotEmpty(cell10)) {
                String reitId10 = "1947533184156504066";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId10 + finalDataTime + departId));
                    put("reit_id", reitId10);
                    put("item_value", cell10);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 一段浓水
            String cell11 = ConvertUtils.getString(row.getCell(11));
            if (StringUtils.isNotEmpty(cell11)) {
                String reitId11 = "1947533183959371777";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId11 + finalDataTime + departId));
                    put("reit_id", reitId11);
                    put("item_value", cell11);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 二段进水
            String cell12 = ConvertUtils.getString(row.getCell(12));
            if (StringUtils.isNotEmpty(cell12)) {
                String reitId12 = "1947533183766433793";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId12 + finalDataTime + departId));
                    put("reit_id", reitId12);
                    put("item_value", cell12);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 二段浓水
            String cell13 = ConvertUtils.getString(row.getCell(13));
            if (StringUtils.isNotEmpty(cell13)) {
                String reitId13 = "1947533183506386945";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId13 + finalDataTime + departId));
                    put("reit_id", reitId13);
                    put("item_value", cell13);
                    put("data_id", ConvertUtils.UUID(finalDataTime));
                    put("data_time", finalDataTime);
                    put("depart_id", departId);
                }});
            }
            // 执行班组
            String cell14 = ConvertUtils.getString(row.getCell(14));
            if (StringUtils.isNotEmpty(cell14)) {
                Map<String, Object> sysUserMap = baseService.queryForMap("select * from sys_user", new QueryWrapper<>().eq("realname", cell14));
                if (sysUserMap != null) {
                    String reitId14 = "1947533183305060353";
                    entityMapList.add(new HashMap<String, Object>() {{
                        put("id", ConvertUtils.UUID(reitId14 + finalDataTime + departId));
                        put("reit_id", reitId14);
                        put("item_value", ConvertUtils.getString(sysUserMap.get("id")));
                        put("data_id", ConvertUtils.UUID(finalDataTime));
                        put("data_time", finalDataTime);
                        put("depart_id", departId);
                    }});
                }
            }
            // 冲洗班组
            String cell15 = ConvertUtils.getString(row.getCell(15));
            if (StringUtils.isNotEmpty(cell15)) {
                Map<String, Object> sysUserMap = baseService.queryForMap("select * from sys_user", new QueryWrapper<>().eq("realname", cell15));
                if (sysUserMap != null) {
                    String reitId15 = "1947533183141482498";
                    entityMapList.add(new HashMap<String, Object>() {{
                        put("id", ConvertUtils.UUID(reitId15 + finalDataTime + departId));
                        put("reit_id", reitId15);
                        put("item_value", ConvertUtils.getString(sysUserMap.get("id")));
                        put("data_id", ConvertUtils.UUID(finalDataTime));
                        put("data_time", finalDataTime);
                        put("depart_id", departId);
                    }});
                }
            }
        }
        baseService.insertBatch("f_report_itemv", entityMapList, MPPConstant.REPLACE);
    }

    /**
     * 滤芯更换再生记录
     */
    @Test
    public void lvxingenghuanzaishengTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据\\保安过滤器滤芯更换记录-标厂.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet0 = workbook.getSheetAt(0);
        // 标厂高品质再生水厂
        String departId = "1945296425003417600";
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        for (Row row : sheet0) {
            // 日期
            String cell0 = ConvertUtils.getString(row.getCell(0));
            if (StringUtils.isEmpty(cell0) || !cell0.contains("20")) {
                continue;
            }
            String dataTime = DateUtils.date2Str(row.getCell(0).getDateCellValue(), BusinessConstant.DATE_FORMAT).substring(0, 16);
            // 时间
            String cell1 = ConvertUtils.getString(row.getCell(1));
            // R01
            String cell2 = ConvertUtils.getString(row.getCell(2));
            if (StringUtils.isNotEmpty(cell2)) {
                String reitId2 = "1950096458978877442";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId2 + dataTime + departId));
                    put("reit_id", reitId2);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell3 = ConvertUtils.getString(row.getCell(3));
            if (StringUtils.isNotEmpty(cell3)) {
                String reitId3 = "1950096458790133762";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId3 + dataTime + departId));
                    put("reit_id", reitId3);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell4 = ConvertUtils.getString(row.getCell(4));
            if (StringUtils.isNotEmpty(cell4)) {
                String reitId4 = "1950096760490614786";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId4 + dataTime + departId));
                    put("reit_id", reitId4);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell5 = ConvertUtils.getString(row.getCell(5));
            if (StringUtils.isNotEmpty(cell5)) {
                String reitId5 = "1950096760297676802";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId5 + dataTime + departId));
                    put("reit_id", reitId5);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell6 = ConvertUtils.getString(row.getCell(6));
            if (StringUtils.isNotEmpty(cell6)) {
                String reitId6 = "1950096760096350210";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId6 + dataTime + departId));
                    put("reit_id", reitId6);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell7 = ConvertUtils.getString(row.getCell(7));
            if (StringUtils.isNotEmpty(cell7)) {
                String reitId7 = "1950096759903412225";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId7 + dataTime + departId));
                    put("reit_id", reitId7);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell8 = ConvertUtils.getString(row.getCell(8));
            if (StringUtils.isNotEmpty(cell8)) {
                String reitId8 = "1950096759706279937";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId8 + dataTime + departId));
                    put("reit_id", reitId8);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell9 = ConvertUtils.getString(row.getCell(9));
            if (StringUtils.isNotEmpty(cell9)) {
                String reitId9 = "1950096759513341954";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId9 + dataTime + departId));
                    put("reit_id", reitId9);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell10 = ConvertUtils.getString(row.getCell(10));
            if (StringUtils.isNotEmpty(cell10)) {
                String reitId10 = "1950096759320403969";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId10 + dataTime + departId));
                    put("reit_id", reitId10);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell11 = ConvertUtils.getString(row.getCell(11));
            if (StringUtils.isNotEmpty(cell11)) {
                String reitId11 = "1950096759056162818";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId11 + dataTime + departId));
                    put("reit_id", reitId11);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            String cell12 = ConvertUtils.getString(row.getCell(12));
            if (StringUtils.isNotEmpty(cell12)) {
                String reitId12 = "1950096758859030530";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId12 + dataTime + departId));
                    put("reit_id", reitId12);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            // R12
            String cell13 = ConvertUtils.getString(row.getCell(13));
            if (StringUtils.isNotEmpty(cell13)) {
                String reitId13 = "1950096758661898242";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId13 + dataTime + departId));
                    put("reit_id", reitId13);
                    put("item_value", "1");
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            // 加碱(kg）
            String cell14 = ConvertUtils.getString(row.getCell(14));
            if (StringUtils.isNotEmpty(cell14)) {
                String reitId14 = "1947488344861515778";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId14 + dataTime + departId));
                    put("reit_id", reitId14);
                    put("item_value", cell14);
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            // 浸泡时间
            String cell15 = ConvertUtils.getString(row.getCell(15));
            if (StringUtils.isNotEmpty(cell15)) {
                String reitId15 = "1947488328654725122";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId15 + dataTime + departId));
                    put("reit_id", reitId15);
                    put("item_value", cell15);
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            // 更换数量
            String cell16 = ConvertUtils.getString(row.getCell(16));
            if (StringUtils.isNotEmpty(cell16)) {
                String reitId16 = "1947488319062351873";
                entityMapList.add(new HashMap<String, Object>() {{
                    put("id", ConvertUtils.UUID(reitId16 + dataTime + departId));
                    put("reit_id", reitId16);
                    put("item_value", cell15);
                    put("data_id", ConvertUtils.UUID(dataTime));
                    put("data_time", dataTime);
                    put("depart_id", departId);
                }});
            }
            // 执行班组
            String cell17 = ConvertUtils.getString(row.getCell(17));
            if (StringUtils.isNotEmpty(cell17)) {
                Map<String, Object> sysUserMap = baseService.queryForMap("select * from sys_user", new QueryWrapper<>().eq("realname", cell17));
                if (sysUserMap != null) {
                    String reitId17 = "1947493440315924482";
                    entityMapList.add(new HashMap<String, Object>() {{
                        put("id", ConvertUtils.UUID(reitId17 + dataTime + departId));
                        put("reit_id", reitId17);
                        put("item_value", ConvertUtils.getString(sysUserMap.get("id")));
                        put("data_id", ConvertUtils.UUID(dataTime));
                        put("data_time", dataTime);
                        put("depart_id", departId);
                    }});
                }
            }
        }
        baseService.insertBatch("f_report_itemv", entityMapList, MPPConstant.REPLACE);
    }

    /**
     * 汇总报表
     */
    @Test
    public void huizongbaobiaoTest() throws Exception {
        String filePath = "E:\\Company\\kingtrol\\037-亦庄\\报表数据\\2025基础数据-标厂v1.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        List<String> sheetNameList = new ArrayList<String>() {{
            add("5月");
            add("6月");
            add("7月");
            add("8月");
        }};
        // 标厂高品质再生水厂
        String departId = "1945296425003417600";
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        // 变量参数
        String curCell;
        Map<Integer, String> index2ReitIdMap = new LinkedHashMap<Integer, String>() {{
            // 进水量
            put(1, "1948221000968269826");
            // 用电量
            put(2, "1948220736861335553");
            // 总产水量
            put(3, "1948220736840364034");
            // 一期产水量
            put(4, "1948220736831975426");
            // 二期产水量
            put(5, "1948220736823586817");
            // 供水量
            put(6, "1948220736815198210");
            // RO1#
            put(7, "1948220736802615297");
            // RO2#
            put(8, "1948220736794226690");
            // RO3#
            put(9, "1948220736785838081");
            // RO4#
            put(10, "1948220736777449474");
            // RO5#
            put(11, "1948220736764866561");
            // RO6#
            put(12, "1948220736748089345");
            // RO7#
            put(13, "1948220736735506434");
            // RO8#
            put(14, "1948220736722923522");
            // RO9#
            put(15, "1948220736714534913");
            // RO10#
            put(16, "1948220736706146306");
            // 总时间
            put(17, "1948220736693563393");
        }};
        for (String sheetName : sheetNameList) {
            Sheet curSheet = workbook.getSheet(sheetName);
            for (Row curRow : curSheet) {
                // 日期
                curCell = ConvertUtils.getString(curRow.getCell(0));
                if (!curCell.startsWith("4")) {
                    continue;
                }
                String dataTime = DateUtils.date2Str(curRow.getCell(0).getDateCellValue(), BusinessConstant.DATE_FORMAT).substring(0, 10).replaceAll("2020", "2025");
                for (Integer index : index2ReitIdMap.keySet()) {
                    String tmpReitId = index2ReitIdMap.get(index);
                    curCell = ConvertUtils.getString(curRow.getCell(index));
                    // R01~R10如果不填，则设置为0
                    if (index >= 7 && index <= 16 && StringUtils.isEmpty(curCell)) {
                        curCell = "0";
                    }
                    if (StringUtils.isNotEmpty(curCell)) {
                        if (curCell.contains("+")) {
                            String[] tmpArr = curCell.split("\\+");
                            if (ConvertUtils.isNumeric(tmpArr[0]) && ConvertUtils.isNumeric(tmpArr[1])) {
                                curCell = ConvertUtils.nPoint(ConvertUtils.getDouble(tmpArr[0], 0D) + ConvertUtils.getDouble(tmpArr[1], 0D), 2);
                            }
                        }
                        String finalCurCell = curCell;
                        entityMapList.add(new HashMap<String, Object>() {{
                            put("id", ConvertUtils.UUID(tmpReitId + dataTime + departId));
                            put("reit_id", tmpReitId);
                            put("item_value", finalCurCell);
                            put("data_id", ConvertUtils.UUID(dataTime));
                            put("data_time", dataTime);
                            put("depart_id", departId);
                        }});
                    }
                }
            }
        }
        baseService.insertBatch("f_report_itemv", entityMapList, MPPConstant.REPLACE);
    }

    /**
     * 更新dataId为秒时间戳的数据
     */
    @Test
    public void updateDataIdTest() {
        // 查询dataId长度为10的数据
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("CHAR_LENGTH(data_id)", 10);
        List<Map<String, Object>> queryMapList = baseService.queryForList("select * from f_report_itemv", queryWrapper0);
        for (Map<String, Object> item : queryMapList) {
            String id = ConvertUtils.getString(item.get("id"));
            String dataTime = ConvertUtils.getString(item.get("data_time"));
            UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
            updateWrapper0.set("data_id", ConvertUtils.UUID(dataTime))
                    .eq("id", id);
            baseService.update("f_report_itemv", updateWrapper0);
        }
    }

    @Test
    public void insertDefaultCountTest() {
        // 请求参数
        String startTimeParam = "2025-01-01 00:00:00";
        String endTimeParam = "2025-09-10 23:59:59";
        List<String> timeList = DateUtils.intervalByHour(startTimeParam, endTimeParam, BusinessConstant.DATE_FORMAT.substring(0, 13));
        String startTs = ConvertUtils.getString(DateUtils.dateStr2Sec(startTimeParam, BusinessConstant.DATE_FORMAT));
        String endTs = ConvertUtils.getString(DateUtils.dateStr2Sec(endTimeParam, BusinessConstant.DATE_FORMAT));
        Map<String, String> assignDictMap = new LinkedHashMap<String, String>() {{
            put("ZERO_", "0");
        }};
        // 查询需要赋值的数据项
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        // 遍历
        for (Map.Entry<String, String> entry : assignDictMap.entrySet()) {
            QueryWrapper<?> assignQueryWrapper = new QueryWrapper<>();
            assignQueryWrapper.likeRight("src_item_id", entry.getKey());
            List<Map<String, Object>> assignQueryMapList = baseService.queryForList("select * from f_report_itemv_convert", assignQueryWrapper);
            List<String> assignNmList = assignQueryMapList.stream().map(item -> ConvertUtils.getString(item.get("src_item_id"))).collect(Collectors.toList());
            if (!assignNmList.isEmpty()) {
                // 查询 已经存在的数据
                QueryWrapper<?> existQueryWrapper = new QueryWrapper<>();
                existQueryWrapper.ge("ts", startTs)
                        .le("ts", endTs)
                        .in("nm", assignNmList);
                List<Map<String, Object>> existQueryMapList = DataSourceTemplate.execute("pg-db", () -> baseService.queryForList("select * from default_count", existQueryWrapper));
                List<String> existNmTsList = existQueryMapList.stream().map(item -> ConvertUtils.getString(item.get("nm")) + "=>" + ConvertUtils.getString(item.get("ts"))).collect(Collectors.toList());
                for (String time : timeList) {
                    String tmpTs = ConvertUtils.getString(DateUtils.dateStr2Sec(time + ":00:00", BusinessConstant.DATE_FORMAT));
                    for (String assignNm : assignNmList) {
                        if (!existNmTsList.contains(assignNm + "=>" + tmpTs)) {
                            Map<String, Object> entityMap = new HashMap<>();
                            entityMap.put("nm", assignNm);
                            entityMap.put("ts", tmpTs);
                            entityMap.put("v", entry.getValue());
                            entityMapList.add(entityMap);
                        }
                    }
                }
            }
        }
        // 插入PG
        if (!entityMapList.isEmpty()) {
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(entityMapList, 100);
            DataSourceTemplate.execute("pg-db", () -> {
                transactionTemplate.execute(transactionStatus -> {
                    for (List<Map<String, Object>> partition : partitionList) {
                        baseService.insertBatch("default_count", partition);
                    }
                    return 1;
                });
                return 1;
            });
        }
    }

}
