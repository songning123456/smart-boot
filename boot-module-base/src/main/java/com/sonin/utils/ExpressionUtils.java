package com.sonin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * <pre>
 * 表达式解析
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/7/22 上午8:14
 */
public class ExpressionUtils {

    /**
     * 1. 将中缀表达式转成中缀字符串列表，方便遍历
     *
     * @param expression
     * @return
     */
    private List<String> toInfixList(String expression) {
        List<String> infixList = new ArrayList<>();
        int i = 0;
        // 保存遍历过程中产生的数字，连续的数字字符拼接成一个数
        StringBuilder stringBuilder = new StringBuilder();
        while (i < expression.length()) {
            if (' ' == expression.charAt(i)) {
                // 如果是' '，则直接跳过
                i++;
            } else if (!Character.isDigit(expression.charAt(i))) {
                // 如果当前字符不是数字，直接加入结果
                infixList.add(expression.charAt(i) + "");
                i++;
            } else {
                // 先清除
                stringBuilder.delete(0, stringBuilder.length());
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    stringBuilder.append(expression.charAt(i));
                    i++;
                }
                infixList.add(stringBuilder.toString());
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
    private List<String> toSuffixList(List<String> infixList) {
        // 符号栈
        Stack<String> operatorStack = new Stack<>();
        // 存储中间结果
        // 在整个转换过程中，没有pop操作，在后面还要逆序输出，所以用list代替栈
        List<String> suffixList = new ArrayList<>();
        for (String item : infixList) {
            // 如果是数字，加入suffixList
            if (item.matches("\\d+")) {
                suffixList.add(item);
            } else if ("(".equals(item)) {
                // 如果是左括号，入栈operatorStack
                operatorStack.push(item);
            } else if (")".equals(item)) {
                // 如果是右括号，则依次弹出符号栈operatorStack栈顶的运算符，并压入suffixList，直到遇到左括号位置，将这一对括号消除掉
                while (!"(".equals(operatorStack.peek())) {
                    suffixList.add(operatorStack.pop());
                }
                // 丢弃左括号，继续循环，也就消除了一对括号
                operatorStack.pop();
            } else {
                // 此时，遇到的是加减乘除运算符
                // 当前操作符优先级<=operatorStack的栈顶运算符的优先级，则将s1的运算符弹出，并加入到s2中
                while (!operatorStack.empty() && priority(operatorStack.peek()) >= priority(item)) {
                    suffixList.add(operatorStack.pop());
                }
                operatorStack.push(item);
            }
        }
        // 将operatorStack中剩余的运算符依次弹出，并加入suffixList
        while (!operatorStack.empty()) {
            suffixList.add(operatorStack.pop());
        }
        return suffixList;
    }

    /**
     * 对后缀表达式字符串列表进行计算
     *
     * @param suffixList
     * @return
     */
    public double calculateSuffix(List<String> suffixList) {
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
                    stack.push(Double.parseDouble(suffix));
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
    private int priority(String operator) {
        if ("+".equals(operator) || "-".equals(operator)) {
            return 1;
        }
        if ("*".equals(operator) || "/".equals(operator)) {
            return 2;
        }
        // 如果是左括号返回0
        return 0;
    }

    public static void main(String[] args) {
        String expression = "12 + (( 22 + 31 ) * 4 ) - 5";
        ExpressionUtils app = new ExpressionUtils();
        // 第1步
        List<String> infixList = app.toInfixList(expression);
        System.out.println(infixList);
        // 第2步
        List<String> suffixList = app.toSuffixList(infixList);
        System.out.println(suffixList);
        // 第3步
        double res = app.calculateSuffix(suffixList);
        System.out.println(res);
    }

}
