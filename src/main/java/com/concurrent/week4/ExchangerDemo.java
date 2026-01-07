package com.concurrent.week4;

import java.util.concurrent.Exchanger;

/**
 * Week 4 - Day 1-2: Exchanger 交换器
 * 
 * 学习目标：
 * 1. Exchanger 的用途：两个线程之间交换数据
 * 2. exchange() 方法的使用
 * 3. 实际应用场景：生产者-消费者、数据校对等
 */
public class ExchangerDemo {

    /**
     * 场景1：两个线程交换数据
     */
    public static void demonstrateBasicExchange() {
        System.out.println("========== Exchanger - 基本交换演示 ==========\n");

        Exchanger<String> exchanger = new Exchanger<>();

        // 线程1：发送数据
        Thread t1 = new Thread(() -> {
            try {
                String data = "Hello from Thread-1";
                System.out.println("Thread-1 准备发送: " + data);
                String received = exchanger.exchange(data); // 交换数据
                System.out.println("Thread-1 收到: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread-1");

        // 线程2：接收并发送数据
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000); // 模拟处理时间
                String data = "Hello from Thread-2";
                System.out.println("Thread-2 准备发送: " + data);
                String received = exchanger.exchange(data); // 交换数据
                System.out.println("Thread-2 收到: " + received);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：生产者-消费者数据交换
     */
    public static void demonstrateProducerConsumer() {
        System.out.println("========== Exchanger - 生产者消费者演示 ==========\n");

        Exchanger<String> exchanger = new Exchanger<>();

        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String data = "Product-" + i;
                    System.out.println("生产者生产: " + data);
                    Thread.sleep(500);
                    String emptyBuffer = exchanger.exchange(data); // 交换：发送产品，接收空缓冲区
                    System.out.println("生产者收到空缓冲区: " + emptyBuffer);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Producer");

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String emptyBuffer = "Empty-Buffer";
                    String product = exchanger.exchange(emptyBuffer); // 交换：发送空缓冲区，接收产品
                    System.out.println("消费者消费: " + product);
                    Thread.sleep(1000); // 模拟消费时间
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：数据校对场景
     */
    public static void demonstrateDataVerification() {
        System.out.println("========== Exchanger - 数据校对演示 ==========\n");

        Exchanger<Integer> exchanger = new Exchanger<>();

        // 数据源1
        Thread source1 = new Thread(() -> {
            try {
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += i;
                }
                System.out.println("数据源1计算总和: " + sum);
                int otherSum = exchanger.exchange(sum);
                System.out.println("数据源1收到对方总和: " + otherSum);
                if (sum == otherSum) {
                    System.out.println("数据源1: 数据一致 ✓");
                } else {
                    System.out.println("数据源1: 数据不一致 ✗");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Source-1");

        // 数据源2
        Thread source2 = new Thread(() -> {
            try {
                Thread.sleep(500);
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += i;
                }
                System.out.println("数据源2计算总和: " + sum);
                int otherSum = exchanger.exchange(sum);
                System.out.println("数据源2收到对方总和: " + otherSum);
                if (sum == otherSum) {
                    System.out.println("数据源2: 数据一致 ✓");
                } else {
                    System.out.println("数据源2: 数据不一致 ✗");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Source-2");

        source1.start();
        source2.start();

        try {
            source1.join();
            source2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicExchange();
        demonstrateProducerConsumer();
        demonstrateDataVerification();
    }
}

