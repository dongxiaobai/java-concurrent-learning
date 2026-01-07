package com.leqee.concurrent.week1;

/**
 * Week 1 - Day 5-7: synchronized 关键字深入
 * 
 * 学习目标：
 * 1. synchronized 的用法：同步方法、同步代码块
 * 2. 对象锁和类锁
 * 3. 可重入性
 * 4. 锁的粒度
 */
public class Day05_06_07_SynchronizedDemo {

    /**
     * 同步方法示例
     */
    static class SynchronizedMethod {
        private int count = 0;

        // 同步方法：锁的是当前对象（this）
        public synchronized void increment() {
            count++;
            System.out.println(Thread.currentThread().getName() + " increment: " + count);
        }

        public synchronized int getCount() {
            return count;
        }
    }

    /**
     * 同步代码块示例
     */
    static class SynchronizedBlock {
        private int count = 0;
        private final Object lock = new Object();

        // 同步代码块：锁的是指定的对象
        public void increment() {
            synchronized (lock) {
                count++;
                System.out.println(Thread.currentThread().getName() + " increment: " + count);
            }
        }

        // 也可以锁 this
        public void increment2() {
            synchronized (this) {
                count++;
            }
        }

        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
    }

    /**
     * 对象锁示例
     * 
     * 同一个对象的同步方法，同一时间只能有一个线程执行
     */
    static class ObjectLock {
        public synchronized void method1() {
            System.out.println(Thread.currentThread().getName() + " 进入 method1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 离开 method1");
        }

        public synchronized void method2() {
            System.out.println(Thread.currentThread().getName() + " 进入 method2");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 离开 method2");
        }

        // 非同步方法，不受锁影响
        public void method3() {
            System.out.println(Thread.currentThread().getName() + " 进入 method3（非同步）");
        }
    }

    /**
     * 类锁示例
     * 
     * 静态同步方法，锁的是类对象（Class对象）
     */
    static class ClassLock {
        public static synchronized void staticMethod1() {
            System.out.println(Thread.currentThread().getName() + " 进入 staticMethod1");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 离开 staticMethod1");
        }

        public static synchronized void staticMethod2() {
            System.out.println(Thread.currentThread().getName() + " 进入 staticMethod2");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 离开 staticMethod2");
        }

        // 同步代码块锁类对象
        public static void staticMethod3() {
            synchronized (ClassLock.class) {
                System.out.println(Thread.currentThread().getName() + " 进入 staticMethod3");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 离开 staticMethod3");
            }
        }
    }

    /**
     * 可重入性示例
     * 
     * synchronized 是可重入的：同一个线程可以多次获取同一个锁
     */
    static class ReentrantDemo {
        public synchronized void method1() {
            System.out.println(Thread.currentThread().getName() + " 进入 method1");
            method2(); // 调用另一个同步方法
            System.out.println(Thread.currentThread().getName() + " 离开 method1");
        }

        public synchronized void method2() {
            System.out.println(Thread.currentThread().getName() + " 进入 method2");
            System.out.println(Thread.currentThread().getName() + " 离开 method2");
        }
    }

    /**
     * 演示同步方法
     */
    public static void demonstrateSynchronizedMethod() {
        System.out.println("========== 同步方法演示 ==========\n");

        SynchronizedMethod counter = new SynchronizedMethod();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                counter.increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                counter.increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("最终计数: " + counter.getCount());
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示对象锁
     */
    public static void demonstrateObjectLock() {
        System.out.println("========== 对象锁演示 ==========\n");

        ObjectLock lock = new ObjectLock();

        // 线程1：调用 method1
        Thread t1 = new Thread(() -> lock.method1(), "Thread-1");

        // 线程2：调用 method2（会被阻塞，因为锁的是同一个对象）
        Thread t2 = new Thread(() -> lock.method2(), "Thread-2");

        // 线程3：调用 method3（不会被阻塞，因为不是同步方法）
        Thread t3 = new Thread(() -> lock.method3(), "Thread-3");

        t1.start();
        try {
            Thread.sleep(100); // 确保 t1 先获取锁
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示类锁
     */
    public static void demonstrateClassLock() {
        System.out.println("========== 类锁演示 ==========\n");

        // 不同实例，但静态方法共享同一个锁（类锁）
        // 注意：即使创建不同实例，静态方法仍然共享同一个锁
        Thread t1 = new Thread(() -> ClassLock.staticMethod1(), "Thread-1");
        Thread t2 = new Thread(() -> ClassLock.staticMethod2(), "Thread-2");
        Thread t3 = new Thread(() -> ClassLock.staticMethod3(), "Thread-3");

        t1.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示可重入性
     */
    public static void demonstrateReentrant() {
        System.out.println("========== 可重入性演示 ==========\n");

        ReentrantDemo demo = new ReentrantDemo();

        Thread t1 = new Thread(() -> demo.method1(), "Thread-1");
        Thread t2 = new Thread(() -> demo.method1(), "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n说明：同一个线程可以多次获取同一个锁（可重入）");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 演示锁的粒度
     * 
     * 粗粒度锁：锁的范围大，性能差但简单
     * 细粒度锁：锁的范围小，性能好但复杂
     */
    static class LockGranularity {
        private int count1 = 0;
        private int count2 = 0;
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();

        // 粗粒度：两个计数器共用一把锁
        public void incrementBothCoarse() {
            synchronized (this) {
                count1++;
                count2++;
            }
        }

        // 细粒度：两个计数器各用一把锁
        public void incrementBothFine() {
            synchronized (lock1) {
                count1++;
            }
            synchronized (lock2) {
                count2++;
            }
        }

        public int getCount1() {
            return count1;
        }

        public int getCount2() {
            return count2;
        }
    }

    public static void main(String[] args) {
        // 演示同步方法
        demonstrateSynchronizedMethod();

        // 演示对象锁
        demonstrateObjectLock();

        // 演示类锁
        demonstrateClassLock();

        // 演示可重入性
        demonstrateReentrant();
    }
}


