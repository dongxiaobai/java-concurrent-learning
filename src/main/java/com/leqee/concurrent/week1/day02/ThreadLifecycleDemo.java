package com.leqee.concurrent.week1;

/**
 * Week 1 - Day 2: 线程生命周期演示
 * 
 * 学习目标：
 * 1. 理解线程的 6 种状态
 * 2. 观察线程状态的变化过程
 * 3. 理解状态转换的条件
 * 4. 掌握 getState() 方法的使用
 */
public class ThreadLifecycleDemo {

    /**
     * 演示线程的 6 种状态
     * 
     * Java 线程的 6 种状态：
     * 1. NEW（新建）：线程对象已创建，但尚未调用 start()
     * 2. RUNNABLE（可运行）：调用 start() 后的状态，包括运行中和就绪
     * 3. BLOCKED（阻塞）：等待获取监视器锁
     * 4. WAITING（等待）：无限期等待
     * 5. TIMED_WAITING（超时等待）：带超时的等待
     * 6. TERMINATED（终止）：线程执行完成或异常退出
     */
    public static void demonstrateAllStates() {
        System.out.println("========== 线程的 6 种状态演示 ==========\n");

        // 1. NEW 状态
        Thread thread = new Thread(() -> {
            System.out.println("线程执行中...");
        });
        System.out.println("1. NEW 状态: " + thread.getState());
        System.out.println("   说明：线程对象已创建，但尚未调用 start()\n");

        // 2. RUNNABLE 状态
        thread.start();
        System.out.println("2. RUNNABLE 状态: " + thread.getState());
        System.out.println("   说明：调用 start() 后，线程进入可运行状态\n");

        // 等待线程完成
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3. TERMINATED 状态
        System.out.println("3. TERMINATED 状态: " + thread.getState());
        System.out.println("   说明：线程执行完成后进入终止状态\n");

        System.out.println("========== 演示完成 ==========\n");
    }

