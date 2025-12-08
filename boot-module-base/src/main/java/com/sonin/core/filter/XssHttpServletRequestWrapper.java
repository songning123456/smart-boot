package com.sonin.core.filter;


import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防止sql注入,xss攻击
 * 前端可以对输入信息做预处理，后端也可以做处理。
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static String key = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|case";
    private static Set<String> notAllowedKeyWords = new HashSet<String>(0);
    private static String replacedString="INVALID";

    private static List<String> keyWordList = new ArrayList<>();

    static {
        String[] keyStr = key.split("\\|");
        for (String str : keyStr) {
            notAllowedKeyWords.add(str);
            // 获取所有可能的大小写组合
            keyWordList.addAll(generatePermutations(str));
        }
    }

    private String currentUrl;

    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
        currentUrl = servletRequest.getRequestURI();
    }


    /**覆盖getParameter方法，将参数名和参数值都做xss过滤。
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        //return HtmlUtil.filter(value);
        return cleanXSS(value);
    }
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }
    @Override
    public Map<String, String[]> getParameterMap(){
        Map<String, String[]> values=super.getParameterMap();
        if (values == null) {
            return null;
        }
        Map<String, String[]> result=new HashMap<>();
        for(String key:values.keySet()){
            String encodedKey=cleanXSS(key);
            int count=values.get(key).length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++){
                encodedValues[i]=cleanXSS(values.get(key)[i]);
            }
            result.put(encodedKey,encodedValues);
        }
        return result;
    }
    /**
     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }
//    private String cleanXSS(String valueP) {
//        // You'll need to remove the spaces from the html entities below
//        String value = valueP.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
//        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
//        //value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
//        value = value.replaceAll("'", "& #39;");
//        value = value.replaceAll("eval\\((.*)\\)", "");
//        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
//        value = value.replaceAll("script", "");
//        value = cleanSqlKeyWords(value);
//        return value;
//    }
    @SuppressWarnings("deprecation")
	private String cleanXSS(String valueP) {
        if (valueP == null) {
            return null;
        }
        // 使用Apache Commons Text库进行HTML实体转义
        //System.out.println("元数据====="+valueP);
        String value = StringEscapeUtils.escapeHtml4(valueP);
        // 移除JavaScript事件处理器和伪协议
        Pattern scriptPattern = Pattern.compile(
                "(?i)(<script[^>]*>.*?</script.*?>|<[^>]*\\b(onerror|onclick|onload|onsubmit|onblur|onchange|onfocus|javascript:|data:|vbscript:|mocha:)[^>]*>.*?</[^>]*>)",Pattern.DOTALL);
        Matcher scriptMatcher = scriptPattern.matcher(value);
        value = scriptMatcher.replaceAll("");

        // 移除数据 URI
        Pattern dataUriPattern = Pattern.compile(
                "(?i)data:(?!image|video|audio).*?(;|,|$)", Pattern.DOTALL);
        Matcher dataUriMatcher = dataUriPattern.matcher(value);
        value = dataUriMatcher.replaceAll("");

        // 移除或转义其他潜在的XSS攻击代码
        Pattern xssPattern = Pattern.compile(
                "(?i)<[^>]*>|[^>]*javascript:.*?|[^>]*data:.*?(;|,|$)", Pattern.DOTALL);
        Matcher xssMatcher = xssPattern.matcher(value);
        value = xssMatcher.replaceAll("");

        // 清洗SQL关键字
        value = cleanSqlKeyWords(value);
        //System.out.println("元数据====="+valueP+"|清洗后数据====="+value);
        return value;
    }

    private String cleanSqlKeyWords(String value) {
        String paramValue = value;
        for (String keyword : keyWordList) {
            if (paramValue.length() > keyword.length() + 4 && (paramValue.contains(" "+keyword)||paramValue.contains(keyword+" ")||paramValue.contains(" "+keyword+" "))) {
                paramValue = StringUtils.replace(paramValue, keyword, replacedString);
                log.error(this.currentUrl + "已被过滤，因为参数中包含不允许sql的关键词(" + keyword+ ")"+";参数："+value+";过滤后的参数："+paramValue);
            }
        }
        return paramValue;
    }

    private static List<String> generatePermutations(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            result.add("");
            return result;
        }
        backtrack(input.toCharArray(), 0, result);
        return result;
    }

    private static void backtrack(char[] chars, int index, List<String> result) {
        if (index == chars.length) {
            result.add(new String(chars));
            return;
        }

        // 处理当前字符为小写的情况
        chars[index] = Character.toLowerCase(chars[index]);
        backtrack(chars, index + 1, result);

        // 如果当前字符是字母，再处理大写的情况
        if (Character.isLetter(chars[index])) {
            chars[index] = Character.toUpperCase(chars[index]);
            backtrack(chars, index + 1, result);
        }
    }

}