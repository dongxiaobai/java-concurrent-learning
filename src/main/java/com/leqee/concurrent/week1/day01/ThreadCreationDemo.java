package com.leqee.concurrent.week1;

/**
 * Week 1 - Day 1: 线程创建方式演示
 * 
 * 学习目标：
 * 1. 理解三种创建线程的方式
 * 2. 观察线程的执行过程
 * 3. 理解线程的基本操作（sleep、interrupt、join）
 * 
 * 注意：线程生命周期相关内容请参考 ThreadLifecycleDemo.java（Day 2）
 */
public class ThreadCreationDemo {

    /**
     * 方式1：继承 Thread 类
     */
    static class MyThread extends Thread {
        private final String threadName;

        public MyThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            System.out.println("方式1 - 继承Thread: " + threadName + 
                             " | 线程名: " + Thread.currentThread().getName() +
                             " | 线程ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(1000); // 模拟任务执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(threadName + " 执行完成");
        }
    }

    /**
     * 方式2：实现 Runnable 接口
     */
    static class MyRunnable implements Runnable {
        private final String taskName;

        public MyRunnable(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public void run() {
            System.out.println("方式2 - 实现Runnable: " + taskName + 
                             " | 线程名: " + Thread.currentThread().getName() +
                             " | 线程ID: " + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(taskName + " 执行完成");
        }
    }

    /**
     * 方式3：实现 Callable 接口（有返回值）
     */
    static class MyCallable implements java.util.concurrent.Callable<String> {
        private final String taskName;

        public MyCallable(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public String call() throws Exception {
            System.out.println("方式3 - 实现Callable: " + taskName + 
                             " | 线程名: " + Thread.currentThread().getName() +
                             " | 线程ID: " + Thread.currentThread().getId());
            Thread.sleep(1000);
            System.out.println(taskName + " 执行完成");
            return "Callable任务[" + taskName + "]的返回值";
        }
    }

    /**
     * 演示三种创建线程的方式
     */
    public static void demonstrateThreadCreation() {
        System.out.println("========== 线程创建方式演示 ==========\n");

        // 方式1：继承 Thread
        System.out.println("--- 方式1：继承 Thread ---");
        Thread t1 = new MyThread("Thread-1");
        Thread t2 = new MyThread("Thread-2");
        t1.start();
        t2.start();

        // 等待线程执行完成
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();

        // 方式2：实现 Runnable
        System.out.println("--- 方式2：实现 Runnable ---");
        Thread t3 = new Thread(new MyRunnable("Runnable-1"));
        Thread t4 = new Thread(new MyRunnable("Runnable-2"));
        t3.start();
        t4.start();

        try {
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();

        // 方式3：实现 Callable（有返回值）
        System.out.println("--- 方式3：实现 Callable（有返回值）---");
        java.util.concurrent.FutureTask<String> futureTask1 = 
            new java.util.concurrent.FutureTask<>(new MyCallable("Callable-1"));
        java.util.concurrent.FutureTask<String> futureTask2 = 
            new java.util.concurrent.FutureTask<>(new MyCallable("Callable-2"));

        Thread t5 = new Thread(futureTask1);
        Thread t6 = new Thread(futureTask2);
        t5.start();
        t6.start();

        try {
            // 获取返回值
            String result1 = futureTask1.get();
            String result2 = futureTask2.get();
            System.out.println("返回值1: " + result1);
            System.out.println("返回值2: " + result2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========");
    }

    /**
     * 演示线程的生命周期
     */
    public static void demonstrateThreadLifecycle() {
        System.out.println("\n========== 线程生命周期演示 ==========\n");

        Thread thread = new Thread(() -> {
            System.out.println("线程开始执行...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程执行完成");
        });

        // NEW 状态
        System.out.println("创建后状态: " + thread.getState()); // NEW

        thread.start();

        // RUNNABLE 状态
        System.out.println("启动后状态: " + thread.getState()); // RUNNABLE

        try {
            thread.join(); // 等待线程执行完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TERMINATED 状态
        System.out.println("执行完成后状态: " + thread.getState()); // TERMINATED

        System.out.println("\n========== 演示完成 ==========");
    }

    /**
     * 演示线程的基本操作
     */
    public static void demonstrateThreadOperations() {
        System.out.println("\n========== 线程基本操作演示 ==========\n");

        // sleep() - 让线程休眠
        System.out.println("--- sleep() 演示 ---");
        Thread t1 = new Thread(() -> {
            System.out.println("线程开始，准备休眠2秒...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("线程被中断");
            }
            System.out.println("线程醒来");
        });
        t1.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // interrupt() - 中断线程
        System.out.println("\n--- interrupt() 演示 ---");
        Thread t2 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("线程运行中...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("捕获到中断异常，退出循环");
                    Thread.currentThread().interrupt(); // 重新设置中断标志
                    break;
                }
            }
        });
        t2.start();

        try {
            Thread.sleep(3000);
            t2.interrupt(); // 中断线程
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // join() - 等待线程完成
        System.out.println("\n--- join() 演示 ---");
        Thread t3 = new Thread(() -> {
            System.out.println("子线程开始执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程执行完成");
        });

        System.out.println("主线程启动子线程");
        t3.start();
        try {
            System.out.println("主线程等待子线程完成...");
            t3.join();
            System.out.println("主线程继续执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========");
    }

    public static void main(String[] args) {
        // 演示线程创建方式
        demonstrateThreadCreation();

        // 演示线程生命周期
        demonstrateThreadLifecycle();

        // 演示线程基本操作
        demonstrateThreadOperations();
    }
}
