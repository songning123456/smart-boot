package com.sonin.core.entity;

/**
 * <pre>
 * cash when 拼接
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/11/23 16:49
 */
public class CaseWhen {

    private String SPACE = " ";

    private String COMMA = ",";

    private String DOUBLE_QUOTES = "\"";

    private StringBuilder caseWhenSB = new StringBuilder();

    public CaseWhen selectCaseWhen(String caseWhen, String then, String els, String alias) {
        caseWhenSB.append(COMMA).append(SPACE).append("ifnull(sum(case when (").append(caseWhen).append(") then ").append(then).append(" else ").append(els).append(" end), 0) as ").append(DOUBLE_QUOTES).append(alias).append(DOUBLE_QUOTES);
        return this;
    }

    public CaseWhen selectCaseWhen(String caseWhen, String alias) {
        selectCaseWhen(caseWhen, "1", "0", alias);
        return this;
    }

    public CaseWhen selectPgCaseWhen(String caseWhen, String then, String els, String alias) {
        caseWhenSB.append(COMMA).append(SPACE).append("COALESCE(sum(case when (").append(caseWhen).append(") then cast(").append(then).append(" as float) else ").append(els).append(" end), 0) as ").append(DOUBLE_QUOTES).append(alias).append(DOUBLE_QUOTES);
        return this;
    }

    public CaseWhen selectPgCaseWhen(String caseWhen, String alias) {
        selectPgCaseWhen(caseWhen, "1", "0", alias);
        return this;
    }

    /**
     * <pre>
     * mysql返回BigDecimal类型; pg返回Double类型
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public String print() {
        return caseWhenSB.toString().replaceFirst(COMMA + SPACE, "");
    }

}
