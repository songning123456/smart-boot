package com.sonin.core.mpp;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.google.common.base.CaseFormat;
import com.sonin.modules.base.service.IBaseService;
import com.sonin.core.context.SpringContext;
import com.sonin.utils.ReflectUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sonin
 * @date 2021/12/4 19:27
 */
public abstract class Base implements IBase {

    private static final Logger logger = LoggerFactory.getLogger(Base.class);

    /**
     * SQL拼接语句
     */
    Collection<Class> classes;

    Collection<String> conditions;

    Collection<String> selectedColumns;

    private String prefixSql;

    private QueryWrapper<?> queryWrapper;

    /**
     * 是否打印日志，默认不打印
     */
    private Boolean logFlag = false;

    /**
     * 打印日志，前缀默认mpp
     */
    private String logPrefixName = "mpp";

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
            sqlInject(EMPTY + value);
            if (value instanceof String) {
                value = SINGLE_QUOTES + value + SINGLE_QUOTES;
            }
            suffixSql = suffixSql.replaceFirst("#\\{ew\\.paramNameValuePairs\\." + item.getKey() + "}", EMPTY + value);
        }
        return prefixSql + SPACE + suffixSql;
    }

    /**
     * === 以下select方法 ===
     */

    public Base select(boolean prefixCondition, Field... fields) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        String className, tableName, fieldName, column, alias;
        for (Field field : fields) {
            // 过滤掉 @TableField(exist = false) 情况
            TableField tableFieldAnno = field.getAnnotation(TableField.class);
            if (tableFieldAnno != null && !tableFieldAnno.exist()) {
                continue;
            }
            className = field.getDeclaringClass().getSimpleName();
            tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
            fieldName = field.getName();
            column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            if (!prefixCondition) {
                alias = tableName + DOT + column + SPACE + AS + SPACE + DOUBLE_QUOTES + fieldName + DOUBLE_QUOTES;
            } else {
                alias = tableName + DOT + column + SPACE + AS + SPACE + DOUBLE_QUOTES + className + UNDERLINE + fieldName + DOUBLE_QUOTES;
            }
            this.selectedColumns.add(alias);
        }
        return this;
    }

    public Base select(Field... fields) {
        this.select(true, fields);
        return this;
    }

    public Base select(Field field) {
        this.select(new Field[]{field});
        return this;
    }

    public Base select(String field) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        this.selectedColumns.add(field);
        return this;
    }

    public Base select(String... fields) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        this.selectedColumns.addAll(Arrays.asList(fields));
        return this;
    }

    public <T> Base select(SFunction<T, ?> sFunc) {
        this.select(new Field[]{ReflectUtils.lambdaField(sFunc)});
        return this;
    }

    public <T> Base select(SFunction<T, ?> sFunc, String alias) {
        if (this.selectedColumns == null) {
            this.selectedColumns = new LinkedHashSet<>();
        }
        Field field = ReflectUtils.lambdaField(sFunc);
        // 过滤掉 @TableField(exist = false) 情况
        TableField tableFieldAnno = field.getAnnotation(TableField.class);
        if (tableFieldAnno != null && !tableFieldAnno.exist()) {
            return this;
        }
        String className = field.getDeclaringClass().getSimpleName();
        String tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        String fieldName = field.getName();
        String column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
        this.selectedColumns.add(tableName + DOT + column + SPACE + AS + SPACE + DOUBLE_QUOTES + alias + DOUBLE_QUOTES);
        return this;
    }

    public <T> Base select(boolean prefixCondition, SFunction<T, ?> sFunc) {
        this.select(prefixCondition, new Field[]{ReflectUtils.lambdaField(sFunc)});
        return this;
    }

    @SafeVarargs
    public final <T> Base select(SFunction<T, ?>... sFuncs) {
        this.select(Arrays.stream(sFuncs).map(ReflectUtils::lambdaField).collect(Collectors.toList()).toArray(new Field[]{}));
        return this;
    }

    @SafeVarargs
    public final <T> Base select(boolean prefixCondition, SFunction<T, ?>... sFuncs) {
        this.select(prefixCondition, Arrays.stream(sFuncs).map(ReflectUtils::lambdaField).collect(Collectors.toList()).toArray(new Field[]{}));
        return this;
    }

    public <T> Base select(T entity) {
        this.select(entity.getClass().getDeclaredFields());
        return this;
    }

    public <T> Base select(Class<T> clazz) {
        this.select(clazz.getDeclaredFields());
        return this;
    }

    public <T> Base select(boolean prefixCondition, T entity) {
        this.select(prefixCondition, entity.getClass().getDeclaredFields());
        return this;
    }

    public <T> Base select(boolean prefixCondition, Class<T> clazz) {
        this.select(prefixCondition, clazz.getDeclaredFields());
        return this;
    }

    /**
     * === 以下MYSQL case when统计 ===
     */

    public <T> Base selectCaseWhen(String caseWhen, String then, String els, String alias) {
        this.select("ifnull(sum(case when (" + caseWhen + ") then " + then + " else " + els + " end), 0) as " + DOUBLE_QUOTES + alias + DOUBLE_QUOTES);
        return this;
    }

    public <T> Base selectCaseWhen(String caseWhen, String alias) {
        this.selectCaseWhen(caseWhen, "1", "0", alias);
        return this;
    }

    public <T> Base selectPercentage(String dividend, String divisor, int nPoint, String alias) {
        this.select("concat(truncate(" + dividend + " / " + divisor + " * 100, " + nPoint + "), '%') as " + alias);
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

    public abstract <L, R> Base innerJoin(Class clazz, SFunction<L, ?> leftFunc, SFunction<R, ?> rightFunc);

    public abstract Base leftJoin(Class clazz, Field leftField, Field rightField);

    public abstract <L, R> Base leftJoin(Class clazz, SFunction<L, ?> leftFunc, SFunction<R, ?> rightFunc);

    public abstract Base rightJoin(Class clazz, Field leftField, Field rightField);

    public abstract <L, R> Base rightJoin(Class clazz, SFunction<L, ?> leftFunc, SFunction<R, ?> rightFunc);

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
        this.queryWrapper.apply(condition, (leftTableName + DOT + leftColumn) + SPACE + EQUAL + SPACE + (rightTableName + DOT + rightColumn));
        return this;
    }

    public <L, R> Base eq(boolean condition, SFunction<L, ?> leftFunc, SFunction<R, ?> rightFunc) {
        return this.eq(condition, ReflectUtils.lambdaField(leftFunc), ReflectUtils.lambdaField(rightFunc));
    }

    public Base eq(boolean condition, String column, Object val) {
        this.queryWrapper.eq(condition, column, val);
        return this;
    }

    public <T> Base eq(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.eq(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base ne(boolean condition, String column, Object val) {
        this.queryWrapper.ne(condition, column, val);
        return this;
    }

    public <T> Base ne(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.ne(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base gt(boolean condition, String column, Object val) {
        this.queryWrapper.gt(condition, column, val);
        return this;
    }

    public <T> Base gt(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.gt(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base ge(boolean condition, String column, Object val) {
        this.queryWrapper.ge(condition, column, val);
        return this;
    }

    public <T> Base ge(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.ge(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base lt(boolean condition, String column, Object val) {
        this.queryWrapper.lt(condition, column, val);
        return this;
    }

    public <T> Base lt(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.lt(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base le(boolean condition, String column, Object val) {
        this.queryWrapper.le(condition, column, val);
        return this;
    }

    public <T> Base le(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.le(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base between(boolean condition, String column, Object val1, Object val2) {
        this.queryWrapper.between(condition, column, val1, val2);
        return this;
    }

    public <T> Base between(boolean condition, SFunction<T, ?> sFunc, Object val1, Object val2) {
        this.queryWrapper.between(condition, lambdaColumn(sFunc), val1, val2);
        return this;
    }

    public Base notBetween(boolean condition, String column, Object val1, Object val2) {
        this.queryWrapper.notBetween(condition, column, val1, val2);
        return this;
    }

    public <T> Base notBetween(boolean condition, SFunction<T, ?> sFunc, Object val1, Object val2) {
        this.queryWrapper.notBetween(condition, lambdaColumn(sFunc), val1, val2);
        return this;
    }

    public Base like(boolean condition, String column, Object val) {
        this.queryWrapper.like(condition, column, val);
        return this;
    }

    public <T> Base like(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.like(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base notLike(boolean condition, String column, Object val) {
        this.queryWrapper.notLike(condition, column, val);
        return this;
    }

    public <T> Base notLike(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.notLike(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base likeLeft(boolean condition, String column, Object val) {
        this.queryWrapper.likeLeft(condition, column, val);
        return this;
    }

    public <T> Base likeLeft(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.likeLeft(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base likeRight(boolean condition, String column, Object val) {
        this.queryWrapper.likeRight(condition, column, val);
        return this;
    }

    public <T> Base likeRight(boolean condition, SFunction<T, ?> sFunc, Object val) {
        this.queryWrapper.likeRight(condition, lambdaColumn(sFunc), val);
        return this;
    }

    public Base isNull(boolean condition, String column) {
        this.queryWrapper.isNull(condition, column);
        return this;
    }

    public <T> Base isNull(boolean condition, SFunction<T, ?> sFunc) {
        this.queryWrapper.isNull(condition, lambdaColumn(sFunc));
        return this;
    }

    public Base isNotNull(boolean condition, String column) {
        this.queryWrapper.isNotNull(condition, column);
        return this;
    }

    public <T> Base isNotNull(boolean condition, SFunction<T, ?> sFunc) {
        this.queryWrapper.isNotNull(condition, lambdaColumn(sFunc));
        return this;
    }

    public Base in(boolean condition, String column, Collection<?> coll) {
        this.queryWrapper.in(condition, column, coll);
        return this;
    }

    public <T> Base in(boolean condition, SFunction<T, ?> sFunc, Collection<?> coll) {
        this.queryWrapper.in(condition, lambdaColumn(sFunc), coll);
        return this;
    }

    public Base notIn(boolean condition, String column, Collection<?> coll) {
        this.queryWrapper.notIn(condition, column, coll);
        return this;
    }

    public <T> Base notIn(boolean condition, SFunction<T, ?> sFunc, Collection<?> coll) {
        this.queryWrapper.notIn(condition, lambdaColumn(sFunc), coll);
        return this;
    }

    public Base inSql(boolean condition, String column, String inValue) {
        this.queryWrapper.inSql(condition, column, inValue);
        return this;
    }

    public <T> Base inSql(boolean condition, SFunction<T, ?> sFunc, String inValue) {
        this.queryWrapper.inSql(condition, lambdaColumn(sFunc), inValue);
        return this;
    }

    public Base notInSql(boolean condition, String column, String inValue) {
        this.queryWrapper.notInSql(condition, column, inValue);
        return this;
    }

    public <T> Base notInSql(boolean condition, SFunction<T, ?> sFunc, String inValue) {
        this.queryWrapper.notInSql(condition, lambdaColumn(sFunc), inValue);
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
     * === 以下是否打印日志 ===
     */

    public Base log() {
        this.logFlag = true;
        return this;
    }

    public Base log(String logPrefix) {
        this.logPrefixName = logPrefix;
        this.logFlag = true;
        return this;
    }

    private void printLog() {
        if (this.logFlag) {
            try {
                logger.info(logPrefixName + ": {}", initSql());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printLog(String initSql) {
        if (this.logFlag) {
            logger.info(logPrefixName + ": {}", initSql);
        }
    }

    public String printSql() {
        try {
            return initSql();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * === 以下方式获取请求结果 ===
     */

    public Map<String, Object> queryForMap() {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.queryForMap(this.prefixSql, this.queryWrapper);
    }

    public IPage<Map<String, Object>> queryForPage(IPage<?> page) {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.queryForPage(page, this.prefixSql, this.queryWrapper);
    }

    public List<Map<String, Object>> queryForList() {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        return baseService.queryForList(this.prefixSql, this.queryWrapper);
    }

    public Map<String, Object> queryForMap(String DBName) {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        Map<String, Object> queryMap = null;
        try {
            DynamicDataSourceContextHolder.push(DBName);
            queryMap = baseService.queryForMap(this.prefixSql, this.queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
        return queryMap;
    }

    public IPage<Map<String, Object>> queryForPage(IPage<?> page, String DBName) {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        IPage<Map<String, Object>> queryMapPage = null;
        try {
            DynamicDataSourceContextHolder.push(DBName);
            queryMapPage = baseService.queryForPage(page, this.prefixSql, this.queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
        return queryMapPage;
    }

    public List<Map<String, Object>> queryForList(String DBName) {
        printLog();
        IBaseService baseService = SpringContext.getBean(IBaseService.class);
        List<Map<String, Object>> queryMapList = null;
        try {
            DynamicDataSourceContextHolder.push(DBName);
            queryMapList = baseService.queryForList(this.prefixSql, this.queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
        return queryMapList;
    }

    /**
     * === 以下lambda转换方法 ===
     */

    private <T> String lambdaColumn(SFunction<T, ?> func) {
        SerializedLambda serializedLambda = LambdaUtils.resolve(func);
        String tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, serializedLambda.getImplClass().getSimpleName());
        String columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, PropertyNamer.methodToProperty(serializedLambda.getImplMethodName()));
        return tableName + DOT + columnName;
    }

}
