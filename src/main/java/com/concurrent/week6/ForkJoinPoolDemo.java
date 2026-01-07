package com.concurrent.week6;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Week 6 - Day 6-7: ForkJoinPool 分叉合并线程池
 * 
 * 学习目标：
 * 1. ForkJoinPool 的工作原理
 * 2. RecursiveTask 和 RecursiveAction
 * 3. 分治算法的并行实现
 * 4. 工作窃取（Work-Stealing）算法
 */
public class ForkJoinPoolDemo {

    /**
     * 场景1: RecursiveTask - 有返回值的分治任务
     * 计算数组元素之和
     */
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 1000; // 阈值
        private final int[] array;
        private final int start;
        private final int end;

        public SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            // 如果任务足够小，直接计算
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // 否则，分割任务
            int mid = (start + end) / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);

            // 分叉执行
            leftTask.fork();
            Long rightResult = rightTask.compute();
            Long leftResult = leftTask.join();

            // 合并结果
            return leftResult + rightResult;
        }
    }

    /**
     * 场景2: RecursiveAction - 无返回值的分治任务
     * 并行打印数组元素
     */
    static class PrintTask extends RecursiveAction {
        private static final int THRESHOLD = 10;
        private final int[] array;
        private final int start;
        private final int end;

        public PrintTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                // 直接执行
                for (int i = start; i < end; i++) {
                    System.out.print(array[i] + " ");
                }
            } else {
                // 分割任务
                int mid = (start + end) / 2;
                PrintTask leftTask = new PrintTask(array, start, mid);
                PrintTask rightTask = new PrintTask(array, mid, end);

                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }
    }

    /**
     * 演示 RecursiveTask
     */
    public static void demonstrateRecursiveTask() {
        System.out.println("========== ForkJoinPool - RecursiveTask 演示 ==========\n");

        // 创建大数组
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        // 创建 ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();

        // 提交任务
        SumTask task = new SumTask(array, 0, array.length);
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(task);
        long endTime = System.currentTimeMillis();

        System.out.println("数组大小: " + array.length);
        System.out.println("计算结果: " + result);
        System.out.println("耗时: " + (endTime - startTime) + "ms");

        // 对比单线程计算
        startTime = System.currentTimeMillis();
        long sum = 0;
        for (int value : array) {
            sum += value;
        }
        endTime = System.currentTimeMillis();
        System.out.println("单线程计算结果: " + sum);
        System.out.println("单线程耗时: " + (endTime - startTime) + "ms");

        pool.shutdown();
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示 RecursiveAction
     */
    public static void demonstrateRecursiveAction() {
        System.out.println("========== ForkJoinPool - RecursiveAction 演示 ==========\n");

        int[] array = new int[50];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        ForkJoinPool pool = new ForkJoinPool();
        PrintTask task = new PrintTask(array, 0, array.length);

        System.out.println("并行打印数组:");
        pool.invoke(task);
        System.out.println("\n");

        pool.shutdown();
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示工作窃取算法
     */
    public static void demonstrateWorkStealing() {
        System.out.println("========== ForkJoinPool - 工作窃取算法说明 ==========\n");

        System.out.println("工作窃取（Work-Stealing）算法:");
        System.out.println("1. 每个工作线程都有自己的双端队列（deque）");
        System.out.println("2. 线程从队列头部获取任务执行");
        System.out.println("3. 当某个线程的队列为空时，可以从其他线程的队列尾部窃取任务");
        System.out.println("4. 这样可以充分利用CPU资源，减少线程等待时间");
        System.out.println("\n优势:");
        System.out.println("- 减少线程竞争");
        System.out.println("- 提高CPU利用率");
        System.out.println("- 适合分治算法");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    /**
     * 演示 ForkJoinPool 的常用方法
     */
    public static void demonstrateForkJoinPoolMethods() {
        System.out.println("========== ForkJoinPool - 常用方法演示 ==========\n");

        ForkJoinPool pool = new ForkJoinPool();

        // invoke: 同步执行并等待结果
        System.out.println("--- invoke 方法 ---");
        RecursiveTask<Integer> task1 = new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                return 100;
            }
        };
        Integer result1 = pool.invoke(task1);
        System.out.println("invoke 结果: " + result1);

        // submit: 异步提交任务
        System.out.println("\n--- submit 方法 ---");
        RecursiveTask<Integer> task2 = new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                return 200;
            }
        };
        java.util.concurrent.Future<Integer> future = pool.submit(task2);
        try {
            System.out.println("submit 结果: " + future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // execute: 异步执行（无返回值）
        System.out.println("\n--- execute 方法 ---");
        RecursiveAction action = new RecursiveAction() {
            @Override
            protected void compute() {
                System.out.println("执行 RecursiveAction");
            }
        };
        pool.execute(action);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdown();
        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateRecursiveTask();
        demonstrateRecursiveAction();
        demonstrateWorkStealing();
        demonstrateForkJoinPoolMethods();
    }
}

