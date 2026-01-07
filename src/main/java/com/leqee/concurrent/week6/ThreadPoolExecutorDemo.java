package com.leqee.concurrent.week6;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Week 6 - Day 1-3: ThreadPoolExecutor 线程池
 * 
 * 学习目标：
 * 1. ThreadPoolExecutor 的核心参数
 * 2. 线程池的工作原理
 * 3. 拒绝策略
 * 4. 线程池的创建和配置
 */
public class ThreadPoolExecutorDemo {

    /**
     * 自定义线程工厂
     */
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * 场景1：创建自定义线程池
     */
    public static void demonstrateCustomThreadPool() {
        System.out.println("========== ThreadPoolExecutor - 自定义线程池 ==========\n");

        // 核心参数说明：
        // corePoolSize: 核心线程数
        // maximumPoolSize: 最大线程数
        // keepAliveTime: 空闲线程存活时间
        // unit: 时间单位
        // workQueue: 工作队列
        // threadFactory: 线程工厂
        // handler: 拒绝策略

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // corePoolSize: 核心线程数
            5,                      // maximumPoolSize: 最大线程数
            60L,                    // keepAliveTime: 空闲线程存活时间
            TimeUnit.SECONDS,       // unit: 时间单位
            new LinkedBlockingQueue<>(10), // workQueue: 工作队列
            new CustomThreadFactory("CustomPool"), // threadFactory: 线程工厂
            new ThreadPoolExecutor.CallerRunsPolicy() // handler: 拒绝策略
        );

        // 提交任务
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务-" + taskId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 关闭线程池
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：线程池工作原理演示
     */
    public static void demonstrateThreadPoolWorkflow() {
        System.out.println("========== ThreadPoolExecutor - 工作原理演示 ==========\n");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 4, 5L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3),
            new CustomThreadFactory("WorkflowPool"),
            new ThreadPoolExecutor.AbortPolicy()
        );

        System.out.println("线程池状态:");
        System.out.println("  核心线程数: " + executor.getCorePoolSize());
        System.out.println("  最大线程数: " + executor.getMaximumPoolSize());
        System.out.println("  当前线程数: " + executor.getPoolSize());
        System.out.println("  活跃线程数: " + executor.getActiveCount());
        System.out.println("  队列大小: " + executor.getQueue().size());

        // 提交任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("任务-" + taskId + " 执行中，当前线程数: " + executor.getPoolSize() +
                                 ", 活跃线程数: " + executor.getActiveCount() +
                                 ", 队列大小: " + executor.getQueue().size());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：拒绝策略演示
     */
    public static void demonstrateRejectionPolicy() {
        System.out.println("========== ThreadPoolExecutor - 拒绝策略演示 ==========\n");

        // 策略1: AbortPolicy - 直接抛出异常
        System.out.println("--- AbortPolicy ---");
        ThreadPoolExecutor executor1 = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor1.execute(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (RejectedExecutionException e) {
            System.out.println("任务被拒绝: " + e.getMessage());
        }
        executor1.shutdown();

        // 策略2: CallerRunsPolicy - 调用者运行
        System.out.println("\n--- CallerRunsPolicy ---");
        ThreadPoolExecutor executor2 = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor2.execute(() -> {
                System.out.println("执行任务-" + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor2.shutdown();

        // 策略3: DiscardPolicy - 直接丢弃
        System.out.println("\n--- DiscardPolicy ---");
        ThreadPoolExecutor executor3 = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardPolicy()
        );

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor3.execute(() -> {
                System.out.println("执行任务-" + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor3.shutdown();

        // 策略4: DiscardOldestPolicy - 丢弃最老的任务
        System.out.println("\n--- DiscardOldestPolicy ---");
        ThreadPoolExecutor executor4 = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor4.execute(() -> {
                System.out.println("执行任务-" + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor4.shutdown();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：线程池监控
     */
    public static void demonstrateMonitoring() {
        System.out.println("========== ThreadPoolExecutor - 线程池监控 ==========\n");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 5, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10),
            new CustomThreadFactory("MonitorPool"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 提交任务
        for (int i = 0; i < 15; i++) {
            final int taskId = i;
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 监控线程池状态
        Thread monitor = new Thread(() -> {
            while (!executor.isShutdown()) {
                System.out.println("监控信息:");
                System.out.println("  核心线程数: " + executor.getCorePoolSize());
                System.out.println("  最大线程数: " + executor.getMaximumPoolSize());
                System.out.println("  当前线程数: " + executor.getPoolSize());
                System.out.println("  活跃线程数: " + executor.getActiveCount());
                System.out.println("  已完成任务数: " + executor.getCompletedTaskCount());
                System.out.println("  队列大小: " + executor.getQueue().size());
                System.out.println("  总任务数: " + executor.getTaskCount());
                System.out.println("---");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        monitor.start();

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
            monitor.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateCustomThreadPool();
        demonstrateThreadPoolWorkflow();
        demonstrateRejectionPolicy();
        demonstrateMonitoring();
    }
}

