package com.leqee.concurrent.week3;

import java.util.concurrent.CountDownLatch;

/**
 * Week 3 - Day 1-2: CountDownLatch 倒计时门闩
 * 
 * 学习目标：
 * 1. CountDownLatch 的用途：等待多个线程完成
 * 2. countDown() 和 await() 的使用
 * 3. 一次性使用的特性
 * 4. 实际应用场景：启动多个服务后统一开始
 */
public class CountDownLatchDemo {

    /**
     * 场景1：等待多个线程完成后再执行主任务
     */
    public static void demonstrateWaitForCompletion() {
        System.out.println("========== CountDownLatch - 等待多个线程完成 ==========\n");

        int workerCount = 5;
        CountDownLatch latch = new CountDownLatch(workerCount);

        // 创建工作线程
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker-" + workerId + " 开始工作");
                    Thread.sleep((long) (Math.random() * 2000)); // 模拟工作时间
                    System.out.println("Worker-" + workerId + " 完成工作");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown(); // 完成工作，计数减1
                }
            }).start();
        }

        try {
            System.out.println("主线程等待所有 Worker 完成...");
            latch.await(); // 等待所有 Worker 完成
            System.out.println("所有 Worker 已完成，主线程继续执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：多个服务启动后，统一开始处理请求
     */
    public static void demonstrateServiceStartup() {
        System.out.println("========== CountDownLatch - 服务启动场景 ==========\n");

        CountDownLatch startupLatch = new CountDownLatch(3); // 3个服务
        CountDownLatch readyLatch = new CountDownLatch(1); // 统一开始信号

        // 服务1：数据库服务
        new Thread(() -> {
            try {
                System.out.println("数据库服务启动中...");
                Thread.sleep(1000);
                System.out.println("数据库服务启动完成");
                startupLatch.countDown();
                readyLatch.await(); // 等待统一开始信号
                System.out.println("数据库服务开始处理请求");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Database-Service").start();

        // 服务2：缓存服务
        new Thread(() -> {
            try {
                System.out.println("缓存服务启动中...");
                Thread.sleep(1500);
                System.out.println("缓存服务启动完成");
                startupLatch.countDown();
                readyLatch.await();
                System.out.println("缓存服务开始处理请求");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Cache-Service").start();

        // 服务3：消息队列服务
        new Thread(() -> {
            try {
                System.out.println("消息队列服务启动中...");
                Thread.sleep(800);
                System.out.println("消息队列服务启动完成");
                startupLatch.countDown();
                readyLatch.await();
                System.out.println("消息队列服务开始处理请求");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "MQ-Service").start();

        try {
            // 等待所有服务启动完成
            startupLatch.await();
            System.out.println("\n所有服务启动完成，统一开始处理请求\n");
            Thread.sleep(500);
            readyLatch.countDown(); // 发送统一开始信号
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：并行计算，等待所有子任务完成
     */
    public static void demonstrateParallelComputation() {
        System.out.println("========== CountDownLatch - 并行计算场景 ==========\n");

        int taskCount = 4;
        CountDownLatch latch = new CountDownLatch(taskCount);
        int[] results = new int[taskCount];

        // 创建多个计算任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    // 模拟计算任务
                    int sum = 0;
                    for (int j = 0; j < 1000000; j++) {
                        sum += j;
                    }
                    results[taskId] = sum;
                    System.out.println("Task-" + taskId + " 计算完成");
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Task-" + i).start();
        }

        try {
            latch.await(); // 等待所有任务完成
            int totalSum = 0;
            for (int result : results) {
                totalSum += result;
            }
            System.out.println("所有任务完成，总结果: " + totalSum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateWaitForCompletion();
        demonstrateServiceStartup();
        demonstrateParallelComputation();
    }
}

