package com.concurrent.week7;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Week 7 - Day 5-7: LongAdder 长整型累加器
 * 
 * 学习目标：
 * 1. LongAdder 的设计思想
 * 2. 与 AtomicLong 的区别
 * 3. 高并发场景下的性能优势
 * 4. 适用场景
 */
public class LongAdderDemo {

    /**
     * 场景1：基本操作
     */
    public static void demonstrateBasicOperations() {
        System.out.println("========== LongAdder - 基本操作演示 ==========\n");

        LongAdder adder = new LongAdder();

        // add: 增加指定值
        adder.add(10);
        System.out.println("add(10) 后: " + adder.sum());

        adder.add(20);
        System.out.println("add(20) 后: " + adder.sum());

        // increment: 增加1
        adder.increment();
        System.out.println("increment() 后: " + adder.sum());

        // decrement: 减少1
        adder.decrement();
        System.out.println("decrement() 后: " + adder.sum());

        // sum: 获取当前总和
        System.out.println("当前总和: " + adder.sum());

        // reset: 重置为0
        adder.reset();
        System.out.println("reset() 后: " + adder.sum());

        // sumThenReset: 获取总和并重置
        adder.add(100);
        long sum = adder.sumThenReset();
        System.out.println("sumThenReset(): " + sum);
        System.out.println("重置后: " + adder.sum());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：高并发场景性能对比
     */
    public static void demonstratePerformanceComparison() {
        System.out.println("========== LongAdder vs AtomicLong - 性能对比 ==========\n");

        int threadCount = 10;
        int operationsPerThread = 100000;

        // LongAdder
        LongAdder longAdder = new LongAdder();
        Thread[] adderThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            adderThreads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    longAdder.increment();
                }
            });
        }

        long start1 = System.currentTimeMillis();
        for (Thread thread : adderThreads) {
            thread.start();
        }
        for (Thread thread : adderThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end1 = System.currentTimeMillis();
        System.out.println("LongAdder 耗时: " + (end1 - start1) + "ms");
        System.out.println("LongAdder 结果: " + longAdder.sum());

        // AtomicLong
        AtomicLong atomicLong = new AtomicLong(0);
        Thread[] atomicThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    atomicLong.incrementAndGet();
                }
            });
        }

        long start2 = System.currentTimeMillis();
        for (Thread thread : atomicThreads) {
            thread.start();
        }
        for (Thread thread : atomicThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println("AtomicLong 耗时: " + (end2 - start2) + "ms");
        System.out.println("AtomicLong 结果: " + atomicLong.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：统计场景应用
     */
    public static void demonstrateStatistics() {
        System.out.println("========== LongAdder - 统计场景演示 ==========\n");

        LongAdder requestCount = new LongAdder();
        LongAdder successCount = new LongAdder();
        LongAdder errorCount = new LongAdder();

        // 模拟请求处理
        Thread[] processors = new Thread[5];
        for (int i = 0; i < 5; i++) {
            processors[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    requestCount.increment();
                    if (Math.random() > 0.1) {
                        successCount.increment();
                    } else {
                        errorCount.increment();
                    }
                }
            });
        }

        for (Thread processor : processors) {
            processor.start();
        }

        for (Thread processor : processors) {
            try {
                processor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("总请求数: " + requestCount.sum());
        System.out.println("成功数: " + successCount.sum());
        System.out.println("失败数: " + errorCount.sum());
        System.out.println("成功率: " + (successCount.sum() * 100.0 / requestCount.sum()) + "%");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：LongAdder 原理说明
     */
    public static void demonstratePrinciple() {
        System.out.println("========== LongAdder - 原理说明 ==========\n");

        System.out.println("LongAdder 设计思想:");
        System.out.println("1. 采用分段累加的策略");
        System.out.println("2. 内部维护一个 base 值和多个 Cell 数组");
        System.out.println("3. 每个线程操作不同的 Cell，减少竞争");
        System.out.println("4. 最终结果 = base + 所有 Cell 的和");
        System.out.println("\n与 AtomicLong 的区别:");
        System.out.println("- AtomicLong: 单个变量，高并发时 CAS 竞争激烈");
        System.out.println("- LongAdder: 分段累加，减少竞争，提高性能");
        System.out.println("\n适用场景:");
        System.out.println("- 高并发写多读少的场景");
        System.out.println("- 统计计数、累加等操作");
        System.out.println("- 不需要实时精确值的场景");
        System.out.println("\n注意事项:");
        System.out.println("- sum() 方法可能不是实时的（最终一致性）");
        System.out.println("- 需要实时精确值时使用 AtomicLong");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstratePerformanceComparison();
        demonstrateStatistics();
        demonstratePrinciple();
    }
}

