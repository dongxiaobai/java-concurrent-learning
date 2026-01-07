package com.leqee.concurrent.week2;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Week 2 - Day 4-5: ReadWriteLock 读写锁
 * 
 * 学习目标：
 * 1. ReadWriteLock 的读写分离特性
 * 2. 读锁（共享锁）和写锁（排他锁）的区别
 * 3. 读写锁的使用场景
 * 4. 读写锁的性能优势
 */
public class ReadWriteLockDemo {

    /**
     * 使用 ReadWriteLock 实现的线程安全缓存
     */
    static class Cache {
        private String data = "初始数据";
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * 读操作：多个线程可以同时读
         */
        public String read() {
            lock.readLock().lock(); // 获取读锁
            try {
                System.out.println(Thread.currentThread().getName() + " 读取数据: " + data);
                Thread.sleep(100); // 模拟读取耗时
                return data;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } finally {
                lock.readLock().unlock(); // 释放读锁
            }
        }

        /**
         * 写操作：只能有一个线程写，且写时不能读
         */
        public void write(String newData) {
            lock.writeLock().lock(); // 获取写锁
            try {
                System.out.println(Thread.currentThread().getName() + " 写入数据: " + newData);
                Thread.sleep(500); // 模拟写入耗时
                this.data = newData;
                System.out.println(Thread.currentThread().getName() + " 写入完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.writeLock().unlock(); // 释放写锁
            }
        }
    }

    /**
     * 对比：使用普通锁 vs 读写锁的性能差异
     */
    static class PerformanceComparison {
        private String data = "数据";
        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final Object normalLock = new Object();

        // 使用读写锁
        public String readWithReadWriteLock() {
            readWriteLock.readLock().lock();
            try {
                return data;
            } finally {
                readWriteLock.readLock().unlock();
            }
        }

        public void writeWithReadWriteLock(String newData) {
            readWriteLock.writeLock().lock();
            try {
                this.data = newData;
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        // 使用普通锁
        public synchronized String readWithNormalLock() {
            return data;
        }

        public synchronized void writeWithNormalLock(String newData) {
            this.data = newData;
        }
    }

    /**
     * 演示读写锁的基本用法
     */
    public static void demonstrateBasicUsage() {
        System.out.println("========== ReadWriteLock 基本用法演示 ==========\n");

        Cache cache = new Cache();

        // 创建多个读线程
        Thread[] readers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    cache.read();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Reader-" + i);
        }

        // 创建写线程
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                cache.write("数据-" + i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Writer");

        // 启动所有线程
        writer.start();
        for (Thread reader : readers) {
            reader.start();
        }

        // 等待所有线程完成
        try {
            writer.join();
            for (Thread reader : readers) {
                reader.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示读写锁的性能优势
     */
    public static void demonstratePerformance() {
        System.out.println("========== 读写锁性能对比演示 ==========\n");

        PerformanceComparison comparison = new PerformanceComparison();

        // 测试读写锁性能
        System.out.println("--- 使用 ReadWriteLock ---");
        long start1 = System.currentTimeMillis();
        Thread[] threads1 = new Thread[10];
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                // 写线程
                threads1[i] = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        comparison.writeWithReadWriteLock("数据-" + j);
                    }
                }, "Writer-" + i);
            } else {
                // 读线程
                threads1[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        comparison.readWithReadWriteLock();
                    }
                }, "Reader-" + i);
            }
        }

        for (Thread thread : threads1) {
            thread.start();
        }

        for (Thread thread : threads1) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end1 = System.currentTimeMillis();
        System.out.println("ReadWriteLock 耗时: " + (end1 - start1) + "ms");

        // 测试普通锁性能
        System.out.println("\n--- 使用普通 synchronized 锁 ---");
        long start2 = System.currentTimeMillis();
        Thread[] threads2 = new Thread[10];
        for (int i = 0; i < 10; i++) {
            if (i % 3 == 0) {
                threads2[i] = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        comparison.writeWithNormalLock("数据-" + j);
                    }
                }, "Writer-" + i);
            } else {
                threads2[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        comparison.readWithNormalLock();
                    }
                }, "Reader-" + i);
            }
        }

        for (Thread thread : threads2) {
            thread.start();
        }

        for (Thread thread : threads2) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println("synchronized 锁耗时: " + (end2 - start2) + "ms");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicUsage();
        demonstratePerformance();
    }
}
