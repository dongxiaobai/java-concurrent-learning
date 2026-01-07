package com.concurrent.week2;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

/**
 * Week 2 - Day 1-3: ReentrantLock 深入
 * 
 * 学习目标：
 * 1. ReentrantLock 的基本用法
 * 2. lock() 和 unlock() 的配对使用
 * 3. tryLock() 的使用场景
 * 4. 公平锁 vs 非公平锁
 * 5. 可中断锁
 */
public class ReentrantLockDemo {

    /**
     * 基本用法示例
     */
    static class BasicUsage {
        private int count = 0;
        private final Lock lock = new ReentrantLock();

        public void increment() {
            lock.lock(); // 获取锁
            try {
                count++;
                System.out.println(Thread.currentThread().getName() + " count: " + count);
            } finally {
                lock.unlock(); // 必须在 finally 中释放锁
            }
        }

        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * tryLock() 示例 - 尝试获取锁，不阻塞
     */
    static class TryLockDemo {
        private final Lock lock = new ReentrantLock();

        public void tryLockMethod() {
            if (lock.tryLock()) { // 尝试获取锁，立即返回
                try {
                    System.out.println(Thread.currentThread().getName() + " 获取到锁");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " 未获取到锁，执行其他逻辑");
            }
        }

        // tryLock(timeout) - 带超时的尝试获取锁
        public void tryLockWithTimeout() {
            try {
                if (lock.tryLock(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 在超时时间内获取到锁");
                        Thread.sleep(2000);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " 超时未获取到锁");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 公平锁 vs 非公平锁
     */
    static class FairLockDemo {
        // 非公平锁（默认）
        private final Lock unfairLock = new ReentrantLock(false);
        
        // 公平锁
        private final Lock fairLock = new ReentrantLock(true);

        public void unfairLockMethod() {
            unfairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 使用非公平锁");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                unfairLock.unlock();
            }
        }

        public void fairLockMethod() {
            fairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 使用公平锁");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                fairLock.unlock();
            }
        }
    }

    /**
     * 可中断锁示例
     */
    static class InterruptibleLockDemo {
        private final Lock lock = new ReentrantLock();

        public void interruptibleMethod() {
            try {
                lock.lockInterruptibly(); // 可中断的锁获取
                try {
                    System.out.println(Thread.currentThread().getName() + " 获取到锁，开始执行");
                    Thread.sleep(5000); // 长时间执行
                    System.out.println(Thread.currentThread().getName() + " 执行完成");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 被中断");
            }
        }
    }

    /**
     * 演示基本用法
     */
    public static void demonstrateBasicUsage() {
        System.out.println("========== ReentrantLock 基本用法演示 ==========\n");

        BasicUsage demo = new BasicUsage();
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    demo.increment();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread-" + i);
        }

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

        System.out.println("最终计数: " + demo.getCount());
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示 tryLock()
     */
    public static void demonstrateTryLock() {
        System.out.println("========== tryLock() 演示 ==========\n");

        TryLockDemo demo = new TryLockDemo();

        Thread t1 = new Thread(() -> demo.tryLockMethod(), "Thread-1");
        Thread t2 = new Thread(() -> demo.tryLockMethod(), "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- 带超时的 tryLock() ---\n");

        Thread t3 = new Thread(() -> demo.tryLockWithTimeout(), "Thread-3");
        Thread t4 = new Thread(() -> demo.tryLockWithTimeout(), "Thread-4");

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

    /**
     * 演示公平锁和非公平锁的区别
     */
    public static void demonstrateFairVsUnfair() {
        System.out.println("========== 公平锁 vs 非公平锁演示 ==========\n");

        FairLockDemo demo = new FairLockDemo();

        System.out.println("--- 非公平锁（默认）---");
        Thread[] unfairThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            unfairThreads[i] = new Thread(() -> demo.unfairLockMethod(), "Unfair-" + i);
        }
        for (Thread thread : unfairThreads) {
            thread.start();
        }
        for (Thread thread : unfairThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n--- 公平锁 ---");
        Thread[] fairThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            fairThreads[i] = new Thread(() -> demo.fairLockMethod(), "Fair-" + i);
        }
        for (Thread thread : fairThreads) {
            thread.start();
        }
        for (Thread thread : fairThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示可中断锁
     */
    public static void demonstrateInterruptible() {
        System.out.println("========== 可中断锁演示 ==========\n");

        InterruptibleLockDemo demo = new InterruptibleLockDemo();

        Thread t1 = new Thread(() -> demo.interruptibleMethod(), "Thread-1");
        t1.start();

        try {
            Thread.sleep(1000);
            System.out.println("中断 Thread-1");
            t1.interrupt(); // 中断线程
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicUsage();
        demonstrateTryLock();
        demonstrateFairVsUnfair();
        demonstrateInterruptible();
    }
}
