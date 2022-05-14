package com.sonin.modules.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/5/11 17:02
 */
@Slf4j
public class CompletableFutureDemo {

    /**
     * <pre>
     * 创建CompletableFuture
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void testCompletableFuture1() {
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("Hello World!");
        try {
            String result = completableFuture.get();
            log.info("CompletableFuture结果: {}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * runAsync无返回值
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void testCompletableFuture2() {
        AtomicLong atomicLong = new AtomicLong();
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            log.info("当前线程: {}", Thread.currentThread().getName());
            atomicLong.set(100);
        });
        runAsync.join();
        log.info("CompletableFuture结果: {}", atomicLong.get());
    }

    /**
     * <pre>
     * runAsync无返回值
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void testCompletableFuture3() throws Exception {
        AtomicLong atomicLong = new AtomicLong();
        CompletableFuture<Long> supplyAsync = CompletableFuture.supplyAsync(() -> {
            log.info("当前线程: {}", Thread.currentThread().getName());
            atomicLong.set(100);
            return atomicLong.get();
        });
        log.info("CompletableFuture结果: {}", supplyAsync.get());
    }

    /**
     * <pre>
     * allOf
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    private void testCompletableFuture4() throws Exception {
        CompletableFuture.allOf(Stream.of("1", "2", "3").map(item -> CompletableFuture.runAsync(() -> System.out.println(item))).toArray(CompletableFuture[]::new)).join();
    }

    private void testCompletableFuture5() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 100).thenApply(number -> number * 3);
        System.out.println(future.get());
    }

    private void testCompletableFuture6() throws Exception {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 100;
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "200");
        CompletableFuture<Object> future3 = CompletableFuture.anyOf(future1, future2);
        System.out.println(future3.get());
    }

    public static void main(String[] args) throws Exception {
        CompletableFutureDemo demo = new CompletableFutureDemo();
        // demo.testCompletableFuture1();
        // demo.testCompletableFuture2();
        // demo.testCompletableFuture3();
        // demo.testCompletableFuture4();
        // demo.testCompletableFuture5();
        demo.testCompletableFuture6();
    }
}
