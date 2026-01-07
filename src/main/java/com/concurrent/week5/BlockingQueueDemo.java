package com.concurrent.week5;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Week 5 - Day 4-5: BlockingQueue 阻塞队列
 * 
 * 学习目标：
 * 1. BlockingQueue 的基本操作：put, take, offer, poll
 * 2. ArrayBlockingQueue 和 LinkedBlockingQueue
 * 3. 生产者-消费者模式
 * 4. 阻塞和非阻塞操作的区别
 */
public class BlockingQueueDemo {

    /**
     * 场景1：基本操作演示
     */
    public static void demonstrateBasicOperations() {
        System.out.println("========== BlockingQueue - 基本操作演示 ==========\n");

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3); // 容量为3

        // put: 阻塞式添加（队列满时阻塞）
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    String item = "Item-" + i;
                    System.out.println("Producer 放入: " + item);
                    queue.put(item); // 如果队列满，会阻塞
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Producer");

        // take: 阻塞式获取（队列空时阻塞）
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(500);
                    String item = queue.take(); // 如果队列空，会阻塞
                    System.out.println("Consumer 取出: " + item);
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
     * 场景2：生产者-消费者模式
     */
    public static void demonstrateProducerConsumer() {
        System.out.println("========== BlockingQueue - 生产者消费者模式 ==========\n");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

        // 多个生产者
        Thread[] producers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        int product = producerId * 100 + j;
                        queue.put(product);
                        System.out.println("Producer-" + producerId + " 生产: " + product);
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "Producer-" + i);
        }

        // 多个消费者
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            consumers[i] = new Thread(() -> {
                try {
                    while (true) {
                        Integer product = queue.take();
                        System.out.println("Consumer-" + consumerId + " 消费: " + product);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    // 正常退出
                }
            }, "Consumer-" + i);
        }

        for (Thread producer : producers) {
            producer.start();
        }
        for (Thread consumer : consumers) {
            consumer.start();
        }

        try {
            for (Thread producer : producers) {
                producer.join();
            }
            Thread.sleep(2000);
            // 中断消费者线程
            for (Thread consumer : consumers) {
                consumer.interrupt();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：非阻塞操作 - offer 和 poll
     */
    public static void demonstrateNonBlocking() {
        System.out.println("========== BlockingQueue - 非阻塞操作演示 ==========\n");

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

        // offer: 非阻塞添加，失败返回false
        System.out.println("offer 操作:");
        System.out.println("  offer(\"A\"): " + queue.offer("A"));
        System.out.println("  offer(\"B\"): " + queue.offer("B"));
        System.out.println("  offer(\"C\"): " + queue.offer("C")); // 队列满，返回false

        // poll: 非阻塞获取，失败返回null
        System.out.println("\npoll 操作:");
        System.out.println("  poll(): " + queue.poll());
        System.out.println("  poll(): " + queue.poll());
        System.out.println("  poll(): " + queue.poll()); // 队列空，返回null

        // offer 和 poll 带超时
        System.out.println("\n带超时的操作:");
        try {
            queue.offer("X", 1, TimeUnit.SECONDS);
            System.out.println("  offer with timeout: 成功");
            String item = queue.poll(1, TimeUnit.SECONDS);
            System.out.println("  poll with timeout: " + item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：ArrayBlockingQueue vs LinkedBlockingQueue
     */
    public static void demonstrateQueueTypes() {
        System.out.println("========== BlockingQueue - 队列类型对比 ==========\n");

        // ArrayBlockingQueue: 有界队列，基于数组
        BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(5);
        System.out.println("ArrayBlockingQueue:");
        System.out.println("  - 有界队列，需要指定容量");
        System.out.println("  - 基于数组实现");
        System.out.println("  - 性能稳定");

        // LinkedBlockingQueue: 可选有界，基于链表
        BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(5); // 有界
        BlockingQueue<String> linkedQueueUnbounded = new LinkedBlockingQueue<>(); // 无界
        System.out.println("\nLinkedBlockingQueue:");
        System.out.println("  - 可选有界或无界");
        System.out.println("  - 基于链表实现");
        System.out.println("  - 吞吐量通常更高");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateProducerConsumer();
        demonstrateNonBlocking();
        demonstrateQueueTypes();
    }
}

