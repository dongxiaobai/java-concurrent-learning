package com.concurrent.week7;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Week 7 - Day 1-2: AtomicInteger 原子整数
 * 
 * 学习目标：
 * 1. AtomicInteger 的基本操作
 * 2. CAS（Compare-And-Swap）操作
 * 3. 原子操作的性能优势
 * 4. 与 volatile 和 synchronized 的区别
 */
public class AtomicIntegerDemo {

    /**
     * 场景1：基本原子操作
     */
    public static void demonstrateBasicOperations() {
        System.out.println("========== AtomicInteger - 基本操作演示 ==========\n");

        AtomicInteger atomicInt = new AtomicInteger(0);

        // get 和 set
        System.out.println("初始值: " + atomicInt.get());
        atomicInt.set(10);
        System.out.println("set(10) 后: " + atomicInt.get());

        // getAndIncrement: 先获取再递增
        System.out.println("getAndIncrement(): " + atomicInt.getAndIncrement());
        System.out.println("当前值: " + atomicInt.get());

        // incrementAndGet: 先递增再获取
        System.out.println("incrementAndGet(): " + atomicInt.incrementAndGet());
        System.out.println("当前值: " + atomicInt.get());

        // getAndAdd: 先获取再加值
        System.out.println("getAndAdd(5): " + atomicInt.getAndAdd(5));
        System.out.println("当前值: " + atomicInt.get());

        // addAndGet: 先加值再获取
        System.out.println("addAndGet(5): " + atomicInt.addAndGet(5));
        System.out.println("当前值: " + atomicInt.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：CAS 操作
     */
    public static void demonstrateCAS() {
        System.out.println("========== AtomicInteger - CAS 操作演示 ==========\n");

        AtomicInteger atomicInt = new AtomicInteger(10);

        // compareAndSet: CAS 操作
        System.out.println("当前值: " + atomicInt.get());
        boolean success1 = atomicInt.compareAndSet(10, 20);
        System.out.println("compareAndSet(10, 20): " + success1 + ", 当前值: " + atomicInt.get());

        boolean success2 = atomicInt.compareAndSet(10, 30);
        System.out.println("compareAndSet(10, 30): " + success2 + ", 当前值: " + atomicInt.get());

        // getAndUpdate: 原子更新
        int oldValue = atomicInt.getAndUpdate(x -> x * 2);
        System.out.println("getAndUpdate(x -> x * 2): 旧值=" + oldValue + ", 新值=" + atomicInt.get());

        // updateAndGet: 原子更新并获取
        int newValue = atomicInt.updateAndGet(x -> x + 10);
        System.out.println("updateAndGet(x -> x + 10): " + newValue);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：多线程并发操作
     */
    public static void demonstrateConcurrentOperations() {
        System.out.println("========== AtomicInteger - 并发操作演示 ==========\n");

        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 10;
        int operationsPerThread = 1000;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    counter.incrementAndGet();
                }
            }, "Thread-" + i);
        }

        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();

        int expected = threadCount * operationsPerThread;
        int actual = counter.get();
        System.out.println("期望值: " + expected);
        System.out.println("实际值: " + actual);
        System.out.println("耗时: " + (endTime - startTime) + "ms");

        if (actual == expected) {
            System.out.println("✅ 线程安全，数据正确！");
        } else {
            System.out.println("❌ 数据不一致");
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：性能对比
     */
    public static void demonstratePerformance() {
        System.out.println("========== AtomicInteger - 性能对比演示 ==========\n");

        int iterations = 10000000;
        int threadCount = 4;

        // AtomicInteger
        AtomicInteger atomicCounter = new AtomicInteger(0);
        Thread[] atomicThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < iterations / threadCount; j++) {
                    atomicCounter.incrementAndGet();
                }
            });
        }

        long start1 = System.currentTimeMillis();
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
        long end1 = System.currentTimeMillis();
        System.out.println("AtomicInteger 耗时: " + (end1 - start1) + "ms, 结果: " + atomicCounter.get());

        // synchronized
        int[] syncCounter = {0};
        Object lock = new Object();
        Thread[] syncThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            syncThreads[i] = new Thread(() -> {
                for (int j = 0; j < iterations / threadCount; j++) {
                    synchronized (lock) {
                        syncCounter[0]++;
                    }
                }
            });
        }

        long start2 = System.currentTimeMillis();
        for (Thread thread : syncThreads) {
            thread.start();
        }
        for (Thread thread : syncThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println("synchronized 耗时: " + (end2 - start2) + "ms, 结果: " + syncCounter[0]);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateCAS();
        demonstrateConcurrentOperations();
        demonstratePerformance();
    }
}

