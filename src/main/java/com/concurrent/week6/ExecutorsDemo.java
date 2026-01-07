package com.concurrent.week6;

import java.util.concurrent.*;

/**
 * Week 6 - Day 4-5: Executors 工具类
 * 
 * 学习目标：
 * 1. Executors 提供的各种线程池工厂方法
 * 2. 不同线程池的适用场景
 * 3. 线程池的选择原则
 */
public class ExecutorsDemo {

    /**
     * 场景1: newFixedThreadPool - 固定大小线程池
     */
    public static void demonstrateFixedThreadPool() {
        System.out.println("========== Executors - FixedThreadPool ==========\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
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

        executor.shutdown();
        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n特点：固定线程数，无界队列，适合执行长期任务");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2: newCachedThreadPool - 缓存线程池
     */
    public static void demonstrateCachedThreadPool() {
        System.out.println("========== Executors - CachedThreadPool ==========\n");

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务-" + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n特点：线程数可伸缩，适合执行大量短期异步任务");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3: newSingleThreadExecutor - 单线程线程池
     */
    public static void demonstrateSingleThreadExecutor() {
        System.out.println("========== Executors - SingleThreadExecutor ==========\n");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务-" + taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n特点：单线程执行，保证任务顺序执行");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4: newScheduledThreadPool - 定时任务线程池
     */
    public static void demonstrateScheduledThreadPool() {
        System.out.println("========== Executors - ScheduledThreadPool ==========\n");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        // 延迟执行
        System.out.println("--- 延迟执行 ---");
        executor.schedule(() -> {
            System.out.println("延迟3秒执行的任务");
        }, 3, TimeUnit.SECONDS);

        // 固定延迟执行
        System.out.println("--- 固定延迟执行 ---");
        executor.scheduleWithFixedDelay(() -> {
            System.out.println("固定延迟执行的任务");
        }, 1, 2, TimeUnit.SECONDS);

        // 固定频率执行
        System.out.println("--- 固定频率执行 ---");
        executor.scheduleAtFixedRate(() -> {
            System.out.println("固定频率执行的任务");
        }, 1, 2, TimeUnit.SECONDS);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n特点：支持定时和周期性任务");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景5: 使用 Future 获取任务结果
     */
    public static void demonstrateFuture() {
        System.out.println("========== Executors - Future 演示 ==========\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 提交有返回值的任务
        Future<Integer> future1 = executor.submit(() -> {
            Thread.sleep(1000);
            return 100;
        });

        Future<String> future2 = executor.submit(() -> {
            Thread.sleep(1500);
            return "Hello";
        });

        Future<Integer> future3 = executor.submit(() -> {
            Thread.sleep(2000);
            return 200;
        });

        try {
            System.out.println("等待任务完成...");
            System.out.println("结果1: " + future1.get());
            System.out.println("结果2: " + future2.get());
            System.out.println("结果3: " + future3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景6: 线程池选择建议
     */
    public static void demonstrateSelectionGuide() {
        System.out.println("========== Executors - 线程池选择指南 ==========\n");

        System.out.println("1. FixedThreadPool:");
        System.out.println("   - 适合：CPU密集型任务，需要限制线程数");
        System.out.println("   - 注意：无界队列可能导致OOM");

        System.out.println("\n2. CachedThreadPool:");
        System.out.println("   - 适合：大量短期异步任务");
        System.out.println("   - 注意：可能创建大量线程");

        System.out.println("\n3. SingleThreadExecutor:");
        System.out.println("   - 适合：需要顺序执行的任务");
        System.out.println("   - 注意：无界队列可能导致OOM");

        System.out.println("\n4. ScheduledThreadPool:");
        System.out.println("   - 适合：定时任务、周期性任务");

        System.out.println("\n5. 推荐做法:");
        System.out.println("   - 使用 ThreadPoolExecutor 自定义线程池");
        System.out.println("   - 根据任务类型选择合适的参数");
        System.out.println("   - 避免使用无界队列");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateFixedThreadPool();
        demonstrateCachedThreadPool();
        demonstrateSingleThreadExecutor();
        demonstrateScheduledThreadPool();
        demonstrateFuture();
        demonstrateSelectionGuide();
    }
}

