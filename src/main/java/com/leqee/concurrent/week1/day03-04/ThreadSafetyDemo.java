package com.leqee.concurrent.week1;

/**
 * Week 1 - Day 3-4: 线程安全问题演示
 * 
 * 学习目标：
 * 1. 理解什么是线程安全
 * 2. 观察竞态条件（Race Condition）
 * 3. 理解共享变量的可见性问题
 * 4. 理解原子性问题
 */
public class ThreadSafetyDemo {

    /**
     * 线程不安全的计数器
     * 
     * 问题：
     * - count++ 不是原子操作，包含三个步骤：
     *   1. 读取 count 的值
     *   2. 将 count 加 1
     *   3. 将结果写回 count
     * - 多个线程同时执行时，可能丢失更新
     */
    static class UnsafeCounter {
        private int count = 0;

        public void increment() {
            count++; // 不是原子操作！
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * 线程安全的计数器（使用 synchronized）
     */
    static class SafeCounter {
        private int count = 0;

        // 方式1：同步方法
        public synchronized void increment() {
            count++;
        }

        // 方式2：同步代码块
        public void increment2() {
            synchronized (this) {
                count++;
            }
        }

        public synchronized int getCount() {
            return count;
        }
    }

    /**
     * 演示线程不安全的问题
     */
    public static void demonstrateUnsafeCounter() {
        System.out.println("========== 线程不安全演示 ==========\n");

        UnsafeCounter counter = new UnsafeCounter();
        int threadCount = 10;
        int incrementsPerThread = 10000;
        int expectedCount = threadCount * incrementsPerThread;

        Thread[] threads = new Thread[threadCount];

        // 创建多个线程同时增加计数
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        int actualCount = counter.getCount();
        System.out.println("期望值: " + expectedCount);
        System.out.println("实际值: " + actualCount);
        System.out.println("丢失的更新: " + (expectedCount - actualCount));
        System.out.println("执行时间: " + (endTime - startTime) + "ms");

        if (actualCount != expectedCount) {
            System.out.println("❌ 线程不安全！数据丢失了！");
        } else {
            System.out.println("✅ 巧合，数据正确（但这不是线程安全的）");
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示线程安全的解决方案
     */
    public static void demonstrateSafeCounter() {
        System.out.println("========== 线程安全演示（使用 synchronized）==========\n");

        SafeCounter counter = new SafeCounter();
        int threadCount = 10;
        int incrementsPerThread = 10000;
        int expectedCount = threadCount * incrementsPerThread;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
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

        int actualCount = counter.getCount();
        System.out.println("期望值: " + expectedCount);
        System.out.println("实际值: " + actualCount);
        System.out.println("执行时间: " + (endTime - startTime) + "ms");

        if (actualCount == expectedCount) {
            System.out.println("✅ 线程安全！数据正确！");
        } else {
            System.out.println("❌ 不应该出现这种情况");
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示可见性问题
     * 
     * 问题：一个线程修改了共享变量，另一个线程可能看不到
     */
    static class VisibilityProblem {
        // 不加 volatile，可能出现可见性问题
        private boolean flag = false;

        public void setFlag() {
            flag = true;
            System.out.println("Flag 设置为 true");
        }

        public void checkFlag() {
            // 可能永远看不到 flag 变为 true（由于 CPU 缓存）
            while (!flag) {
                // 空循环
            }
            System.out.println("Flag 变为 true，退出循环");
        }
    }

    /**
     * 解决可见性问题（使用 volatile）
     */
    static class VisibilitySolution {
        // 使用 volatile 保证可见性
        private volatile boolean flag = false;

        public void setFlag() {
            flag = true;
            System.out.println("Flag 设置为 true");
        }

        public void checkFlag() {
            while (!flag) {
                // 空循环
            }
            System.out.println("Flag 变为 true，退出循环");
        }
    }

    /**
     * 演示可见性问题
     */
    public static void demonstrateVisibilityProblem() {
        System.out.println("========== 可见性问题演示 ==========\n");

        VisibilityProblem problem = new VisibilityProblem();

        // 线程1：检查 flag
        Thread t1 = new Thread(() -> {
            System.out.println("线程1开始检查 flag...");
            problem.checkFlag();
        });

        // 线程2：设置 flag
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000); // 延迟1秒
                problem.setFlag();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- 使用 volatile 解决可见性问题 ---\n");

        VisibilitySolution solution = new VisibilitySolution();

        Thread t3 = new Thread(() -> {
            System.out.println("线程3开始检查 flag...");
            solution.checkFlag();
        });

        Thread t4 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                solution.setFlag();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t3.start();
        t4.start();

        try {
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        // 演示线程不安全
        demonstrateUnsafeCounter();

        // 演示线程安全解决方案
        demonstrateSafeCounter();

        // 演示可见性问题
        demonstrateVisibilityProblem();
    }
}
