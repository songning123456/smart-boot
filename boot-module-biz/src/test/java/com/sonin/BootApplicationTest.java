package com.sonin;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;


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
     * 汉字转拼音
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    @Test
    public void pinyinTest() {
        String tableName = "sheet";
        List<Map<String, Object>> queryMapList = baseService.queryForList("select * from " + tableName, new QueryWrapper<>());
        for (Map<String, Object> item : queryMapList) {
            String name = StrUtils.getString(item.get("name"));
            String id = PinyinUtil.getPinyin(name, "") + "A01";
            UpdateWrapper<?> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("id", id)
                    .eq("name", name);
            baseService.update(tableName, updateWrapper);
        }
    }

}
