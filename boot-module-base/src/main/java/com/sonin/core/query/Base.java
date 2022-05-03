package com.sonin.core.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.CaseFormat;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.core.context.SpringContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sonin
 * @date 2021/12/4 19:27
 */
public abstract class Base implements IBase {

    /**
     * SQL拼接语句
     */
    Collection<Class> classes;

    Collection<String> conditions;

    Collection<String> selectedColumns;

    private String prefixSql;

    private QueryWrapper<?> queryWrapper;

    /**
     * 构造返回字段
     *
     * @return
     */
    String initColumns() {
        StringBuilder stringBuilder = new StringBuilder();
        String className, tableName, classFieldName, tableFieldName, alias;
        Field[] fields;
        for (Class clazz : this.classes) {
            className = clazz.getSimpleName();
            tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 过滤掉 @TableField(exist = false) 情况
                TableField tableFieldAnno = field.getAnnotation(TableField.class);
                if (tableFieldAnno != null && !tableFieldAnno.exist()) {
                    continue;
                }
                classFieldName = field.getName();
                tableFieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, classFieldName);
                alias = DOUBLE_QUOTES + className + UNDERLINE + classFieldName + DOUBLE_QUOTES;
                stringBuilder.append(COMMA).append(SPACE).append(tableName).append(DOT).append(tableFieldName).append(SPACE).append(AS).append(SPACE).append(alias);
            }
        }
        return stringBuilder.toString().replaceFirst(COMMA + SPACE, EMPTY);
    }

    /**
     * 判断SQL注入
     *
     * @param param
     * @throws Exception
     */
    private void sqlInject(String param) throws Exception {
        Pattern pattern = Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|')");
        Matcher matcher = pattern.matcher(param.toLowerCase());
        if (matcher.find()) {
            throw new Exception("SQL注入: " + param);
        }
    }

    /**
     * 构造完整SQL
     *
     * @return
     * @throws Exception
     */
    private String initSql() throws Exception {
        String suffixSql = this.queryWrapper.getCustomSqlSegment();
        Map<String, Object> paramNameValuePairs = this.queryWrapper.getParamNameValuePairs();
        Object value;
        for (Map.Entry<String, Object> item : paramNameValuePairs.entrySet()) {
            value = item.getValue();
            sqlInject("" + value);
            if (value instanceof String) {
                value = "'" + value + "'";
            }
            suffixSql = suffixSql.replaceFirst("#\\{ew\\.paramNameValuePairs\\." + item.getKey() + "}", "" + value);
        }
        return prefixSql + SPACE + suffixSql;
    }

    /**
     * 选择查询字段，格式: DemoA_aName
     *
     * @param fields
     * @return
     */
    public Base select(Field... fields) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        String className, tableName, fieldName, column, alias;
        for (Field field : fields) {
            className = field.getDeclaringClass().getSimpleName();
            tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
            fieldName = field.getName();
            column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            alias = tableName + DOT + column + SPACE + AS + SPACE + DOUBLE_QUOTES + className + UNDERLINE + fieldName + DOUBLE_QUOTES;
            this.selectedColumns.add(alias);
        }
        return this;
    }

    /**
     * 选择查询字段，格式自定义
     *
     * @param fields
     * @return
     */
    public Base select(String... fields) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        this.selectedColumns.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * === 以下抽象方法 ===
     */

    /**
     * 构造前缀SQL
     *
     * @return
     */
    public abstract String initPrefixSql();

    public abstract Base from(Class... classes);

    public abstract Base innerJoin(Class clazz, Field leftField, Field rightField);

    public abstract Base leftJoin(Class clazz, Field leftField, Field rightField);

    public abstract Base rightJoin(Class clazz, Field leftField, Field rightField);

    /**
     * 准备构造查询条件
     *
     * @return
     */
    public Base where() {
        this.prefixSql = initPrefixSql();
        this.queryWrapper = new QueryWrapper<>();
        return this;
    }

    /**
     * === 以下方式提供QueryWrapper构造条件 ===
     */

    public Base eq(boolean condition, Field leftField, Field rightField) {
        String leftClassName = leftField.getDeclaringClass().getSimpleName();
        String leftTableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, leftClassName);
        String leftFieldName = leftField.getName();
        String leftColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, leftFieldName);
        String rightClassName = rightField.getDeclaringClass().getSimpleName();
        String rightTableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rightClassName);
        String rightFieldName = rightField.getName();
        String rightColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rightFieldName);
        this.queryWrapper.apply(condition, (leftTableName + DOT + leftColumn) + " = " + (rightTableName + DOT + rightColumn));
        return this;
    }

    public Base eq(boolean condition, String column, Object val) {
        this.queryWrapper.eq(condition, column, val);
        return this;
    }

    public Base ne(boolean condition, String column, Object val) {
        this.queryWrapper.ne(condition, column, val);
        return this;
    }

    public Base gt(boolean condition, String column, Object val) {
        this.queryWrapper.gt(condition, column, val);
        return this;
    }

    public Base ge(boolean condition, String column, Object val) {
        this.queryWrapper.ge(condition, column, val);
        return this;
    }

    public Base lt(boolean condition, String column, Object val) {
        this.queryWrapper.lt(condition, column, val);
        return this;
    }

    public Base le(boolean condition, String column, Object val) {
        this.queryWrapper.le(condition, column, val);
        return this;
    }

    public Base between(boolean condition, String column, Object val1, Object val2) {
        this.queryWrapper.between(condition, column, val1, val2);
        return this;
    }

    public Base notBetween(boolean condition, String column, Object val1, Object val2) {
        this.queryWrapper.notBetween(condition, column, val1, val2);
        return this;
    }

    public Base like(boolean condition, String column, Object val) {
        this.queryWrapper.like(condition, column, val);
        return this;
    }

    public Base notLike(boolean condition, String column, Object val) {
        this.queryWrapper.notLike(condition, column, val);
        return this;
    }

    public Base likeLeft(boolean condition, String column, Object val) {
        this.queryWrapper.likeLeft(condition, column, val);
        return this;
    }

    public Base likeRight(boolean condition, String column, Object val) {
        this.queryWrapper.likeRight(condition, column, val);
        return this;
    }

    public Base isNull(boolean condition, String column) {
        this.queryWrapper.isNull(condition, column);
        return this;
    }

    public Base isNotNull(boolean condition, String column) {
        this.queryWrapper.isNotNull(condition, column);
        return this;
    }

    public Base in(boolean condition, String column, Collection<?> coll) {
        this.queryWrapper.in(condition, column, coll);
        return this;
    }

    public Base notIn(boolean condition, String column, Collection<?> coll) {
        this.queryWrapper.notIn(condition, column, coll);
        return this;
    }

    public Base inSql(boolean condition, String column, String inValue) {
        this.queryWrapper.inSql(condition, column, inValue);
        return this;
    }

    public Base notInSql(boolean condition, String column, String inValue) {
        this.queryWrapper.notInSql(condition, column, inValue);
        return this;
    }

    public Base groupBy(boolean condition, String... columns) {
        this.queryWrapper.groupBy(condition, columns);
        return this;
    }

    public Base having(boolean condition, String sqlHaving, Object... params) {
        this.queryWrapper.having(condition, sqlHaving, params);
        return this;
    }

    public Base or(boolean condition) {
        this.queryWrapper.or(condition);
        return this;
    }

    public Base apply(boolean condition, String applySql, Object... value) {
        this.queryWrapper.apply(condition, applySql, value);
        return this;
    }

    public Base last(boolean condition, String lastSql) {
        this.queryWrapper.last(condition, lastSql);
        return this;
    }

    public Base comment(boolean condition, String comment) {
        this.queryWrapper.comment(condition, comment);
        return this;
    }

    public Base exists(boolean condition, String existsSql) {
        this.queryWrapper.exists(condition, existsSql);
        return this;
    }

    public Base notExists(boolean condition, String notExistsSql) {
        this.queryWrapper.notExists(condition, notExistsSql);
        return this;
    }

    public Base orderBy(boolean condition, boolean isAsc, String... columns) {
        this.queryWrapper.orderBy(condition, isAsc, columns);
        return this;
    }

    /**
     * === 以下方式获取请求结果 ===
     */

    public Map<String, Object> selectMap() {
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.selectMap(this.prefixSql, this.queryWrapper);
    }

    public IPage<Map<String, Object>> selectMapsPage(IPage<?> page) {
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.selectMapsPage(page, this.prefixSql, this.queryWrapper);
    }

    public List<Map<String, Object>> selectMaps() {
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.selectMaps(this.prefixSql, this.queryWrapper);
    }

    public Map<String, Object> selectMap(String DBName) throws Exception {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean(DBName);
        return jdbcTemplate.queryForMap(initSql());
    }

    public IPage<Map<String, Object>> selectMapsPage(IPage<Map<String, Object>> page, String DBName, String customPageSql) throws Exception {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean(DBName);
        TransactionTemplate transactionTemplate = SpringContext.getBean(TransactionTemplate.class);
        String countSql = SELECT + SPACE + COUNT_ALL + SPACE + FROM + SPACE + LEFT_BRACKET + initSql() + RIGHT_BRACKET + SPACE + AS + SPACE + "tmp";
        if (customPageSql == null || "".equals(customPageSql)) {
            queryWrapper.last(LIMIT + SPACE + (page.getCurrent() - 1) * page.getSize() + COMMA + SPACE + page.getCurrent() * page.getSize());
        } else {
            queryWrapper.last(customPageSql);
        }
        String pageSql = initSql();
        transactionTemplate.execute((transactionStatus -> {
            page.setTotal(Long.parseLong("" + jdbcTemplate.queryForMap(countSql).get(COUNT_ALL)));
            page.setRecords(jdbcTemplate.queryForList(pageSql));
            return 1;
        }));
        return page;
    }

    public List<Map<String, Object>> selectMaps(String DBName) throws Exception {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringContext.getBean(DBName);
        return jdbcTemplate.queryForList(initSql());
    }

}
