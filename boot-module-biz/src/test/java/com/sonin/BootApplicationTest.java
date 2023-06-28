package com.sonin;

import com.sonin.modules.base.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

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
    public void test() {
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

}
