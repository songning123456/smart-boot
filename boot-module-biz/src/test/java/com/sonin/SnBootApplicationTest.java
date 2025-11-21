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
import org.apache.commons.collections4.ListUtils;
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
@ActiveProfiles("dev")
public class SnBootApplicationTest {

    @Autowired
    private IMPPService mppService;
    @Autowired
    private TransactionTemplate transactionTemplate;

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
            List<Map<String, Object>> sysAreaList = mppService.queryForList("select * from sys_area", new QueryWrapper<>());
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
            List<Map<String, Object>> queryMapList1 = mppService.queryForList("select " + String.join(",", columnList1) + " from sys_depart left join sys_factory_info on sys_depart.id = sys_factory_info.factory_id", new QueryWrapper<>());
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
            mppService.insertBatch("sys_demo", resMapList, MPPConstant.INSERT_IGNORE);
            // 关闭资源
            workbook.close();
            file.close();
            // 手动处理完 城市生活污水处理厂 白马镇污水处理厂
            List<Map<String, Object>> queryMapLis2 = mppService.queryForList("select * from sys_demo", new QueryWrapper<>().in("depart_name", Arrays.asList("城市生活污水处理厂", "白马镇污水处理厂")));
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
                    mppService.update("sys_demo", updateWrapper2);
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
        String tokenStr0 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlMTU0ZTdiMC0zYjIxLTQ1MzItYTU0MC0wMmNlOWNhZTY2NTciLCJuYW1lIjoi5p-z5ZCv5YWJIiwibWVjaGFuaXNtSWQiOiI5MzJjNDNkMy1kM2UwLTRlOTUtODYzMy01YmVjYmMzZjkyN2EiLCJtZWNoYW5pc21OYW1lIjoi5bm_5bee5aSp5L-h5L-d6Zmp5YWs5Lyw5pyJ6ZmQ5YWs5Y-45rGf6IuP5YiG5YWs5Y-4Iiwicm9sZUlkcyI6ImIxOTQ3YmZiLTMwMzYtNDY4NS1iODZjLWM1Mzg4M2UyOGUyZCwyZWVkYzg5OS0zOWE2LTRiNDAtYjc1MS04NTljYTFiM2U2MTMsMDFiZjY3OTktYzNiMC00ODdjLWFjNDItOThkZmJhN2Q5ZjY0LDdlZmRiZTk4LTJhZGUtNGIwZC05ZDIxLWEyNDczNjliYzZjNCIsInBob25lIjoiMTg5MjEwOTkyMzkiLCJuYmYiOjE3NjM2OTQ1OTQsImV4cCI6MTc2Mzc4MDk5NCwiaXNzIjoiYmxkLnlqeCIsImF1ZCI6ImJsZC55angifQ.nE7-ToHHGo15FoA6kD5O_Xrft-waQ_REvfVtIi8HLsI";
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

