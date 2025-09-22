package com.sonin;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.modules.mpp.constant.MPPConstant;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import com.sonin.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
@ActiveProfiles("sn")
public class SnBootApplicationTest {

    @Autowired
    private IMPPService baseService;

    @Test
    public void readExcelTest() {
        try {
            String filePath = "E:\\Company\\kingtrol\\011-四川生态环保\\20250708\\厂站信息 (4).xls";
            FileInputStream file = new FileInputStream(new File(filePath));
            Workbook workbook;
            // 根据文件扩展名创建不同的Workbook实例
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file);
            } else if (filePath.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(file);
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持.xlsx和.xls格式");
            }
            // 查询所有的区域信息
            List<Map<String, Object>> sysAreaList = baseService.queryForList("select * from sys_area", new QueryWrapper<>());
            Map<String, String> areaId2NameDictMap = sysAreaList.stream().collect(Collectors.toMap(item -> ConvertUtils.getString(item.get("id")), item -> ConvertUtils.getString(item.get("area_name"))));
            // 查询机构信息
            List<String> columnList1 = new ArrayList<String>() {{
                add("sys_depart.id");
                add("sys_depart.depart_name");
                add("sys_factory_info.pro_province");
                add("sys_factory_info.pro_city");
                add("sys_factory_info.pro_county");
                add("sys_factory_info.pro_address");
            }};
            List<Map<String, Object>> queryMapList1 = baseService.queryForList("select " + String.join(",", columnList1) + " from sys_depart left join sys_factory_info on sys_depart.id = sys_factory_info.factory_id", new QueryWrapper<>());
            // 获取第一个工作表
            Sheet sheet0 = workbook.getSheetAt(0);
            // 遍历每一行
            List<Map<String, Object>> resMapList = new ArrayList<>();
            for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
                Map<String, Object> resMap = new HashMap<>();
                resMapList.add(resMap);
                resMap.put("id", i);
                resMap.put("order_num", i);
                String departName = ConvertUtils.getString(sheet0.getRow(i).getCell(2));
                resMap.put("depart_name", departName);
                // 过滤掉重复的部门名称
                if (Arrays.asList("城市生活污水处理厂", "白马镇污水处理厂").contains(departName)) {
                    continue;
                }
                Optional<Map<String, Object>> tmpOptional = queryMapList1.stream().filter(item -> departName.equals(item.get("depart_name"))).findFirst();
                if (tmpOptional.isPresent()) {
                    String departId = ConvertUtils.getString(tmpOptional.get().get("id"));
                    resMap.put("depart_id", departId);
                    String proProvince = ConvertUtils.getString(tmpOptional.get().get("pro_province"));
                    resMap.put("pro_province", areaId2NameDictMap.getOrDefault(proProvince, ""));
                    String proCity = ConvertUtils.getString(tmpOptional.get().get("pro_city"));
                    resMap.put("pro_city", areaId2NameDictMap.getOrDefault(proCity, ""));
                    String proCounty = ConvertUtils.getString(tmpOptional.get().get("pro_county"));
                    resMap.put("pro_county", areaId2NameDictMap.getOrDefault(proCounty, ""));
                    String proAddress = ConvertUtils.getString(tmpOptional.get().get("pro_address"));
                    resMap.put("pro_address", proAddress);
                    String fullName = areaId2NameDictMap.getOrDefault(proProvince, "") + areaId2NameDictMap.getOrDefault(proCity, "") + areaId2NameDictMap.getOrDefault(proCounty, "") + proAddress;
                    resMap.put("full_name", fullName);
                }
            }
            baseService.insertBatch("sys_demo", resMapList, MPPConstant.INSERT_IGNORE);
            // 关闭资源
            workbook.close();
            file.close();
            // 手动处理完 城市生活污水处理厂 白马镇污水处理厂
            List<Map<String, Object>> queryMapLis2 = baseService.queryForList("select * from sys_demo", new QueryWrapper<>().in("depart_name", Arrays.asList("城市生活污水处理厂", "白马镇污水处理厂")));
            for (Map<String, Object> tmpMap : queryMapLis2) {
                String departId = ConvertUtils.getString(tmpMap.get("depart_id"));
                Optional<Map<String, Object>> tmpOptional = queryMapList1.stream().filter(item -> departId.equals(item.get("id"))).findFirst();
                if (tmpOptional.isPresent()) {
                    String proProvince = ConvertUtils.getString(tmpOptional.get().get("pro_province"));
                    String proCity = ConvertUtils.getString(tmpOptional.get().get("pro_city"));
                    String proCounty = ConvertUtils.getString(tmpOptional.get().get("pro_county"));
                    String proAddress = ConvertUtils.getString(tmpOptional.get().get("pro_address"));
                    String fullName = areaId2NameDictMap.getOrDefault(proProvince, "") + areaId2NameDictMap.getOrDefault(proCity, "") + areaId2NameDictMap.getOrDefault(proCounty, "") + proAddress;
                    UpdateWrapper<?> updateWrapper2 = new UpdateWrapper<>();
                    updateWrapper2.set("pro_province", areaId2NameDictMap.getOrDefault(proProvince, ""))
                            .set("pro_city", areaId2NameDictMap.getOrDefault(proCity, ""))
                            .set("pro_county", areaId2NameDictMap.getOrDefault(proCounty, ""))
                            .set("pro_address", proAddress)
                            .set("full_name", fullName)
                            .eq("id", tmpMap.get("id"));
                    baseService.update("sys_demo", updateWrapper2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void yssTest() {
        // http://yjx.chezhuweishi.com/#/login
        // 账号：18921099239，密码：123456
//        String tokenStr = HttpUtils.doGet("http://120.24.255.164:5010/api/OAuth/SignInAsync?phone=18921099239&password=123456");
//        JSON tokenJson = JSONUtil.parse(tokenStr);
//        String tokenStr0 = ((JSONObject) tokenJson).getJSONObject("data").getStr("token");
        String tokenStr0 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlMTU0ZTdiMC0zYjIxLTQ1MzItYTU0MC0wMmNlOWNhZTY2NTciLCJuYW1lIjoi5p-z5ZCv5YWJIiwibWVjaGFuaXNtSWQiOiI5MzJjNDNkMy1kM2UwLTRlOTUtODYzMy01YmVjYmMzZjkyN2EiLCJtZWNoYW5pc21OYW1lIjoi5bm_5bee5aSp5L-h5L-d6Zmp5YWs5Lyw5pyJ6ZmQ5YWs5Y-45rGf6IuP5YiG5YWs5Y-4Iiwicm9sZUlkcyI6ImIxOTQ3YmZiLTMwMzYtNDY4NS1iODZjLWM1Mzg4M2UyOGUyZCwyZWVkYzg5OS0zOWE2LTRiNDAtYjc1MS04NTljYTFiM2U2MTMsMDFiZjY3OTktYzNiMC00ODdjLWFjNDItOThkZmJhN2Q5ZjY0LDdlZmRiZTk4LTJhZGUtNGIwZC05ZDIxLWEyNDczNjliYzZjNCIsInBob25lIjoiMTg5MjEwOTkyMzkiLCJuYmYiOjE3NTgyNDQ1NTIsImV4cCI6MTc1ODMzMDk1MiwiaXNzIjoiYmxkLnlqeCIsImF1ZCI6ImJsZC55angifQ.NUOqTmmkzNnp_ai7-_1Hgfz9EYWiJ_NXsZFWyc9_ZJo";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Bearer " + tokenStr0);
        String resStr = HttpUtils.doGet("http://120.24.255.164:5010/api/AnXinDcTask/QueryPagerZyAnXinDcTask?FinanceStates=0&FinanceStates=10&FinanceStates=20&CProdTypes=%E8%B4%A3%E4%BB%BB%E4%BF%9D%E9%99%A9&CProdTypes=%E6%84%8F%E5%A4%96%E4%BC%A4%E5%AE%B3%E4%BF%9D%E9%99%A9&personDanger=&page=1&pageSize=1000", headerMap);
        JSON json = JSONUtil.parse(resStr);
        JSONArray jsonArray = ((JSONObject) json).getJSONObject("data").getJSONArray("data");
        int typeA = 0, typeB = 0, typeC = 0;
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String aStr = jsonObject.getStr("dcTaskPgMechanism");
            String bStr = jsonObject.getStr("operatorMechanisms");
            if (aStr.contains("江苏") && bStr.contains("江苏")) {
                typeA++;
            } else if (aStr.contains("江苏") && !bStr.contains("江苏")) {
                typeB++;
            } else if (!aStr.contains("江苏") && bStr.contains("江苏")) {
                typeC++;
            }
        }
        System.out.println("typeA=" + typeA);
        System.out.println("typeB=" + typeB);
        System.out.println("typeC=" + typeC);
        System.out.println("typeABC=" + (typeA + typeB + typeC));
    }

}
