package com.leqee.concurrent.week3;

import java.util.concurrent.Semaphore;

/**
 * Week 3 - Day 4-5: Semaphore 信号量
 * 
 * 学习目标：
 * 1. Semaphore 的用途：控制同时访问资源的线程数量
 * 2. acquire() 和 release() 的使用
 * 3. 公平模式和非公平模式
 * 4. 实际应用场景：限流、连接池等
 */
public class SemaphoreDemo {

    /**
     * 场景1：限制同时访问资源的线程数量
     */
    public static void demonstrateResourceLimiting() {
        System.out.println("========== Semaphore - 资源限流演示 ==========\n");

        int maxConcurrent = 3; // 最多允许3个线程同时访问
        Semaphore semaphore = new Semaphore(maxConcurrent);

        // 创建10个线程尝试访问资源
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread-" + threadId + " 尝试获取资源");
                    semaphore.acquire(); // 获取许可
                    System.out.println("Thread-" + threadId + " 获取到资源，开始使用");
                    Thread.sleep(2000); // 模拟使用资源
                    System.out.println("Thread-" + threadId + " 释放资源");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); // 释放许可
                }
            }, "Thread-" + i).start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：数据库连接池模拟
     */
    public static void demonstrateConnectionPool() {
        System.out.println("========== Semaphore - 连接池模拟演示 ==========\n");

        int poolSize = 5; // 连接池大小
        Semaphore connectionSemaphore = new Semaphore(poolSize);

        // 模拟多个线程请求数据库连接
        for (int i = 0; i < 10; i++) {
            final int requestId = i;
            new Thread(() -> {
                try {
                    System.out.println("Request-" + requestId + " 请求数据库连接");
                    connectionSemaphore.acquire();
                    System.out.println("Request-" + requestId + " 获取到连接，执行查询");
                    Thread.sleep((long) (Math.random() * 2000));
                    System.out.println("Request-" + requestId + " 查询完成，释放连接");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    connectionSemaphore.release();
                }
            }, "Request-" + i).start();
        }

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：tryAcquire() - 非阻塞获取许可
     */
    public static void demonstrateTryAcquire() {
        System.out.println("========== Semaphore - tryAcquire() 演示 ==========\n");

        Semaphore semaphore = new Semaphore(2); // 只有2个许可

        // 线程1和2先获取许可
        Thread t1 = new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("Thread-1 获取到许可");
                Thread.sleep(3000);
                semaphore.release();
                System.out.println("Thread-1 释放许可");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println("Thread-2 获取到许可");
                Thread.sleep(3000);
                semaphore.release();
                System.out.println("Thread-2 释放许可");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        // Thread-3 尝试非阻塞获取
        Thread t3 = new Thread(() -> {
            if (semaphore.tryAcquire()) {
                try {
                    System.out.println("Thread-3 获取到许可");
                    Thread.sleep(1000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Thread-3 未获取到许可，执行其他逻辑");
            }
        });

        try {
            Thread.sleep(100);
            t3.start();
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：公平模式 vs 非公平模式
     */
    public static void demonstrateFairVsUnfair() {
        System.out.println("========== Semaphore - 公平模式 vs 非公平模式 ==========\n");

        // 非公平模式（默认）
        Semaphore unfairSemaphore = new Semaphore(2, false);
        System.out.println("--- 非公平模式 ---");
        for (int i = 0; i < 5; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    unfairSemaphore.acquire();
                    System.out.println("Thread-" + id + " 获取到许可（非公平）");
                    Thread.sleep(500);
                    unfairSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 公平模式
        Semaphore fairSemaphore = new Semaphore(2, true);
        System.out.println("\n--- 公平模式 ---");
        for (int i = 0; i < 5; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    fairSemaphore.acquire();
                    System.out.println("Thread-" + id + " 获取到许可（公平）");
                    Thread.sleep(500);
                    fairSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateResourceLimiting();
        demonstrateConnectionPool();
        demonstrateTryAcquire();
        demonstrateFairVsUnfair();
    }
}