    @Test
    public void yss2Test() {
        // http://ccx.chezhuweishi.com/#/login
        // 18921099239，123456
        String tokenStr0 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiNWQwZDYzMC1kNDM1LTRiMTEtYjBmNi1jMzZmZDJmMjU0ZDciLCJuYW1lIjoi5p-z5ZCv5YWJIiwibWVjaGFuaXNtSWQiOiI0MjBjNWZlZS00OGFmLTQ1MGYtOTljNy05ZWU1YTAzM2YxYmEiLCJyb2xlSWRzIjoiNDU1MjllOGYtNThlOC00MWYwLWI3MjQtZmI4ODIzNmJlOTIwIiwibmJmIjoxNzU4NTMyOTExLCJleHAiOjE3NTg2MTkzMTEsImlzcyI6ImJsZC5jb250cmFjdCIsImF1ZCI6ImJsZC5jb250cmFjdCJ9.s3sTxH9SAuAoltkWZOuMu6_tITC_gniFSvGwZ-YPcHY";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Bearer " + tokenStr0);
        String resStr = HttpUtils.doGet("http://47.107.67.160:8009/api/Assets/QueryAssetsType?Word=&AssetsTypes=[]&AssetsStates=[]&CrimeCity=&IsZCPG=true&AssetsCaseType=2&MechanismId=&Operator=&page=1&pageSize=500&startTime=&endTime=&creationStartTime=&creationEndTime=&Mechanism=", headerMap);
        JSON json = JSONUtil.parse(resStr);
        JSONArray jsonArray = ((JSONObject) json).getJSONObject("data").getJSONArray("data");
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        String notFinishStr = "71H6106010620240318A000001\n" +
                "71H6106010620240319A000003\n" +
                "71H6106010620240415A000001\n" +
                "71H6106010620240410A000004\n" +
                "8601192024032305000670\n" +
                "CCX202409010900197753\n" +
                "RQYC2024310000N0039443\n" +
                "RQBB2024310000S0004001\n" +
                "RQBB2024310000N0039149\n" +
                "RQBB2024310000N0039125\n" +
                "DSHH4600242400002324\n" +
                "DSHH480029240000005\n" +
                "DSHH4100242400000020\n" +
                "401042000000240008885\n" +
                "423C70111202420000594\n" +
                "DSHH4600242400002474\n" +
                "RQBB2024310000N0041121\n" +
                "8601012024310067000247\n" +
                "RQEF2024310000N0041206\n" +
                "RQYC2024310000N0040343\n" +
                "RQYC2024310000N0041075\n" +
                "RQBB2024310000N0040281\n" +
                "CCX202410040700200303\n" +
                "CCX202410020500200228\n" +
                "4220300202422030000007\n" +
                "4220300202422030000006\n" +
                "CCX202411140600203135\n" +
                "CCX202411140100203087\n" +
                "CCX202501030200206497 \n" +
                "423B70111202520000076\n" +
                "91000002800002549596\n" +
                "423B70111202520000109 \n" +
                "402022000000251884562\n" +
                "860119202503230500012\n" +
                "CCX202506060400217278 \n" +
                "DSHH4600702500001509\n" +
                "CCX202507161100220235\n" +
                "8601192024032305001548\n" +
                "0761~000405\n" +
                "DSHH4600702500003329\n" +
                "71H6108010620250810A000001\n" +
                "71H6108010620250816A000001\n" +
                "423B70111202520000329\n" +
                "71H6108010620250815A000001\n" +
                "71H6106010620250824A000002\n" +
                "71H6108010620250820A000001\n" +
                "71H6108010620250820A000002\n" +
                "71H6108010620250805A000001\n" +
                "71H6108010620250822A000001\n" +
                "71H6108010620250808A000001\n" +
                "71H6108010620250718A000001\n" +
                "71H6108010620250715A000001\n" +
                "71H6108010620250726A000001\n" +
                "71H6108010620250802A000001\n" +
                "71H6108010620250807A000001\n" +
                "71H6108010620250825A000001\n" +
                "71H6108122920250907A000001\n" +
                "71H6108010620250906A000001\n" +
                "71H6108010620250905A000001\n" +
                "71H6108010620250903A000001\n" +
                "71H6108010620250831A000001\n" +
                "8601012025441203000006\n";
        List<String> notFinishList = Arrays.asList(notFinishStr.split("\n"));
        for (int i = 0; i < jsonArray.size(); i++) {
            String id = jsonArray.getJSONObject(i).getStr("id");
            String inCaseNo = jsonArray.getJSONObject(i).getStr("inCaseNo");
            if (!notFinishList.contains(inCaseNo)) {
                continue;
            }
            String tmpResStr = HttpUtils.doGet("http://47.107.67.160:8009/api/Assets/QueryAssetsPropertyInsuranceResponse/" + id, headerMap);
            JSON tmpJSON = JSONUtil.parse(tmpResStr);
            JSONObject tmpObj = ((JSONObject) tmpJSON).getJSONObject("data").getJSONObject("assets");
//            String fileNo = tmpObj.getStr("fileNo");
            String ajxq = tmpObj.getStr("ajxq");
            entityMapList.add(new HashMap<String, Object>() {{
                put("id", id);
                put("in_case_no", inCaseNo);
                put("ajxq", ajxq);
            }});
        }
        if (!entityMapList.isEmpty()) {
            List<List<Map<String, Object>>> partitionList = ListUtils.partition(entityMapList, 100);
            transactionTemplate.execute(transactionStatus -> {
                partitionList.forEach(partition -> mppService.insertBatch("demo_003", partition));
                return 1;
            });
        }
    }

}
