package com.sonin.core.query;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

/**
 * @author sonin
 * @date 2021/12/4 19:31
 * QueryWrapper条件e.g: demo_b.b_name = xxx
 */
public class Join extends Base {

    private Class from;

    Join() {
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
        stringBuilder.append(SPACE).append(FROM).append(SPACE).append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.from.getSimpleName()));
        if (this.conditions != null && !this.conditions.isEmpty()) {
            stringBuilder.append(SPACE).append(String.join(SPACE, this.conditions));
        }
        return stringBuilder.toString();
    }

    @Override
    public Base from(Class... classes) {
        this.from = classes[0];
        this.classes.add(classes[0]);
        return this;
    }

    @Override
    public Base innerJoin(Class clazz, Field leftField, Field rightField) {
        directionJoin(clazz, leftField, rightField, INNER_JOIN);
        return this;
    }

    @Override
    public Base leftJoin(Class clazz, Field leftField, Field rightField) {
        directionJoin(clazz, leftField, rightField, LEFT_JOIN);
        return this;
    }

    @Override
    public Base rightJoin(Class clazz, Field leftField, Field rightField) {
        directionJoin(clazz, leftField, rightField, RIGHT_JOIN);
        return this;
    }

    private void directionJoin(Class clazz, Field leftField, Field rightField, String direction) {
        if (this.conditions == null) {
            this.conditions = new LinkedHashSet<>();
        }
        this.classes.add(leftField.getDeclaringClass());
        this.classes.add(rightField.getDeclaringClass());
        // e.g: demo_b
        String fromTableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        // e.g: demo_b
        String leftTableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, leftField.getDeclaringClass().getSimpleName());
        // e.g: a_id
        String leftColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, leftField.getName());
        // e.g: demo_a
        String rightTableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rightField.getDeclaringClass().getSimpleName());
        // e.g: id
        String rightColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rightField.getName());
        // e.g: inner/left/right join demo_b on demo_b.a_id = demo_a.id
        this.conditions.add(direction + SPACE + fromTableName + SPACE + ON + SPACE + leftTableName + DOT + leftColumn + SPACE + EQUAL + SPACE + rightTableName + DOT + rightColumn);
    }

    @Override
    public Base and(Field leftField, Field rightField) {
        return this;
    }

}
