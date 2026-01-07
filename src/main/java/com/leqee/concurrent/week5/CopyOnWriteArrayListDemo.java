package com.leqee.concurrent.week5;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Week 5 - Day 3: CopyOnWriteArrayList 写时复制列表
 * 
 * 学习目标：
 * 1. CopyOnWriteArrayList 的写时复制机制
 * 2. 读多写少的场景优势
 * 3. 迭代器的弱一致性
 * 4. 与 ArrayList、Vector 的区别
 */
public class CopyOnWriteArrayListDemo {

    /**
     * 场景1：读多写少的场景
     */
    public static void demonstrateReadHeavy() {
        System.out.println("========== CopyOnWriteArrayList - 读多写少场景 ==========\n");

        List<String> list = new CopyOnWriteArrayList<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");

        // 多个读线程
        Thread[] readers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int readerId = i;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    // 读操作不需要加锁，性能高
                    int size = list.size();
                    if (size > 0) {
                        String element = list.get((int) (Math.random() * size));
                        System.out.println("Reader-" + readerId + " 读取: " + element);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Reader-" + i);
        }

        // 写线程（较少）
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                String newElement = "新元素-" + i;
                list.add(newElement);
                System.out.println("Writer 添加: " + newElement);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Writer");

        for (Thread reader : readers) {
            reader.start();
        }
        writer.start();

        try {
            for (Thread reader : readers) {
                reader.join();
            }
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("最终列表大小: " + list.size());
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：迭代器的弱一致性
     */
    public static void demonstrateWeakConsistency() {
        System.out.println("========== CopyOnWriteArrayList - 弱一致性演示 ==========\n");

        List<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        // 获取迭代器
        Iterator<String> iterator = list.iterator();

        // 在迭代过程中修改列表
        Thread modifier = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("修改线程添加元素");
                list.add("D");
                list.add("E");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        modifier.start();

        // 使用迭代器遍历（不会看到新添加的元素）
        System.out.println("迭代器遍历:");
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }

        try {
            modifier.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 重新获取迭代器，可以看到新元素
        System.out.println("\n重新获取迭代器遍历:");
        Iterator<String> newIterator = list.iterator();
        while (newIterator.hasNext()) {
            System.out.println("  " + newIterator.next());
        }

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：写时复制的开销演示
     */
    public static void demonstrateCopyOverhead() {
        System.out.println("========== CopyOnWriteArrayList - 写时复制开销 ==========\n");

        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        System.out.println("初始大小: " + list.size());

        // 频繁写入操作
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            list.add(1000 + i); // 每次写入都会复制整个数组
        }
        long endTime = System.currentTimeMillis();

        System.out.println("写入100个元素后大小: " + list.size());
        System.out.println("耗时: " + (endTime - startTime) + "ms");
        System.out.println("注意：频繁写入时，CopyOnWriteArrayList 性能较差");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：适用场景说明
     */
    public static void demonstrateUseCases() {
        System.out.println("========== CopyOnWriteArrayList - 适用场景 ==========\n");

        System.out.println("CopyOnWriteArrayList 适用场景：");
        System.out.println("1. 读多写少：监听器列表、配置信息等");
        System.out.println("2. 迭代操作频繁：需要遍历但很少修改");
        System.out.println("3. 弱一致性可接受：迭代时看不到最新修改");
        System.out.println("\n不适用场景：");
        System.out.println("1. 写多读少：频繁写入会导致性能问题");
        System.out.println("2. 需要强一致性：迭代时必须看到最新修改");
        System.out.println("3. 内存敏感：写时复制会占用额外内存");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateReadHeavy();
        demonstrateWeakConsistency();
        demonstrateCopyOverhead();
        demonstrateUseCases();
    }
}

