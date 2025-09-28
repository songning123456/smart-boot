package com.sonin;

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
                if  (tmpIdArr.length == 2) {
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
}
