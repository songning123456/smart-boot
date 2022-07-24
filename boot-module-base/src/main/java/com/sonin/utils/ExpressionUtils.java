package com.sonin.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * <pre>
 * 表达式解析
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/22 上午8:14
 */
@Slf4j
public class ExpressionUtils {

    /**
     * 1. 将中缀表达式转成中缀字符串列表，方便遍历
     *
     * @param expression
     * @return
     */
    private static List<String> toInfixList(String expression) {
        List<String> infixList = new ArrayList<>();
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (i < expression.length()) {
            if ('{' == expression.charAt(i)) {
                // 保存遍历过程中产生的数字，{...}格式拼接成一个数
                stringBuilder.delete(0, stringBuilder.length());
                i++;
                while (i < expression.length() && '}' != expression.charAt(i)) {
                    stringBuilder.append(expression.charAt(i));
                    i++;
                }
                infixList.add(stringBuilder.toString());
            } else if (Character.isDigit(expression.charAt(i))) {
                // 如果遇到数字，拼接成一个浮点类型的字符串
                stringBuilder.delete(0, stringBuilder.length());
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || '.' == expression.charAt(i))) {
                    stringBuilder.append(expression.charAt(i));
                    i++;
                }
                infixList.add(stringBuilder.toString());
            } else if ('+' == expression.charAt(i) || '-' == expression.charAt(i) || '*' == expression.charAt(i) || '/' == expression.charAt(i) || '(' == expression.charAt(i) || ')' == expression.charAt(i)) {
                // 如果当前字符是 +-*/()，直接加入结果
                infixList.add(expression.charAt(i) + "");
                i++;
            } else {
                // 直接跳过，e.g: ' '
                i++;
            }
        }
        return infixList;
    }


    /**
     * 2. 将中缀表达式字符串列表，转成后缀表达式字符串列表
     *
     * @param infixList
     * @return
     */
    public static List<String> toSuffixList(List<String> infixList) {
        // 符号栈
        Stack<String> operatorStack = new Stack<>();
        // 在整个转换过程中，没有pop操作，在后面还要逆序输出，所以用list代替栈
        List<String> suffixList = new ArrayList<>();
        for (String item : infixList) {
            if ("(".equals(item)) {
                // 如果是左括号，入栈operatorStack
                operatorStack.push(item);
            } else if (")".equals(item)) {
                // 如果是右括号，则依次弹出符号栈operatorStack栈顶的运算符，并压入suffixList，直到遇到左括号位置，将这一对括号消除掉
                while (!"(".equals(operatorStack.peek())) {
                    suffixList.add(operatorStack.pop());
                }
                // 丢弃左括号，继续循环，也就消除了一对括号
                operatorStack.pop();
            } else if ("+".equals(item) || "-".equals(item) || "*".equals(item) || "/".equals(item)) {
                // 此时，遇到的是加减乘除运算符
                // 当前操作符优先级<=operatorStack的栈顶运算符的优先级，则将operatorStack的运算符弹出，并加入到suffixList中
                while (!operatorStack.empty() && priority(operatorStack.peek()) >= priority(item)) {
                    suffixList.add(operatorStack.pop());
                }
                operatorStack.push(item);
            } else {
                suffixList.add(item);
            }
        }
        // 将operatorStack中剩余的运算符依次弹出，并加入suffixList
        while (!operatorStack.empty()) {
            suffixList.add(operatorStack.pop());
        }
        return suffixList;
    }

    /**
     * <pre>
     * 直接将表达式转成后缀表达式字符串列表
     * </pre>
     *
     * @param expression
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static List<String> toSuffixList(String expression) {
        return toSuffixList(toInfixList(expression));
    }

    /**
     * 对后缀表达式字符串列表进行计算
     *
     * @param suffixList
     * @return
     */
    public static double calculateSuffix(List<String> suffixList, Map<String, Double> expressionMap) {
        Stack<Double> stack = new Stack<>();
        for (String suffix : suffixList) {
            double n1, n2;
            switch (suffix) {
                case "*":
                    n1 = stack.pop();
                    n2 = stack.pop();
                    stack.push(n1 * n2);
                    break;
                case "/":
                    n1 = stack.pop();
                    n2 = stack.pop();
                    stack.push(n2 / n1);
                    break;
                case "+":
                    n1 = stack.pop();
                    n2 = stack.pop();
                    stack.push(n1 + n2);
                    break;
                case "-":
                    n1 = stack.pop();
                    n2 = stack.pop();
                    stack.push(n2 - n1);
                    break;
                default:
                    if (expressionMap.containsKey(suffix)) {
                        stack.push(expressionMap.get(suffix));
                    } else {
                        stack.push(Double.parseDouble(suffix));
                    }
                    break;
            }
        }
        return stack.pop();
    }

    /**
     * 符号优先级
     *
     * @param operator
     * @return
     */
    private static int priority(String operator) {
        if ("+".equals(operator) || "-".equals(operator)) {
            return 1;
        }
        if ("*".equals(operator) || "/".equals(operator)) {
            return 2;
        }
        // 如果是左括号返回0
        return 0;
    }

    /**
     * 表达式结果
     *
     * @param expression
     * @param expressionMap
     * @return
     */
    public static Double expressionResult(String expression, Map<String, Double> expressionMap) {
        return calculateSuffix(toSuffixList(toInfixList(expression)), expressionMap);
    }

    /**
     * 解析表达式指标
     *
     * @param expression
     * @return
     */
    public static List<String> parseExpression(String expression) {
        List<String> codeList = new ArrayList<>();
        int i = 0;
        // 保存遍历过程中产生的{...}，{...}格式拼接成一个指标
        StringBuilder stringBuilder = new StringBuilder();
        while (i < expression.length()) {
            if ('{' == expression.charAt(i)) {
                stringBuilder.delete(0, stringBuilder.length());
                i++;
                while (i < expression.length() && '}' != expression.charAt(i)) {
                    stringBuilder.append(expression.charAt(i));
                    i++;
                }
                codeList.add(stringBuilder.toString());
            } else {
                // 直接跳过
                i++;
            }
        }
        return codeList;
    }

    public static void main(String[] args) throws Exception {
        String expression = "({a} + (( {b} + {c} ) * {d} ) - {e}) / 2.0";
        Map<String, Double> expressionMap = new HashMap<String, Double>(5) {{
            put("a", 2D);
            put("b", 1D);
            put("c", 1D);
            put("d", 1D);
            put("e", 1D);
        }};
        List<String> codeList = parseExpression(expression);
        System.out.println(codeList);
        double expressionResult = expressionResult(expression, expressionMap);
        System.out.println(expressionResult);
    }

}
