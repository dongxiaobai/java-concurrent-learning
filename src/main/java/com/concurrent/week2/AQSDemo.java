package com.concurrent.week2;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Week 2 - Day 6-7: AQS (AbstractQueuedSynchronizer) 原理
 * 
 * 学习目标：
 * 1. 理解 AQS 的核心思想
 * 2. 了解 AQS 的同步队列（CLH队列）
 * 3. 理解 state 状态变量的作用
 * 4. 学习如何基于 AQS 实现自定义锁
 */
public class AQSDemo {

    /**
     * 基于 AQS 实现的简单互斥锁
     * 
     * 这是一个简化版的 Mutex，演示 AQS 的基本用法
     */
    static class SimpleMutex {
        private final Sync sync = new Sync();

        /**
         * 内部同步器，继承 AQS
         */
        static class Sync extends AbstractQueuedSynchronizer {
            /**
             * 尝试获取锁
             * 如果 state 为 0，说明锁未被占用，可以获取
             */
            @Override
            protected boolean tryAcquire(int arg) {
                if (compareAndSetState(0, 1)) { // CAS 操作，将 state 从 0 改为 1
                    setExclusiveOwnerThread(Thread.currentThread()); // 设置独占线程
                    return true;
                }
                return false;
            }

            /**
             * 尝试释放锁
             */
            @Override
            protected boolean tryRelease(int arg) {
                if (getState() == 0) {
                    throw new IllegalMonitorStateException();
                }
                setExclusiveOwnerThread(null); // 清除独占线程
                setState(0); // 将 state 设置为 0
                return true;
            }

            /**
             * 判断是否被当前线程独占
             */
            @Override
            protected boolean isHeldExclusively() {
                return getExclusiveOwnerThread() == Thread.currentThread();
            }
        }

        public void lock() {
            sync.acquire(1); // 调用 AQS 的 acquire 方法
        }

        public void unlock() {
            sync.release(1); // 调用 AQS 的 release 方法
        }

        public boolean tryLock() {
            return sync.tryAcquire(1);
        }
    }

    /**
     * 基于 AQS 实现的共享锁（信号量）
     * 
     * 允许多个线程同时获取锁，但有限制数量
     */
    static class SimpleSemaphore {
        private final Sync sync;

        public SimpleSemaphore(int permits) {
            sync = new Sync(permits);
        }

        static class Sync extends AbstractQueuedSynchronizer {
            Sync(int permits) {
                setState(permits); // 初始化 state 为许可数量
            }

            /**
             * 尝试获取共享锁
             */
            @Override
            protected int tryAcquireShared(int acquires) {
                for (;;) {
                    int current = getState();
                    int remaining = current - acquires;
                    if (remaining < 0 || compareAndSetState(current, remaining)) {
                        return remaining; // 返回剩余许可数
                    }
                }
            }

            /**
             * 尝试释放共享锁
             */
            @Override
            protected boolean tryReleaseShared(int releases) {
                for (;;) {
                    int current = getState();
                    int next = current + releases;
                    if (compareAndSetState(current, next)) {
                        return true;
                    }
                }
            }
        }

        public void acquire() {
            sync.acquireShared(1);
        }

        public void release() {
            sync.releaseShared(1);
        }
    }

    /**
     * 演示自定义互斥锁
     */
    public static void demonstrateMutex() {
        System.out.println("========== 基于 AQS 的自定义互斥锁演示 ==========\n");

        SimpleMutex mutex = new SimpleMutex();
        int[] count = {0};

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                mutex.lock(); // 获取锁
                try {
                    System.out.println(Thread.currentThread().getName() + " 获取到锁");
                    count[0]++;
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getName() + " count: " + count[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.unlock(); // 释放锁
                    System.out.println(Thread.currentThread().getName() + " 释放锁");
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

        System.out.println("最终 count: " + count[0]);
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示自定义共享锁（信号量）
     */
    public static void demonstrateSemaphore() {
        System.out.println("========== 基于 AQS 的自定义信号量演示 ==========\n");

        SimpleSemaphore semaphore = new SimpleSemaphore(3); // 允许3个线程同时获取

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                semaphore.acquire(); // 获取许可
                try {
                    System.out.println(Thread.currentThread().getName() + " 获取到许可，开始执行");
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 执行完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); // 释放许可
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

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示 AQS 的核心概念
     */
    public static void demonstrateAQSConcepts() {
        System.out.println("========== AQS 核心概念说明 ==========\n");

        System.out.println("AQS (AbstractQueuedSynchronizer) 的核心组件：");
        System.out.println("1. state: 同步状态，通过 CAS 操作修改");
        System.out.println("2. CLH队列: 双向链表，存储等待获取锁的线程");
        System.out.println("3. 独占模式: 同一时刻只有一个线程能获取锁（如 ReentrantLock）");
        System.out.println("4. 共享模式: 同一时刻可以有多个线程获取锁（如 Semaphore、CountDownLatch）");
        System.out.println("\nAQS 提供了模板方法：");
        System.out.println("- acquire/release: 独占模式获取/释放锁");
        System.out.println("- acquireShared/releaseShared: 共享模式获取/释放锁");
        System.out.println("- tryAcquire/tryRelease: 子类需要实现的独占模式方法");
        System.out.println("- tryAcquireShared/tryReleaseShared: 子类需要实现的共享模式方法");
        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateAQSConcepts();
        demonstrateMutex();
        demonstrateSemaphore();
    }
}

