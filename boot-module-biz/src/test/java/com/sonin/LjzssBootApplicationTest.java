package com.sonin;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.core.constant.BusinessConstant;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.CoordinateConverter;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DistanceCalculatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.io.IOException;
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
@ActiveProfiles("ljzss")
@SpringBootTest(classes = BootApplication.class)
public class LjzssBootApplicationTest {

    @Autowired
    private IMPPService mppService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 001：同步管井管线数据
     * 002：执行完以上操作后，用坐标转换工具转换经纬度。（select id, original_longitude, original_latitude from pipenetwork_tubewell_20250923bak）
     * 003: 修改经纬度信息（84+火星）
     * 004：修改图片信息
     * 005：数据同步到目标表中（pipenetwork_tubewell 和 pipenetwork_pipeline）
     */
    @Test
    public void syncPipeTest() {
        // 表后缀
        String tableNameSuffix = "_20250923bak";
        List<String> fullFileNameList = new ArrayList<String>() {{
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\002\\中水系统管网导入点线表段永清合表v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\002\\中水系统管网导入点线表功法合表v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\002\\中水系统管网导入点线表汉腾v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\002\\中水系统管网导入点线表和立均合表v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\003\\错误坐标点v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\004\\DN1000中水管管道数据导入点线表v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\005\\中水系统管网导入点线表v1.xlsx");
            // add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\006\\中水系统管网导入点线表青龙河v1.xlsx");
            add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\007\\1中水系统管网导入点线表-段永清合表_v1.xlsx");
            add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\007\\中水系统管网导入点线表功法合表_v1.xlsx");
            add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\007\\中水系统管网导入点线表汉腾_v1.xlsx");
            add("E:\\Company\\kingtrol\\035-丽江再生水\\管井管线20250710\\007\\中水系统管网导入点线表和立均_v1.xlsx");
        }};
        // 查询所有的机构信息
        List<Map<String, Object>> sysDepartMapList = mppService.queryForList("select * from sys_depart", new QueryWrapper<>());
        Map<String, String> sysDepartDictMap = sysDepartMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("depart_name")), item -> ConvertUtils.getString(item.get("id"))));
        Date now = new Date();
        // 遍历所有的excel文件
        for (String fullFileName : fullFileNameList) {
            String[] fileNameArr = fullFileName.split("\\\\");
            String fileName = fileNameArr[fileNameArr.length - 1];
            try (FileInputStream file = new FileInputStream(fullFileName)) {
                Workbook workbook;
                // 根据文件扩展名判断Excel版本
                if (fullFileName.toLowerCase().endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(file);
                } else if (fullFileName.toLowerCase().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(file);
                } else {
                    log.error("错误：不支持的文件格式，请提供.xlsx或.xls文件");
                    continue;
                }
                // 获取第0个工作表:管井
                Sheet sheet0 = workbook.getSheetAt(0);
                List<Map<String, Object>> pipeList0 = new ArrayList<>();
                int index0 = 0;
                // 遍历所有行
                for (Row row : sheet0) {
                    String id = ConvertUtils.getString(row.getCell(0));
                    // 从第2行开始
                    if (index0 < 2 || StringUtils.isEmpty(id)) {
                        index0++;
                        continue;
                    }
                    Map<String, Object> tmpMap = new HashMap<>();
                    // 起始点号
                    tmpMap.put("id", id);
                    tmpMap.put("tubewell_code", id);
                    tmpMap.put("tubewell_name", id);
                    // 节点:弯头/三通/四通/其他测绘点
                    tmpMap.put("node", ConvertUtils.getString(row.getCell(1)));
                    // 泵房
                    tmpMap.put("pump_room", ConvertUtils.getString(row.getCell(2)));
                    // 阀门类型（普通闸阀/软密闸阀/蝶阀/减压阀/排气阀/排泥阀/智能取水终端（取水栓/取水箱））
                    tmpMap.put("valve_type", ConvertUtils.getString(row.getCell(3)));
                    // 水表类型（流量计（电磁/超声波）/计费表）
                    tmpMap.put("meter_type", ConvertUtils.getString(row.getCell(4)));
                    // X
                    String originalLongitude = ConvertUtils.getString(row.getCell(5));
                    tmpMap.put("original_longitude", originalLongitude);
                    // Y
                    String originalLatitude = ConvertUtils.getString(row.getCell(6));
                    tmpMap.put("original_latitude", originalLatitude);
                    // 口径（mm）
                    tmpMap.put("tubewell_diameter", ConvertUtils.getString(row.getCell(7)));
                    // 地面高程(m)
                    tmpMap.put("ground_elevation", ConvertUtils.getString(row.getCell(8)));
                    // 埋深(m)
                    tmpMap.put("tubewell_depth", ConvertUtils.getString(row.getCell(9)));
                    // 水表ID号
                    tmpMap.put("meter_no", ConvertUtils.getString(row.getCell(10)));
                    // 数据采集终端编号
                    tmpMap.put("terminal_no", ConvertUtils.getString(row.getCell(11)));
                    // 阀门开启度
                    tmpMap.put("valve_open_degree", ConvertUtils.getString(row.getCell(12)));
                    // 设备厂家
                    tmpMap.put("design_unit_person", ConvertUtils.getString(row.getCell(13)));
                    // 安装单位
                    tmpMap.put("build_unit_person", ConvertUtils.getString(row.getCell(14)));
                    // 敷设年代
                    tmpMap.put("finish_time", DateUtils.strToDate("2024-01-01 00:00:00", BusinessConstant.DATE_FORMAT));
                    // 区划名称（所属行政区划）
                    tmpMap.put("pro_county", ConvertUtils.getString(row.getCell(16)));
                    // 所在道路名称
                    tmpMap.put("pro_road", ConvertUtils.getString(row.getCell(17)));
                    // 井盖材质
                    tmpMap.put("manhole_material", ConvertUtils.getString(row.getCell(18)));
                    // 井盖形状
                    tmpMap.put("manhole_type", ConvertUtils.getString(row.getCell(19)));
                    // 井室尺寸
                    tmpMap.put("manhole_size", ConvertUtils.getString(row.getCell(20)));
                    // 所属分区
                    tmpMap.put("depart_id", sysDepartDictMap.get(ConvertUtils.getString(row.getCell(21)) + "分区"));
                    // 现场照片（管井外貌、周围建筑物各至少一张，可单独提供，照片名称按管井编号命名）
                    // 备注
                    tmpMap.put("remark", ConvertUtils.getString(row.getCell(23)));
                    tmpMap.put("create_by", fileName);
                    tmpMap.put("create_time", now);
                    pipeList0.add(tmpMap);
                    index0++;
                }
                // 获取第1个工作表:管线
                Sheet sheet1 = workbook.getSheetAt(1);
                List<Map<String, Object>> pipeList1 = new ArrayList<>();
                int index1 = 0;
                // 遍历所有行
                for (Row row : sheet1) {
                    // 从第1行开始
                    if (index1 < 1) {
                        index1++;
                        continue;
                    }
                    Map<String, Object> tmpMap = new HashMap<>();
                    // 起点编号
                    String startCode = ConvertUtils.getString(row.getCell(1));
                    // 终点编号
                    String endCode = ConvertUtils.getString(row.getCell(2));
                    tmpMap.put("id", startCode + "," + endCode);
                    tmpMap.put("pipeline_code", startCode + "," + endCode);
                    tmpMap.put("pipeline_name", startCode + "," + endCode);
                    // 口径(mm)
                    tmpMap.put("pipeline_diameter", ConvertUtils.getString(row.getCell(3)));
                    // 管道材质
                    tmpMap.put("pipeline_material", ConvertUtils.getString(row.getCell(4)));
                    // 管道颜色
                    tmpMap.put("pipeline_color", ConvertUtils.getString(row.getCell(5)));
                    // 管材厂家
                    tmpMap.put("design_unit_person", ConvertUtils.getString(row.getCell(6)));
                    // 管壁厚度（mm）
                    tmpMap.put("pipeline_thickness", ConvertUtils.getString(row.getCell(7)));
                    // 埋深(m)
                    tmpMap.put("inlet_buried_depth", ConvertUtils.getString(row.getCell(8)));
                    tmpMap.put("effluent_buried_depth", ConvertUtils.getString(row.getCell(8)));
                    // 地面高程(m)
                    tmpMap.put("inlet_ground_elevation", ConvertUtils.getString(row.getCell(9)));
                    tmpMap.put("effluent_ground_elevation", ConvertUtils.getString(row.getCell(9)));
                    // 敷设年代
                    tmpMap.put("built_time", DateUtils.strToDate("2024-01-01 00:00:00", BusinessConstant.DATE_FORMAT));
                    // 区划名称（所属行政区划）
                    tmpMap.put("pro_county", ConvertUtils.getString(row.getCell(11)));
                    // 所在道路名称
                    tmpMap.put("pro_road", ConvertUtils.getString(row.getCell(12)));
                    // 分区
                    tmpMap.put("depart_id", sysDepartDictMap.get(ConvertUtils.getString(row.getCell(13)) + "分区"));
                    // 安装单位
                    tmpMap.put("construction_unit", ConvertUtils.getString(row.getCell(14)));
                    // 管线类型
                    tmpMap.put("pipeline_type", ConvertUtils.getString(row.getCell(15)));
                    // 备注
                    tmpMap.put("remark", ConvertUtils.getString(row.getCell(16)));
                    tmpMap.put("create_by", fileName);
                    tmpMap.put("create_time", now);
                    pipeList1.add(tmpMap);
                    index1++;
                }
                // 关闭工作簿
                workbook.close();
                // 插入数据
                transactionTemplate.execute(status -> {
                    // 插入管井数据
                    if (!pipeList0.isEmpty()) {
                        List<List<Map<String, Object>>> partition0 = ListUtils.partition(pipeList0, 100);
                        partition0.forEach(item -> mppService.insertBatch("pipenetwork_tubewell" + tableNameSuffix, item, MPPConstant.REPLACE));
                    }
                    // 插入管线数据
                    if (!pipeList1.isEmpty()) {
                        List<List<Map<String, Object>>> partition1 = ListUtils.partition(pipeList1, 100);
                        partition1.forEach(item -> mppService.insertBatch("pipenetwork_pipeline" + tableNameSuffix, item, MPPConstant.REPLACE));
                    }
                    return 1;
                });
                // 延迟5s
                Thread.sleep(5000);
            } catch (IOException e) {
                log.error("错误：文件读取失败 - {}", e.getMessage());
            } catch (Exception e) {
                log.error("错误：处理Excel文件时发生异常 - {}", e.getMessage());
            }
        }
    }

    /**
     * 003 坐标转换
     */
    @Test
    public void zuobiaoConvertTest() {
        String fileName = "E:\\Company\\kingtrol\\035-丽江再生水\\坐标转换\\13.xlsx";
        String tableNameSuffix = "_20250923bak";
        try (FileInputStream file = new FileInputStream(fileName)) {
            Workbook workbook;
            // 根据文件扩展名判断Excel版本
            if (fileName.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file);
            } else if (fileName.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(file);
            } else {
                log.error("错误：不支持的文件格式，请提供.xlsx或.xls文件");
                return;
            }
            Sheet sheet0 = workbook.getSheetAt(0);
            for (Row row : sheet0) {
                String id = ConvertUtils.getString(row.getCell(0));
                String data = ConvertUtils.getString(row.getCell(5));
                String[] dataArr = data.split(",");
                if (dataArr.length < 3) {
                    continue;
                }
                String longitude = dataArr[2];
                String[] longitudeArr = longitude.split(":");
                double proLongitude = Double.parseDouble(longitudeArr[0]) + Double.parseDouble(longitudeArr[1]) / 60 + Double.parseDouble(longitudeArr[2]) / 3600;
                String latitude = dataArr[1];
                String[] latitudeArr = latitude.split(":");
                double proLatitude = Double.parseDouble(latitudeArr[0]) + Double.parseDouble(latitudeArr[1]) / 60 + Double.parseDouble(latitudeArr[2]) / 3600;
                // 转换成 火星坐标系
                CoordinateConverter.Coordinate marsPosition = CoordinateConverter.wgs84ToGcj02(proLatitude, proLongitude);
                UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                updateWrapper0.set("pro_longitude", proLongitude)
                        .set("pro_latitude", proLatitude)
                        .set("mars_latitude", marsPosition.getLatitude())
                        .set("mars_longitude", marsPosition.getLongitude())
                        .eq("id", id);
                mppService.update("pipenetwork_tubewell" + tableNameSuffix, updateWrapper0);
            }
        } catch (Exception e) {
            log.error("错误：处理Excel文件时发生异常 - {}", e.getMessage());
        }
    }

    /**
     * 004 递归读取图片
     */
    @Test
    public void recursionReadImageTest() {
        // 表后缀
        String tableNameSuffix = "_20250923bak";
        // 要搜索的文件夹路径，可以根据需要修改
//        String folderPath = "E:\\Company\\kingtrol\\035-丽江再生水\\20250923资料";
        String folderPath = "E:\\Company\\kingtrol\\035-丽江再生水\\20250926资料";
        File folder = new File(folderPath);
        // 检查文件夹是否存在且是一个目录
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定的路径不存在或不是一个文件夹: " + folderPath);
            return;
        }
        System.out.println("在 " + folderPath + " 中找到的照片:");
        System.out.println("-----------------------------------");
        List<String> imageNameList = new ArrayList<>();
        // 递归查找所有图片文件
        findImageFiles(folder, imageNameList);
        // 存入照片信息
        for (String imageName : imageNameList) {
            String tmpImageName = "files/pipe/" + imageName;
            String[] imageNameArr = imageName.split("_");
            // 管井ID
            String tmpTubewellId = imageNameArr[0];
            // 查询当前管井照片
            Map<String, Object> tubewellMap = mppService.queryForMap("select * from pipenetwork_tubewell", new QueryWrapper<>().eq("id", tmpTubewellId));
            if (tubewellMap == null) {
                continue;
            }
            String tmpImage = ConvertUtils.getString(tubewellMap.get("other_imgs"));
            Set<String> tmpImageSet = new HashSet<>();
            if (!StringUtils.isEmpty(tmpImage)) {
                tmpImageSet.addAll(new HashSet<>(Arrays.asList(tmpImage.split(","))));
            }
            tmpImageSet.add(tmpImageName);
            UpdateWrapper<?> tmpUpdateWrapper = new UpdateWrapper<>();
            tmpUpdateWrapper.set("photo", String.join(",", tmpImageSet))
                    .eq("id", tmpTubewellId);
            mppService.update("pipenetwork_tubewell" + tableNameSuffix, tmpUpdateWrapper);
        }
    }

    /**
     * 递归查找文件夹中的所有图片文件
     *
     * @param file 要搜索的文件或文件夹
     */
    private void findImageFiles(File file, List<String> imageNameList) {
        // 如果是目录，则递归查找其子文件和子目录
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            // 确保目录可访问
            if (files != null) {
                for (File subFile : files) {
                    findImageFiles(subFile, imageNameList);
                }
            }
        } else if (file.isFile() && isImageFile(file)) {
            // 如果是文件，检查是否为图片
            // 输出图片文件的名称
            System.out.println(file.getName());
            imageNameList.add(file.getName());
        }
    }

    /**
     * 判断文件是否为图片文件
     *
     * @param file 要判断的文件
     * @return 如果是图片文件则返回true，否则返回false
     */
    private static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        // 定义常见的图片文件扩展名
        List<String> imageSuffixList = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff");
        for (String extension : imageSuffixList) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 004 数据同步到目标表中（pipenetwork_tubewell 和 pipenetwork_pipeline）
     */
    @Test
    public void syncPipe2TargetTest() {
        // 表后缀
        String tableNameSuffix = "_20250923bak";
        List<Map<String, Object>> tubewellMapList = mppService.queryForList("select * from pipenetwork_tubewell" + tableNameSuffix, new QueryWrapper<>());
        List<Map<String, Object>> pipelineMapList = mppService.queryForList("select * from pipenetwork_pipeline" + tableNameSuffix, new QueryWrapper<>());
        if (!tubewellMapList.isEmpty()) {
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(tubewellMapList, 100);
            transactionTemplate.execute(transactionStatus -> {
                partitionList.forEach(partition -> mppService.insertBatch("pipenetwork_tubewell", partition, MPPConstant.REPLACE));
                return 1;
            });
        }
        if (!pipelineMapList.isEmpty()) {
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(pipelineMapList, 100);
            transactionTemplate.execute(transactionStatus -> {
                partitionList.forEach(partition -> mppService.insertBatch("pipenetwork_pipeline", partition, MPPConstant.REPLACE));
                return 1;
            });
        }
    }

    @Test
    public void wgs84ToGcj02Test() {
//        String tableName = "pipenetwork_tubewell";
        QueryWrapper<?> queryWrapper0 = new QueryWrapper<>();
        // queryWrapper0.in("id", Arrays.asList("DK1+736.737B", "DK2+097.912B", "DYLQ+769.789"));
        queryWrapper0.apply("(mars_latitude is null or mars_latitude = '')");
        String tableName = "sys_factory_info";
        // 管井
        List<Map<String, Object>> queryMapList0 = mppService.queryForList("select * from " + tableName, queryWrapper0);
        for (Map<String, Object> item : queryMapList0) {
            String id = ConvertUtils.getString(item.get("id"));
            double proLatitude = ConvertUtils.getDouble(item.get("pro_latitude"), 0D);
            double proLongitude = ConvertUtils.getDouble(item.get("pro_longitude"), 0D);
            if (proLatitude == 0D || proLongitude == 0D) {
                continue;
            }
            // WGS84 转 GCJ02
            CoordinateConverter.Coordinate gcjCoord = CoordinateConverter.wgs84ToGcj02(proLatitude, proLongitude);
            UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
            updateWrapper0.set("mars_latitude", gcjCoord.getLatitude())
                    .set("mars_longitude", gcjCoord.getLongitude())
                    .eq("id", id);
            mppService.update(tableName, updateWrapper0);
        }
    }

    @Test
    public void calLengthFunc() {
        // 1. 管线总里程
        // 查询所有的管井
        List<Map<String, Object>> pipeMapList = mppService.queryForList("select * from pipenetwork_tubewell", new QueryWrapper<>());
        Map<String, Double[]> pipeId2InfoMap = pipeMapList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("id")), item -> new Double[]{ConvertUtils.getDouble(item.get("pro_longitude"), 0D), ConvertUtils.getDouble(item.get("pro_latitude"), 0D)}));
        // 查询所有的管线信息
        List<Map<String, Object>> pipeLineMapList = mppService.queryForList("select * from pipenetwork_pipeline_20250926v2", new QueryWrapper<>());
        for (Map<String, Object> pipeLineMap : pipeLineMapList) {
            String id = ConvertUtils.getString(pipeLineMap.get("id"));
            if (id.contains(",")) {
                String[] tmpIdArr = id.split(",");
                if (tmpIdArr.length == 2) {
                    String startPipeId = tmpIdArr[0];
                    String endPipeId = tmpIdArr[1];
                    boolean successFlag = pipeId2InfoMap.containsKey(startPipeId) && pipeId2InfoMap.containsKey(endPipeId) && pipeId2InfoMap.get(startPipeId)[0] != 0D && pipeId2InfoMap.get(startPipeId)[1] != 0D && pipeId2InfoMap.get(endPipeId)[0] != 0D && pipeId2InfoMap.get(endPipeId)[1] != 0D;
                    if (successFlag) {
                        // 计算管线长度
                        double tmpDistance = DistanceCalculatorUtils.calculateDistance(pipeId2InfoMap.get(startPipeId)[1], pipeId2InfoMap.get(startPipeId)[0], pipeId2InfoMap.get(endPipeId)[1], pipeId2InfoMap.get(endPipeId)[0]);
                        UpdateWrapper<?> updateWrapper0 = new UpdateWrapper<>();
                        updateWrapper0.set("length", tmpDistance)
                                .eq("id", id);
                        mppService.update("pipenetwork_pipeline_20250926v2", updateWrapper0);
                    }

                }
            }
        }
    }

    /**
     * 批量添加第三方报警配置
     */
    @Test
    public void addThirdAlarmConfigTest() {
        // 查询所有报警类型
        QueryWrapper<?> alarmTypeQueryWrapper = new QueryWrapper<>();
        alarmTypeQueryWrapper.isNotNull("alarm_type_value");
        List<Map<String, Object>> alarmTypeMapList = mppService.queryForList("select * from third_alarm_type", alarmTypeQueryWrapper);
        // 遍历
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        for (Map<String, Object> item : alarmTypeMapList) {
            String alarmTypeId = ConvertUtils.getString(item.get("id"));
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("id", ConvertUtils.UUID(alarmTypeId));
            entityMap.put("alarm_type_id", alarmTypeId);
            entityMap.put("alarm_level", "T001");
            entityMap.put("send_flag", "1");
            entityMap.put("alarm_start_time", "00");
            entityMap.put("alarm_end_time", "23");
            entityMapList.add(entityMap);
        }
        mppService.insertBatch("third_alarm_config", entityMapList);
    }

    @Test
    public void insertMapDataTest() {
        String srcDataStr = "{\n" +
                "    \"type\": \"FeatureCollection\",\n" +
                "    \"name\": \"3分区\",\n" +
                "    \"crs\": {\n" +
                "        \"type\": \"name\",\n" +
                "        \"properties\": {\n" +
                "            \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"features\": [\n" +
                "        {\n" +
                "            \"type\": \"Feature\",\n" +
                "            \"properties\": {\n" +
                "                \"name\": \"2\",\n" +
                "                \"areaId\": \"2\",\n" +
                "                \"color\": \"#FF6F6F\",\n" +
                "                \"outlineColor\": \"rgba(255,61,61,0.10)\",\n" +
                "                \"bgColor\": \"rgba(255,61,61,0.30)\"\n" +
                "            },\n" +
                "            \"geometry\": {\n" +
                "                \"type\": \"Polygon\",\n" +
                "                \"coordinates\": [\n" +
                "                    [\n" +
                "                        [\n" +
                "                            100.20758981239464,\n" +
                "                            26.912151342270537\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2097680320124,\n" +
                "                            26.91059058889153\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21187581828656,\n" +
                "                            26.91053077989431\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21419419958015,\n" +
                "                            26.910721287450713\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21391311089873,\n" +
                "                            26.914286179254653\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21602060008806,\n" +
                "                            26.914351213886402\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22571305818295,\n" +
                "                            26.914423421891026\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22592373768722,\n" +
                "                            26.913422851593044\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22810053465957,\n" +
                "                            26.91336203247908\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2304175481278,\n" +
                "                            26.913738970355556\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23126006304506,\n" +
                "                            26.912738769791034\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23266416202812,\n" +
                "                            26.912864733419255\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2323131109119,\n" +
                "                            26.914866034720106\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2316110047136,\n" +
                "                            26.917242338400904\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23182158587535,\n" +
                "                            26.919368990529744\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23175134822026,\n" +
                "                            26.92093253640294\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23161091292255,\n" +
                "                            26.92205822213651\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23231294899303,\n" +
                "                            26.923246966988117\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23301495023608,\n" +
                "                            26.924623291997097\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23519109899519,\n" +
                "                            26.926062915335084\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23659496031253,\n" +
                "                            26.927001687699594\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23778818763218,\n" +
                "                            26.92756505530777\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23834966066305,\n" +
                "                            26.929378914796402\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23968312777217,\n" +
                "                            26.9316307538209\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24045510038077,\n" +
                "                            26.933632182949683\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22852142543759,\n" +
                "                            26.93337574677307\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22149895427994,\n" +
                "                            26.933182092018786\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21616064944622,\n" +
                "                            26.933238956145424\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21250756576055,\n" +
                "                            26.93298442254801\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20801084214834,\n" +
                "                            26.934104170830814\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20772978906733,\n" +
                "                            26.933728568192592\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20702714759983,\n" +
                "                            26.932539398564344\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2066055979039,\n" +
                "                            26.93009984270282\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20562183632616,\n" +
                "                            26.929598145037655\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20491915461596,\n" +
                "                            26.92872158595304\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2040056338911,\n" +
                "                            26.927782158166586\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2033731994219,\n" +
                "                            26.92646789248728\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20295158839768,\n" +
                "                            26.925153912883186\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20189748446194,\n" +
                "                            26.92327607340372\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2010541627887,\n" +
                "                            26.922711905070425\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20133532205301,\n" +
                "                            26.921336398289544\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20105425286314,\n" +
                "                            26.919584747257133\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20028121690228,\n" +
                "                            26.918207577747\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20098402959586,\n" +
                "                            26.917708305407118\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20337342532537,\n" +
                "                            26.91802464978713\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20716806859231,\n" +
                "                            26.917592309741924\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20969765032316,\n" +
                "                            26.9155942986001\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21117313522016,\n" +
                "                            26.91447039214968\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20997873926727,\n" +
                "                            26.913905913036217\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2089950773479,\n" +
                "                            26.91334167985755\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20751954199581,\n" +
                "                            26.912213801576744\n" +
                "                        ]\n" +
                "                    ]\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"type\": \"Feature\",\n" +
                "            \"properties\": {\n" +
                "                \"name\": \"3\",\n" +
                "                \"areaId\": \"3\",\n" +
                "                \"color\": \"#3DFF43\",\n" +
                "                \"outlineColor\": \"rgba(61,255,67,0.10)\",\n" +
                "                \"bgColor\": \"rgba(61,255,67,0.30)\"\n" +
                "            },\n" +
                "            \"geometry\": {\n" +
                "                \"type\": \"Polygon\",\n" +
                "                \"coordinates\": [\n" +
                "                    [\n" +
                "                        [\n" +
                "                            100.20782756200158,\n" +
                "                            26.912051372161027\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20805813030985,\n" +
                "                            26.911504405636688\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20636740787893,\n" +
                "                            26.911023169182233\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2049840165373,\n" +
                "                            26.91081595986138\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20252451263482,\n" +
                "                            26.910949142027\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2002186020025,\n" +
                "                            26.910603547391442\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19860439528186,\n" +
                "                            26.909711661195388\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19745137889045,\n" +
                "                            26.908683627540306\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19675957120484,\n" +
                "                            26.907109015294203\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19645215488848,\n" +
                "                            26.90491928038609\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19637533835478,\n" +
                "                            26.902798284338257\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19646105405518,\n" +
                "                            26.900511785202976\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.1966324004033,\n" +
                "                            26.898454055686226\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19688937838326,\n" +
                "                            26.89654887129419\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19721178615332,\n" +
                "                            26.89494061951625\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19786055744001,\n" +
                "                            26.892676490986535\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.19840950614184,\n" +
                "                            26.890589816034577\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.1992079671997,\n" +
                "                            26.88783722790909\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2002060067312,\n" +
                "                            26.88450743498177\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20050541328489,\n" +
                "                            26.883530680133756\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20908664809254,\n" +
                "                            26.882166144666225\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21601985330108,\n" +
                "                            26.88146408519375\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22081146336687,\n" +
                "                            26.880932691803164\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22601988287978,\n" +
                "                            26.88071189955193\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2307210368926,\n" +
                "                            26.880650961146575\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23238428694256,\n" +
                "                            26.881650935122458\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23238427334472,\n" +
                "                            26.882585405439574\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23227577154212,\n" +
                "                            26.8841642645848\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23299888348652,\n" +
                "                            26.885292476331717\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23372198323196,\n" +
                "                            26.8864528820076\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23397503951803,\n" +
                "                            26.887870783922317\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23339652837684,\n" +
                "                            26.889835963820378\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23202253593256,\n" +
                "                            26.89231614612442\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23191403414623,\n" +
                "                            26.89383042594457\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23187784638478,\n" +
                "                            26.895538039917536\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23137814389234,\n" +
                "                            26.896602199060222\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23198676725848,\n" +
                "                            26.89843298066978\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23251929068928,\n" +
                "                            26.900467049519566\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23388864876712,\n" +
                "                            26.902094802618464\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2345732761024,\n" +
                "                            26.904603361782605\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23472539484203,\n" +
                "                            26.906094780667956\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23419286051866,\n" +
                "                            26.907585806520018\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23388853721839,\n" +
                "                            26.90928029433914\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23343205508681,\n" +
                "                            26.910635732914187\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23289949782492,\n" +
                "                            26.911516629082115\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23145397534934,\n" +
                "                            26.91131238738961\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23069309203727,\n" +
                "                            26.91368431666814\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22780169452048,\n" +
                "                            26.913343328928104\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22612758113284,\n" +
                "                            26.913138650925966\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22589925124176,\n" +
                "                            26.914765244175722\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21623287090127,\n" +
                "                            26.914348977687503\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21396878813859,\n" +
                "                            26.91420237460625\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21437566192759,\n" +
                "                            26.91073621221919\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2103655977598,\n" +
                "                            26.910368974481482\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20972627233753,\n" +
                "                            26.910109425166713\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20879628209386,\n" +
                "                            26.911194757844733\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20850564836014,\n" +
                "                            26.911711783551073\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20780815286466,\n" +
                "                            26.912021271784084\n" +
                "                        ]\n" +
                "                    ]\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"type\": \"Feature\",\n" +
                "            \"properties\": {\n" +
                "                \"name\": \"4\",\n" +
                "                \"areaId\": \"4\",\n" +
                "                \"color\": \"#FFF386\",\n" +
                "                \"outlineColor\": \"rgba(255,243,134,0.10)\",\n" +
                "                \"bgColor\": \"rgba(255,243,134,0.30)\"\n" +
                "            },\n" +
                "            \"geometry\": {\n" +
                "                \"type\": \"Polygon\",\n" +
                "                \"coordinates\": [\n" +
                "                    [\n" +
                "                        [\n" +
                "                            100.20040077186171,\n" +
                "                            26.8831144778514\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21611015342755,\n" +
                "                            26.881275385049765\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2208291563985,\n" +
                "                            26.880798105250726\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22956871767096,\n" +
                "                            26.88039212168543\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23195512759962,\n" +
                "                            26.88133322317487\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23264001299034,\n" +
                "                            26.881715126306656\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23478023546174,\n" +
                "                            26.879961443957843\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23486585272282,\n" +
                "                            26.87919849346683\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2362354847958,\n" +
                "                            26.87858876034373\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.23871771370509,\n" +
                "                            26.87813199707255\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24051501460707,\n" +
                "                            26.877980006110096\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24239774415699,\n" +
                "                            26.878819826447835\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24368134347957,\n" +
                "                            26.879048993801693\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24633396124536,\n" +
                "                            26.87614991401046\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24881515502962,\n" +
                "                            26.87355570622991\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.25129609507327,\n" +
                "                            26.870503373634982\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2530924721683,\n" +
                "                            26.86813762105651\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.25853275517282,\n" +
                "                            26.860051700039016\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.25975942808343,\n" +
                "                            26.858956533300567\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26244617415675,\n" +
                "                            26.85744356173812\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26454862823971,\n" +
                "                            26.855878517421015\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26565821495747,\n" +
                "                            26.853844781096\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26630058958581,\n" +
                "                            26.852124056537622\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26600861971424,\n" +
                "                            26.851498683302122\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.26180385653666,\n" +
                "                            26.85009354062257\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.25765675797177,\n" +
                "                            26.848791978654273\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.24521112011516,\n" +
                "                            26.844101293212145\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2291194218283,\n" +
                "                            26.839341256998292\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22629706161409,\n" +
                "                            26.8384639927017\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.22427212163602,\n" +
                "                            26.838188799125177\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.220589988314,\n" +
                "                            26.838568160010222\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21690732384464,\n" +
                "                            26.840040952785987\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21463608966691,\n" +
                "                            26.841679093908198\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.21168935090282,\n" +
                "                            26.84408188220987\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20997029092854,\n" +
                "                            26.845665661109987\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20825109477126,\n" +
                "                            26.848233755051986\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.2070843882486,\n" +
                "                            26.851568093860095\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20548776216738,\n" +
                "                            26.856542240855518\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20468940244506,\n" +
                "                            26.859603382768498\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20389101929393,\n" +
                "                            26.862500380904542\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20316589307016,\n" +
                "                            26.867291525210316\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20255786708381,\n" +
                "                            26.871823534075208\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20164585346633,\n" +
                "                            26.87750494873359\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20118982687825,\n" +
                "                            26.880413187130095\n" +
                "                        ],\n" +
                "                        [\n" +
                "                            100.20042986414111,\n" +
                "                            26.883050285482437\n" +
                "                        ]\n" +
                "                    ]\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(srcDataStr);
        com.alibaba.fastjson2.JSONArray jsonArray = jsonObject.getJSONArray("features");
        // 组装数据
        List<Map<String, Object>> zonMapList = new ArrayList<>();
        List<Map<String, Object>> zoningMapPositionList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject tmpObj = jsonArray.getJSONObject(i);
            JSONObject properties = tmpObj.getJSONObject("properties");
            String name = properties.getString("name");
            String areaId = properties.getString("areaId");
            String color = properties.getString("color");
            String outlineColor = properties.getString("outlineColor");
            String bgColor = properties.getString("bgColor");
            String id = ConvertUtils.UUID(areaId);
            Map<String, Object> zonMap = new HashMap<String, Object>() {{
                put("id", id);
                put("name", name);
                put("area_id", areaId);
                put("color", color);
                put("outline_color", outlineColor);
                put("bg_color", bgColor);
            }};
            zonMapList.add(zonMap);
            com.alibaba.fastjson2.JSONArray coordinates = tmpObj.getJSONObject("geometry").getJSONArray("coordinates");
            com.alibaba.fastjson2.JSONArray subCoordinates = coordinates.getJSONArray(0);
            for (int j = 0; j < subCoordinates.size(); j++) {
                String longitude = subCoordinates.getJSONArray(j).getString(0);
                String latitude = subCoordinates.getJSONArray(j).getString(1);
                Map<String, Object> zoningMapPosition = new HashMap<String, Object>() {{
                    put("id", null);
                    put("zoning_id", id);
                    put("longitude", longitude);
                    put("latitude", latitude);
                }};
                zoningMapPositionList.add(zoningMapPosition);
            }
        }
        transactionTemplate.execute(transactionStatus -> {
            mppService.insertBatch("zoning_map", zonMapList);
            mppService.insertBatch("zoning_map_position", zoningMapPositionList);
            return 1;
        });
    }

}
