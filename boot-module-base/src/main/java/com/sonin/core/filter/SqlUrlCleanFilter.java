package com.sonin.core.filter;

import com.sonin.utils.SqlCleanUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

@Slf4j
public class SqlUrlCleanFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        HttpServletRequestWrapper cleanRequest = new HttpServletRequestWrapper(request) {

            /**
             * 新增：安全路径清洗（不改变 / 结构）
             */
            @Override
            public String getRequestURI() {
                String raw = super.getRequestURI();
                String cleaned = cleanPath(raw);
                if (!Objects.equals(raw, cleaned)) {
                    log.warn("Path 清洗: {} → {}", raw, cleaned);
                }
                return cleaned;
            }

            @Override
            public String getServletPath() {
                String raw = super.getServletPath();
                String cleaned = cleanPath(raw);
                if (!Objects.equals(raw, cleaned)) {
                    log.warn("ServletPath 清洗: {} → {}", raw, cleaned);
                }
                return cleaned;
            }


            /**
             * 🔥 保留你原来的参数清洗逻辑
             */
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                String cleaned = SqlCleanUtil.clean(value, value, "PARAM");
                if (!Objects.equals(value, cleaned)) {
                    log.info("参数清洗: {} = {} → {}", name, value, cleaned);
                }
                return cleaned;
            }

            @Override
            public String[] getParameterValues(String name) {
                String[] values = super.getParameterValues(name);
                if (values == null) {
                    return null;
                }

                List<String> cleanedList = new ArrayList<>();
                for (String v : values) {
                    String cleaned = SqlCleanUtil.clean(v, v, "PARAM");
                    if (!Objects.equals(v, cleaned)) {
                        log.info("参数清洗数组: {} = {} → {}", name, v, cleaned);
                    }
                    cleanedList.add(cleaned);
                }

                return cleanedList.toArray(new String[0]);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                Map<String, String[]> map = super.getParameterMap();
                Map<String, String[]> newMap = new HashMap<>();
                for (String key : map.keySet()) {
                    String[] values = map.get(key);
                    String[] cleaned = Arrays.stream(values)
                            .map(v -> {
                                String c = SqlCleanUtil.clean(v, v, "PARAM");
                                if (!Objects.equals(v, c)) {
                                    log.info("参数清洗 Map: {} = {} → {}", key, v, c);
                                }
                                return c;
                            }).toArray(String[]::new);
                    newMap.put(key, cleaned);
                }
                return newMap;
            }


            /**
             * =========================================================
             * 核心：安全路径清洗方法（不会破坏 /）
             * =========================================================
             */
            private String cleanPath(String path) {
                if (path == null) {
                    return null;
                }

                String cleaned = path;

                // 去掉危险字符，但保留 / 结构
                cleaned = cleaned
                        .replaceAll("[\"'<>\\\\;]", "")   // 删除危险符号
                        .replaceAll("[\\p{Cntrl}]", "");  // 删除控制字符

                return cleaned;
            }
        };

        chain.doFilter(cleanRequest, resp);
    }
}