    /**
     * 演示 BLOCKED 状态
     * 
     * BLOCKED：等待获取监视器锁（monitor lock）
     * 场景：进入 synchronized 代码块时，锁被其他线程占用
     */
    public static void demonstrateBlockedState() {
        System.out.println("========== BLOCKED 状态演示 ==========\n");

        Object lock = new Object();

        // 线程1：持有锁
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread-1 获取到锁，开始执行");
                try {
                    Thread.sleep(3000); // 持有锁 3 秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread-1 释放锁");
            }
        }, "Thread-1");

        // 线程2：等待获取锁
        Thread t2 = new Thread(() -> {
            System.out.println("Thread-2 尝试获取锁");
            synchronized (lock) {
                System.out.println("Thread-2 获取到锁");
            }
        }, "Thread-2");

        t1.start();
        try {
            Thread.sleep(100); // 确保 t1 先获取锁
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t2.start();
        try {
            Thread.sleep(200); // 等待 t2 进入 BLOCKED 状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread-2 的状态: " + t2.getState()); // BLOCKED
        System.out.println("   说明：Thread-2 等待获取锁，进入 BLOCKED 状态\n");

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("========== 演示完成 ==========\n");
    }

    /**
     * 演示 WAITING 状态
     * 
     * WAITING：无限期等待
     * 触发方法：Object.wait(), Thread.join(), LockSupport.park()
     */
    public static void demonstrateWaitingState() {
        System.out.println("========== WAITING 状态演示 ==========\n");

        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("线程进入 WAITING 状态");
                    lock.wait(); // 无限期等待
                    System.out.println("线程被唤醒");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Waiting-Thread");

        t.start();

        try {
            Thread.sleep(200); // 等待线程进入 WAITING 状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("线程状态: " + t.getState()); // WAITING
        System.out.println("   说明：调用 wait() 后，线程进入 WAITING 状态\n");

        // 唤醒线程
        synchronized (lock) {
            lock.notify();
        }

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("========== 演示完成 ==========\n");
    }

    /**
     * 演示 TIMED_WAITING 状态
     * 
     * TIMED_WAITING：带超时的等待
     * 触发方法：Thread.sleep(), Object.wait(timeout), Thread.join(timeout)
     */
    public static void demonstrateTimedWaitingState() {
        System.out.println("========== TIMED_WAITING 状态演示 ==========\n");

        // 方式1：使用 sleep()
        Thread t1 = new Thread(() -> {
            try {
                System.out.println("Thread-1 准备休眠");
                Thread.sleep(2000); // 休眠 2 秒
                System.out.println("Thread-1 醒来");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread-1");

        t1.start();

        try {
            Thread.sleep(100); // 等待线程进入 TIMED_WAITING 状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread-1 状态（sleep）: " + t1.getState()); // TIMED_WAITING
        System.out.println("   说明：调用 sleep() 后，线程进入 TIMED_WAITING 状态\n");

        // 方式2：使用 wait(timeout)
        Object lock = new Object();
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Thread-2 准备等待（带超时）");
                    lock.wait(2000); // 等待 2 秒
                    System.out.println("Thread-2 等待结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-2");

        t2.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Thread-2 状态（wait(timeout)）: " + t2.getState()); // TIMED_WAITING
        System.out.println("   说明：调用 wait(timeout) 后，线程进入 TIMED_WAITING 状态\n");

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("========== 演示完成 ==========\n");
    }

    /**
     * 演示完整的状态转换过程
     */
    public static void demonstrateStateTransition() {
        System.out.println("========== 线程状态转换完整过程 ==========\n");

        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行");
            try {
                Thread.sleep(1000); // TIMED_WAITING
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程执行完成");
        }, "State-Thread");

        // NEW
        System.out.println("状态1 - NEW: " + thread.getState());
        System.out.println("   转换条件：创建 Thread 对象\n");

        thread.start();

        // RUNNABLE
        System.out.println("状态2 - RUNNABLE: " + thread.getState());
        System.out.println("   转换条件：调用 start()\n");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TIMED_WAITING（如果线程在 sleep）
        System.out.println("状态3 - TIMED_WAITING: " + thread.getState());
        System.out.println("   转换条件：调用 sleep()\n");

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TERMINATED
        System.out.println("状态4 - TERMINATED: " + thread.getState());
        System.out.println("   转换条件：run() 方法执行完成\n");

        System.out.println("========== 演示完成 ==========\n");
    }

    /**
     * 演示 isAlive() 方法
     * 
     * isAlive()：判断线程是否存活
     * 返回 true：线程已启动且尚未终止（NEW 和 TERMINATED 返回 false）
     */
    public static void demonstrateIsAlive() {
        System.out.println("========== isAlive() 方法演示 ==========\n");

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // NEW 状态
        System.out.println("创建后:");
        System.out.println("  getState(): " + thread.getState());
        System.out.println("  isAlive(): " + thread.isAlive() + "\n");

        thread.start();

        // RUNNABLE 状态
        System.out.println("启动后:");
        System.out.println("  getState(): " + thread.getState());
        System.out.println("  isAlive(): " + thread.isAlive() + "\n");

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TERMINATED 状态
        System.out.println("完成后:");
        System.out.println("  getState(): " + thread.getState());
        System.out.println("  isAlive(): " + thread.isAlive() + "\n");

        System.out.println("说明：");
        System.out.println("  - NEW 和 TERMINATED 状态：isAlive() 返回 false");
        System.out.println("  - 其他状态：isAlive() 返回 true\n");

        System.out.println("========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        // 演示所有状态
        demonstrateAllStates();

        // 演示 BLOCKED 状态
        demonstrateBlockedState();

        // 演示 WAITING 状态
        demonstrateWaitingState();

        // 演示 TIMED_WAITING 状态
        demonstrateTimedWaitingState();

        // 演示完整的状态转换
        demonstrateStateTransition();

        // 演示 isAlive() 方法
        demonstrateIsAlive();
    }
}

