package com.leqee.concurrent.week4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Week 4 - Day 3-7: CompletableFuture 异步编程
 * 
 * 学习目标：
 * 1. CompletableFuture 的基本用法
 * 2. 异步任务的创建和执行
 * 3. 任务链式调用：thenApply, thenAccept, thenRun
 * 4. 任务组合：thenCombine, allOf, anyOf
 * 5. 异常处理：exceptionally, handle
 */
public class CompletableFutureDemo {

    /**
     * 场景1：创建异步任务
     */
    public static void demonstrateBasicAsync() {
        System.out.println("========== CompletableFuture - 基本异步任务 ==========\n");

        // 方式1：使用 runAsync（无返回值）
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println("异步任务1执行中...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("异步任务1完成");
        });

        // 方式2：使用 supplyAsync（有返回值）
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务2执行中...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务2的结果";
        });

        try {
            future1.get(); // 等待任务1完成
            String result = future2.get(); // 获取任务2的结果
            System.out.println("任务2返回: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：任务链式调用
     */
    public static void demonstrateChaining() {
        System.out.println("========== CompletableFuture - 任务链式调用 ==========\n");

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("步骤1: 获取数据");
                return "原始数据";
            })
            .thenApply(data -> {
                System.out.println("步骤2: 处理数据 - " + data);
                return data + " -> 处理后";
            })
            .thenApply(data -> {
                System.out.println("步骤3: 格式化数据 - " + data);
                return data + " -> 格式化后";
            })
            .thenApply(data -> {
                System.out.println("步骤4: 最终处理 - " + data);
                return data + " -> 最终结果";
            });

        try {
            String result = future.get();
            System.out.println("最终结果: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：thenAccept 和 thenRun
     */
    public static void demonstrateConsumeAndRun() {
        System.out.println("========== CompletableFuture - thenAccept 和 thenRun ==========\n");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("生产数据");
            return "数据";
        });

        // thenAccept: 消费结果，无返回值
        CompletableFuture<Void> acceptFuture = future.thenAccept(data -> {
            System.out.println("消费数据: " + data);
        });

        // thenRun: 不依赖结果，执行后续操作
        CompletableFuture<Void> runFuture = acceptFuture.thenRun(() -> {
            System.out.println("执行后续操作");
        });

        try {
            runFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：任务组合 - thenCombine
     */
    public static void demonstrateCombine() {
        System.out.println("========== CompletableFuture - 任务组合 ==========\n");

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1: 计算 10 + 20");
            return 10 + 20;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2: 计算 30 + 40");
            return 30 + 40;
        });

        // 组合两个任务的结果
        CompletableFuture<Integer> combined = future1.thenCombine(future2, (result1, result2) -> {
            System.out.println("组合结果: " + result1 + " + " + result2);
            return result1 + result2;
        });

        try {
            Integer result = combined.get();
            System.out.println("最终结果: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景5：等待所有任务完成 - allOf
     */
    public static void demonstrateAllOf() {
        System.out.println("========== CompletableFuture - allOf 演示 ==========\n");

        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务1完成";
        });

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务2完成";
        });

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3完成";
        });

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);

        try {
            allTasks.get(); // 等待所有任务完成
            System.out.println("所有任务完成:");
            System.out.println("  " + task1.get());
            System.out.println("  " + task2.get());
            System.out.println("  " + task3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景6：任意一个任务完成 - anyOf
     */
    public static void demonstrateAnyOf() {
        System.out.println("========== CompletableFuture - anyOf 演示 ==========\n");

        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务1完成";
        });

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务2完成";
        });

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3完成";
        });

        CompletableFuture<Object> anyTask = CompletableFuture.anyOf(task1, task2, task3);

        try {
            Object result = anyTask.get();
            System.out.println("最快完成的任务: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景7：异常处理
     */
    public static void demonstrateExceptionHandling() {
        System.out.println("========== CompletableFuture - 异常处理 ==========\n");

        // exceptionally: 捕获异常并返回默认值
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("模拟异常");
            }
            return "成功";
        }).exceptionally(ex -> {
            System.out.println("捕获异常: " + ex.getMessage());
            return "默认值";
        });

        try {
            System.out.println("结果1: " + future1.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // handle: 无论成功还是失败都会执行
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("模拟异常");
            }
            return "成功";
        }).handle((result, ex) -> {
            if (ex != null) {
                System.out.println("处理异常: " + ex.getMessage());
                return "异常处理后的默认值";
            }
            return result;
        });

        try {
            System.out.println("结果2: " + future2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicAsync();
        demonstrateChaining();
        demonstrateConsumeAndRun();
        demonstrateCombine();
        demonstrateAllOf();
        demonstrateAnyOf();
        demonstrateExceptionHandling();
    }
}

