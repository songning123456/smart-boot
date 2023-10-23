package com.sonin;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.sonin.core.constant.BaseConstant;
import com.sonin.core.mpp.DataSourceTemplate;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.modules.sequence.service.ISequenceService;
import com.sonin.utils.DateUtils;
import com.sonin.utils.DigitalUtils;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.*;


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
public class BootApplicationTest {

    @Autowired
    private IBaseService baseService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ISequenceService sequenceService;

    /**
     * <pre>
     * JdbcTemplate不支持事务，mybatis支持事务
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void transactionTest() {
        String day = "20240601";
        String sqlPrefix = "INSERT INTO realtimedata ( id, nm, v, ts, createtime, factoryname, devicename, type, gatewaycode ) ";
        String sqlSuffix = "SELECT t1.nm as id, t1.nm, t1.v, t1.ts, t1.createtime, t1.factoryname, t1.devicename, t1.type, t1.gatewaycode FROM xsinsert${day} t1 INNER JOIN ( SELECT nm, max( ts ) AS ts FROM xsinsert${day} GROUP BY nm ) t2 ON t1.nm = t2.nm AND t1.ts = t2.ts";
        sqlSuffix = sqlSuffix.replaceAll("\\$\\{day}", day.substring(0, 8));
        String deleteSql = "delete from realtimedata where nm in (select tmp.nm from (" + sqlSuffix + ") as tmp)";
        String insertSql = sqlPrefix + sqlSuffix;
        String querySql = "select * from realtimedata";
        transactionTemplate.execute((transactionStatus -> {
            List<Map<String, Object>> queryResult = baseService.querySql(querySql);
            int deleteResult = baseService.deleteSql(deleteSql);
            List<Map<String, Object>> queryResult2 = baseService.querySql(querySql);
            int insertResult = baseService.insertSql(insertSql);
            List<Map<String, Object>> queryResult3 = baseService.querySql(querySql);
            return 1;
        }));
        System.out.println("");
    }

    /**
     * <pre>
     * 读取文件测试
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void readFileTest() {
        File folder = new File("E:\\Project\\github-pages\\interview-linux\\pages");
        StringBuilder stringBuilder = new StringBuilder();
        // 检查文件夹是否存在
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // 获取文件夹下的所有文件
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) { // 只处理文件，不包括子文件夹
                        stringBuilder.append("* [").append(file.getName().replaceAll("\\.md", "")).append("](/pages/").append(file.getName()).append(")\n");
                    }
                }
            }
        }
        System.out.println(stringBuilder.toString());
    }

    /**
     * <pre>
     * 文件重命名
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void mvFileTest() {
        File folder = new File("E:\\Downloads\\demo1");
        List<String> singerList = Arrays.asList("邓紫棋", "林俊杰", "周杰伦 - ", "周杰伦 -", "周杰伦");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // 获取文件夹下的所有文件
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        for (String singer : singerList) {
                            if (fileName.toLowerCase().contains(singer) && fileName.endsWith(".mp3")) {
                                fileName = fileName.replace(".mp3", "");
                                /*int lastIndex = fileName.indexOf(singer) + singer.length();
                                String newFileName = fileName.substring(lastIndex + 1) + "_" + singer + ".mp3";
                                File newFile = new File(file.getParent() + File.separator + newFileName);
                                try {
                                    file.renameTo(newFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                                if (fileName.endsWith("周杰伦 - ")) {
                                    String newFileName = fileName.replace("周杰伦 - ", "周杰伦") + ".mp3";
                                    File newFile = new File(file.getParent() + File.separator + newFileName);
                                    try {
                                        file.renameTo(newFile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * <pre>
     * 汉字转拼音
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void pinyinTest() {
        List<String> zhNameList = new ArrayList<String>() {{
            add("原水浊度");
            add("原水pH");
            add("出水浊度");
            add("出水pH");
            add("出水余氯");
            add("原水流量");
            add("出水流量");
            add("当前出水压力");
        }};
        String tableName = "sys_metric_dict_alias";
        List<Map<String, Object>> entityList = new ArrayList<>();
        Map<String, Object> entityMap;
        Date nowDate = new Date();
        for (String zhName : zhNameList) {
            String id = PinyinUtil.getPinyin(zhName, "");
            entityMap = new LinkedHashMap<>();
            entityMap.put("id", id);
            entityMap.put("alias_desc", zhName);
            entityMap.put("create_by", "sonin");
            entityMap.put("create_time", nowDate);
            entityList.add(entityMap);
        }
        baseService.insertBatch(tableName, entityList);
    }

    /**
     * <pre>
     * 模拟数据
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void generateDataTest() {
        String yearMonthDayStr = "2023-08-24";
        List<String> nmList = new ArrayList<String>() {{
            add("demo_test");
        }};
        String startTime = yearMonthDayStr + " 00:00:00";
        String endTime = yearMonthDayStr + " 23:59:59";
        String countTable = "ffs_count";
        long startTs = DateUtils.strToDate(startTime, BaseConstant.dateFormat).getTime() / 1000;
        long endTs = DateUtils.strToDate(endTime, BaseConstant.dateFormat).getTime() / 1000;
        List<Map<String, Object>> historyDataList = new ArrayList<>();
        List<Map<String, Object>> countDataList = new ArrayList<>();
        int index = 0;
        for (String nm : nmList) {
            for (long ts = startTs; ts <= endTs; ts += 10) {
                long finalTs = ts;
                historyDataList.add(new HashMap<String, Object>() {{
                    put("nm", nm);
                    put("v", DigitalUtils.intRandom(0, 100));
                    put("ts", finalTs);
                }});
            }
            for (long ts = startTs; ts <= endTs; ts += 3600) {
                String hourStr = DateUtils.sec2DateStr(ts, BaseConstant.dateFormat.substring(0, 13)) + ":00:00";
                Long hourTs = DateUtils.dateStr2Sec(hourStr, BaseConstant.dateFormat);
                int finalIndex = index;
                countDataList.add(new HashMap<String, Object>() {{
                    put("id", finalIndex);
                    put("nm", nm);
                    put("v", DigitalUtils.intRandom(0, 100));
                    put("ts", StrUtils.getString(hourTs));
                }});
                index++;
            }
        }
        if (!historyDataList.isEmpty() && !countDataList.isEmpty()) {
            DataSourceTemplate.execute("pg-db", () -> {
                baseService.insert("realtimedata", historyDataList.get(historyDataList.size() - 1));
                baseService.insertBatch("xsinsert" + yearMonthDayStr.replaceAll("-", ""), historyDataList);
                baseService.insertBatch(countTable, countDataList);
                return 1;
            });
        }
    }

}
