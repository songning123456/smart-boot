package com.sonin.core.query;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author sonin
 * @date 2021/12/4 19:37
 * QueryWrapper条件e.g: demo_b.b_name = xxx
 */
public class Where extends Base {

    Where() {
        this.classes = new LinkedHashSet<>();
    }

    @Override
    public String initPrefixSql() {
        StringBuilder stringBuilder = new StringBuilder(SELECT + SPACE);
        if (this.selectedColumns != null && !this.selectedColumns.isEmpty()) {
            String selectedColumns = String.join(COMMA + SPACE, this.selectedColumns);
            stringBuilder.append(selectedColumns);
        } else {
            stringBuilder.append(initColumns());
        }
        stringBuilder.append(SPACE).append(FROM);
        for (Class clazz : classes) {
            String className = clazz.getSimpleName();
            String tableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
            stringBuilder.append(COMMA).append(SPACE).append(tableName);
        }
        return stringBuilder.toString().replaceFirst(FROM + COMMA, FROM);
    }

    @Override
    public Base from(Class... classes) {
        this.classes.addAll(Arrays.asList(classes));
        return this;
    }

    @Override
    public Base innerJoin(Class clazz, Field leftField, Field rightField) {
        return this;
    }

    @Override
    public Base leftJoin(Class clazz, Field leftField, Field rightField) {
        return this;
    }

    @Override
    public Base rightJoin(Class clazz, Field leftField, Field rightField) {
        return this;
    }

}
