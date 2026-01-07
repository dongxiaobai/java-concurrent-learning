package com.leqee.concurrent.week1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 演示 Runnable 如何实现"任务和线程分离"
 * 
 * 核心理解：
 * 1. 任务（Task）= 做什么（What to do）
 * 2. 线程（Thread）= 怎么执行（How to execute）
 * 3. Runnable 将两者分离，让任务可以在不同环境中执行
 */
public class RunnableSeparationDemo {

    // ========== 方式1：继承 Thread（耦合） ==========
    
    /**
     * 问题：任务逻辑和线程对象耦合在一起
     * MyTask1 既是一个任务，又是一个线程
     */
    static class MyTask1 extends Thread {
        private String name;
        
        public MyTask1(String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
            System.out.println(name + " 执行任务");
        }
    }
    
    // ========== 方式2：实现 Runnable（分离） ==========
    
    /**
     * 优势：任务逻辑和线程对象分离
     * MyTask2 只是一个任务，不包含线程的概念
     */
    static class MyTask2 implements Runnable {
        private String name;
        
        public MyTask2(String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
            System.out.println(name + " 执行任务");
        }
    }
    
    /**
     * 场景1：复用同一个任务对象
     * 
     * 方式1（继承Thread）的问题：
     * - 每次执行都需要创建新的 Thread 对象
     * - 任务逻辑和线程对象绑定，无法复用
     */
    public static void demonstrateReusability() {
        System.out.println("========== 场景1：复用任务对象 ==========\n");
        
        // 方式1：继承 Thread - 需要创建多个对象
        System.out.println("方式1（继承Thread）：");
        for (int i = 0; i < 3; i++) {
            MyTask1 task = new MyTask1("Task-" + i);
            task.start();  // 每次都要创建新对象
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 方式2：实现 Runnable - 可以复用同一个任务对象
        System.out.println("\n方式2（实现Runnable）：");
        MyTask2 task = new MyTask2("SharedTask");  // 只创建一次
        for (int i = 0; i < 3; i++) {
            new Thread(task).start();  // 复用同一个任务对象
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n说明：Runnable 方式可以复用任务对象，更节省内存");
    }
    
    /**
     * 场景2：在线程池中使用
     * 
     * 方式1（继承Thread）的问题：
     * - Thread 对象不适合在线程池中使用
     * - 线程池管理的是任务，不是线程
     */
    public static void demonstrateThreadPool() {
        System.out.println("\n========== 场景2：在线程池中使用 ==========\n");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // 方式1：继承 Thread - 虽然可以，但不是最佳实践
        System.out.println("方式1（继承Thread）：");
        executor.execute(new MyTask1("ThreadTask"));
        
        // 方式2：实现 Runnable - 完美适配线程池
        System.out.println("\n方式2（实现Runnable）：");
        MyTask2 task = new MyTask2("RunnableTask");
        executor.execute(task);  // 线程池需要的是 Runnable
        
        executor.shutdown();
        
        System.out.println("\n说明：线程池的设计就是接收 Runnable，而不是 Thread");
    }
    
    /**
     * 场景3：灵活的执行方式
     * 
     * 方式1（继承Thread）的问题：
     * - 任务只能在新线程中执行
     * - 无法在当前线程中直接执行
     */
    public static void demonstrateFlexibleExecution() {
        System.out.println("\n========== 场景3：灵活的执行方式 ==========\n");
        
        MyTask2 task = new MyTask2("FlexibleTask");
        
        // ✅ 可以在当前线程中执行（同步）
        System.out.println("在当前线程中执行（同步）：");
        task.run();  // 直接调用，不创建新线程
        
        // ✅ 可以在新线程中执行（异步）
        System.out.println("\n在新线程中执行（异步）：");
        new Thread(task).start();
        
        // ✅ 可以根据条件选择执行方式
        System.out.println("\n根据条件选择执行方式：");
        boolean useAsync = true;
        if (useAsync) {
            new Thread(task).start();
        } else {
            task.run();  // 同步执行
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n说明：Runnable 任务可以在不同环境中执行，更灵活");
    }
    
    /**
     * 场景4：任务和线程的职责分离
     * 
     * 核心理解：
     * - 任务（Runnable）：定义"做什么"
     * - 线程（Thread）：负责"怎么执行"
     */
    public static void demonstrateSeparation() {
        System.out.println("\n========== 场景4：职责分离 ==========\n");
        
        // 任务：只定义要做什么
        Runnable task = () -> {
            System.out.println("执行任务逻辑");
        };
        
        // 线程1：用新线程执行
        Thread thread1 = new Thread(task);
        thread1.start();
        
        // 线程2：用另一个线程执行同一个任务
        Thread thread2 = new Thread(task);
        thread2.start();
        
        // 当前线程：也可以执行同一个任务
        task.run();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n说明：");
        System.out.println("- 任务（Runnable）：定义业务逻辑");
        System.out.println("- 线程（Thread）：提供执行环境");
        System.out.println("- 两者分离，可以灵活组合");
    }
    
    /**
     * 核心概念总结
     */
    public static void demonstrateConcept() {
        System.out.println("\n========== 核心概念总结 ==========\n");
        
        System.out.println("1. 任务 vs 线程：");
        System.out.println("   任务（Task/Runnable）= 做什么（What to do）");
        System.out.println("   线程（Thread）= 怎么执行（How to execute）");
        System.out.println();
        
        System.out.println("2. 继承 Thread 的问题：");
        System.out.println("   - 把'做什么'和'怎么执行'混在一起");
        System.out.println("   - 任务就是线程，线程就是任务");
        System.out.println("   - 无法分离，不够灵活");
        System.out.println();
        
        System.out.println("3. 实现 Runnable 的优势：");
        System.out.println("   - 任务只定义'做什么'");
        System.out.println("   - 线程负责'怎么执行'");
        System.out.println("   - 任务可以独立存在，可以在不同环境中执行");
        System.out.println();
        
        System.out.println("4. 类比理解：");
        System.out.println("   - 继承 Thread：就像'会开车的司机'，司机和开车技能绑定");
        System.out.println("   - 实现 Runnable：就像'会做菜的厨师'，厨师可以给不同餐厅打工");
        System.out.println();
        
        System.out.println("5. 实际优势：");
        System.out.println("   ✅ 可以复用任务对象");
        System.out.println("   ✅ 可以在线程池中使用");
        System.out.println("   ✅ 可以灵活选择执行方式（同步/异步）");
        System.out.println("   ✅ 符合面向接口编程思想");
        System.out.println("   ✅ 任务和线程职责分离，更易维护");
    }
    
    public static void main(String[] args) {
        demonstrateReusability();
        demonstrateThreadPool();
        demonstrateFlexibleExecution();
        demonstrateSeparation();
        demonstrateConcept();
    }
}

