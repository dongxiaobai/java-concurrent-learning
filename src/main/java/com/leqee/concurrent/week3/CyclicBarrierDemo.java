package com.leqee.concurrent.week3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Week 3 - Day 3: CyclicBarrier 循环屏障
 * 
 * 学习目标：
 * 1. CyclicBarrier 的用途：多个线程互相等待，到达屏障点后一起继续
 * 2. await() 的使用
 * 3. 可重复使用的特性（与 CountDownLatch 的区别）
 * 4. 屏障动作（barrierAction）的使用
 */
public class CyclicBarrierDemo {

    /**
     * 场景1：多个线程到达屏障点后一起继续
     */
    public static void demonstrateBasicUsage() {
        System.out.println("========== CyclicBarrier - 基本用法演示 ==========\n");

        int threadCount = 5;
        CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
            System.out.println("\n所有线程到达屏障点，一起继续执行\n");
        });

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread-" + threadId + " 执行第一阶段任务");
                    Thread.sleep((long) (Math.random() * 2000));
                    System.out.println("Thread-" + threadId + " 到达屏障点，等待其他线程");
                    barrier.await(); // 等待所有线程到达屏障点

                    System.out.println("Thread-" + threadId + " 执行第二阶段任务");
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println("Thread-" + threadId + " 完成");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, "Thread-" + i).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：分阶段任务，每阶段都需要等待所有线程完成
     */
    public static void demonstrateMultiStage() {
        System.out.println("========== CyclicBarrier - 多阶段任务演示 ==========\n");

        int threadCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // 阶段1
                    System.out.println("Thread-" + threadId + " 完成阶段1");
                    barrier.await();

                    // 阶段2
                    Thread.sleep(500);
                    System.out.println("Thread-" + threadId + " 完成阶段2");
                    barrier.await();

                    // 阶段3
                    Thread.sleep(500);
                    System.out.println("Thread-" + threadId + " 完成阶段3");
                    barrier.await();

                    System.out.println("Thread-" + threadId + " 所有阶段完成");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, "Thread-" + i).start();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：并行计算，多轮迭代
     */
    public static void demonstrateParallelIteration() {
        System.out.println("========== CyclicBarrier - 并行迭代计算演示 ==========\n");

        int workerCount = 4;
        int iterations = 3;
        CyclicBarrier barrier = new CyclicBarrier(workerCount, () -> {
            System.out.println("--- 一轮迭代完成，开始下一轮 ---\n");
        });

        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                for (int iter = 0; iter < iterations; iter++) {
                    try {
                        System.out.println("Worker-" + workerId + " 执行第 " + (iter + 1) + " 轮迭代");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("Worker-" + workerId + " 完成第 " + (iter + 1) + " 轮迭代");
                        barrier.await(); // 等待所有 Worker 完成当前轮次
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Worker-" + workerId + " 所有迭代完成");
            }, "Worker-" + i).start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 对比 CountDownLatch 和 CyclicBarrier
     */
    public static void demonstrateComparison() {
        System.out.println("========== CountDownLatch vs CyclicBarrier 对比 ==========\n");

        System.out.println("CountDownLatch 特点：");
        System.out.println("- 一次性使用，计数到0后不能重置");
        System.out.println("- 一个或多个线程等待其他线程完成");
        System.out.println("- countDown() 可以在不同线程调用");
        System.out.println("\nCyclicBarrier 特点：");
        System.out.println("- 可重复使用，可以重置");
        System.out.println("- 多个线程互相等待，到达屏障点后一起继续");
        System.out.println("- await() 必须在参与屏障的线程中调用");
        System.out.println("- 可以设置屏障动作（barrierAction）");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicUsage();
        demonstrateMultiStage();
        demonstrateParallelIteration();
        demonstrateComparison();
    }
}

