package com.sonin.modules.test;

import com.sonin.api.vo.Result;
import com.sonin.core.context.SpringContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/17 17:01
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/cbytj")
    public Result<Object> cbytjCtrl() {
        Result<Object> result = new Result<>();
        try {
            Map<String, Object> resMap = new LinkedHashMap<>();
            List<Map<String, Object>> table = new ArrayList<>();
            JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean("suzhou");
            // 查询所有的 抄表人员
            List<Map<String, Object>> cbyMapList = jdbcTemplate.queryForList("select DISTINCT(抄表员) as cby, 户号 as hh from 用户信息");
            // 抄表人员 => 户号
            Map<String, List<String>> cby2hhMap = new HashMap<>();
            for (Map<String, Object> item : cbyMapList) {
                String cby = "" + item.get("cby");
                String hh = "" + item.get("hh");
                cby2hhMap.putIfAbsent(cby, new ArrayList<>());
                cby2hhMap.get(cby).add(hh);
            }
            long scsAvg = 0L;
            double cjlAvg = 0D;
            double ycsAvg = 0D;
            for (Map.Entry<String, List<String>> item : cby2hhMap.entrySet()) {
                String cby = item.getKey();
                Map<String, Object> entity = new LinkedHashMap<>();
                entity.put("姓名", cby);
                String inSqlPart = "";
                for (String hh : item.getValue()) {
                    inSqlPart = inSqlPart + ",'" + hh + "'";
                }
                inSqlPart = inSqlPart.substring(1);
                Map<String, Object> jdbcMap = jdbcTemplate.queryForMap("select count(DISTINCT(户号)) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 户号 in (" + inSqlPart + ")");
                entity.put("实抄数", jdbcMap.get("total"));
                scsAvg += Long.parseLong("" + jdbcMap.get("total"));
                Map<String, Object> jdbcMap2 = jdbcTemplate.queryForMap("select count(DISTINCT(户号)) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 抄表标识 = '正常抄表' and 户号 in (" + inSqlPart + ")");
                try {
                    if (new BigDecimal("" + jdbcMap.get("total")).equals(new BigDecimal(0D))) {
                        entity.put("抄见率", 0D);
                        cjlAvg += 0D;
                    } else {
                        entity.put("抄见率", new Double("" + jdbcMap2.get("total")) / (new Double("" + jdbcMap.get("total"))));
                        cjlAvg += new Double("" + jdbcMap2.get("total")) / (new Double("" + jdbcMap.get("total")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    entity.put("抄见率", 0D);
                    cjlAvg += 0D;
                }
                Map<String, Object> jdbcMap3 = jdbcTemplate.queryForMap("select count(DISTINCT(户号)) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 抄表标识 != '正常抄表' and 实用水量 != 0 and 户号 in (" + inSqlPart + ")");
                entity.put("异常数", jdbcMap3.get("total"));
                ycsAvg += Long.parseLong("" + jdbcMap3.get("total"));
                table.add(entity);
            }
            Collections.sort(table, ((o1, o2) -> Integer.parseInt("" + o2.get("实抄数")) - Integer.parseInt("" + o1.get("实抄数"))));
            resMap.put("总人数", cby2hhMap.size());
            resMap.put("平均实抄数", scsAvg / cby2hhMap.size());
            resMap.put("平均抄见率", cjlAvg / cby2hhMap.size());
            resMap.put("平均异常数", ycsAvg / cby2hhMap.size());
            resMap.put("table", table);
            result.setResult(resMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/cbctj")
    public Result<Object> cbctjCtrl() {
        Result<Object> result = new Result<>();
        try {
            Map<String, Object> resMap = new LinkedHashMap<>();
            List<Map<String, Object>> table = new ArrayList<>();
            JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean("suzhou");
            // 查询所有的 表册号
            List<Map<String, Object>> bchMapList = jdbcTemplate.queryForList("select 表册号 as bch, 户号 as hh from 用户信息");
            // 表册号 => 户号
            Map<String, List<String>> bch2hhMap = new HashMap<>();
            for (Map<String, Object> item : bchMapList) {
                String bch = "" + item.get("bch");
                String hh = "" + item.get("hh");
                bch2hhMap.putIfAbsent(bch, new ArrayList<>());
                bch2hhMap.get(bch).add(hh);
            }
            long yhsAvg = 0L;
            double cjlAvg = 0D;
            double mjslAvg = 0D;
            for (Map.Entry<String, List<String>> item : bch2hhMap.entrySet()) {
                String bch = item.getKey();
                Map<String, Object> entity = new LinkedHashMap<>();
                entity.put("表册号", bch);
                entity.put("用户数", item.getValue().size());
                yhsAvg += item.getValue().size();
                String inSqlPart = "";
                for (String hh : item.getValue()) {
                    inSqlPart = inSqlPart + ",'" + hh + "'";
                }
                inSqlPart = inSqlPart.substring(1);
                Map<String, Object> jdbcMap = jdbcTemplate.queryForMap("select count(DISTINCT(户号)) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 户号 in (" + inSqlPart + ")");
                entity.put("实抄数", jdbcMap.get("total"));
                Map<String, Object> jdbcMap2 = jdbcTemplate.queryForMap("select count(DISTINCT(户号)) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 抄表标识 = '正常抄表' and 户号 in (" + inSqlPart + ")");
                try {
                    if (new BigDecimal("" + jdbcMap.get("total")).equals(new BigDecimal(0D))) {
                        entity.put("抄见率", 0D);
                        cjlAvg += 0D;
                    } else {
                        entity.put("抄见率", new Double("" + jdbcMap2.get("total")) / (new Double("" + jdbcMap.get("total"))));
                        cjlAvg += new Double("" + jdbcMap2.get("total")) / (new Double("" + jdbcMap.get("total")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    entity.put("抄见率", 0D);
                    cjlAvg += 0D;
                }
                Map<String, Object> jdbcMap3 = jdbcTemplate.queryForMap("select ifnull(sum( 实用水量 ), 0) as total from 抄表 where date_format( 本期抄表时间, '%Y-%m' ) = '2022-06' and 户号 in (" + inSqlPart + ")");
                entity.put("抄见水量", jdbcMap3.get("total"));
                mjslAvg += Double.parseDouble("" + jdbcMap3.get("total"));
                table.add(entity);
            }
            Collections.sort(table, ((o1, o2) -> Integer.parseInt("" + o2.get("实抄数")) - Integer.parseInt("" + o1.get("实抄数"))));
            resMap.put("表册总数", bch2hhMap.size());
            resMap.put("平均用户数", yhsAvg / bch2hhMap.size());
            resMap.put("平均抄见率", cjlAvg / bch2hhMap.size());
            resMap.put("平均抄见水量", mjslAvg / bch2hhMap.size());
            resMap.put("table", table);
            result.setResult(resMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
