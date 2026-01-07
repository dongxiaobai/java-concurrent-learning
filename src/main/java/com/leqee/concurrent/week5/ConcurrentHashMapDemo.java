package com.leqee.concurrent.week5;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Week 5 - Day 1-2: ConcurrentHashMap 并发哈希表
 * 
 * 学习目标：
 * 1. ConcurrentHashMap 的线程安全性
 * 2. 与 HashMap、Hashtable 的区别
 * 3. 分段锁机制（JDK 7）和 CAS + synchronized（JDK 8+）
 * 4. 常用操作方法
 */
public class ConcurrentHashMapDemo {

    /**
     * 场景1：多线程并发写入
     */
    public static void demonstrateConcurrentWrite() {
        System.out.println("========== ConcurrentHashMap - 并发写入演示 ==========\n");

        ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

        // 创建多个线程同时写入
        Thread[] writers = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            writers[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String key = "key-" + threadId + "-" + j;
                    map.put(key, j);
                }
                System.out.println("Writer-" + threadId + " 完成写入");
            }, "Writer-" + i);
        }

        long startTime = System.currentTimeMillis();
        for (Thread writer : writers) {
            writer.start();
        }

        for (Thread writer : writers) {
            try {
                writer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();

        System.out.println("写入完成，Map大小: " + map.size());
        System.out.println("耗时: " + (endTime - startTime) + "ms");
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：computeIfAbsent 和 computeIfPresent
     */
    public static void demonstrateComputeMethods() {
        System.out.println("========== ConcurrentHashMap - compute 方法演示 ==========\n");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // computeIfAbsent: 如果key不存在，则计算并放入
        String key1 = "key1";
        Integer value1 = map.computeIfAbsent(key1, k -> {
            System.out.println("计算 key1 的值");
            return 100;
        });
        System.out.println("key1 的值: " + value1);

        // 再次调用，不会重新计算
        Integer value1Again = map.computeIfAbsent(key1, k -> {
            System.out.println("这行不会执行");
            return 200;
        });
        System.out.println("key1 的值（再次）: " + value1Again);

        // computeIfPresent: 如果key存在，则重新计算
        map.put("key2", 10);
        Integer value2 = map.computeIfPresent("key2", (k, v) -> {
            System.out.println("重新计算 key2，旧值: " + v);
            return v * 2;
        });
        System.out.println("key2 的新值: " + value2);

        // compute: 无论key是否存在都计算
        Integer value3 = map.compute("key3", (k, v) -> {
            if (v == null) {
                System.out.println("key3 不存在，创建新值");
                return 1;
            } else {
                System.out.println("key3 存在，旧值: " + v);
                return v + 1;
            }
        });
        System.out.println("key3 的值: " + value3);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：多线程安全操作
     */
    public static void demonstrateThreadSafety() {
        System.out.println("========== ConcurrentHashMap - 线程安全演示 ==========\n");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // 线程1：写入数据
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put("key" + i, i);
            }
            System.out.println("Writer 完成写入");
        }, "Writer");

        // 线程2：读取数据
        Thread reader = new Thread(() -> {
            int count = 0;
            while (count < 1000) {
                for (int i = 0; i < 1000; i++) {
                    if (map.containsKey("key" + i)) {
                        count++;
                    }
                }
            }
            System.out.println("Reader 完成读取");
        }, "Reader");

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("最终Map大小: " + map.size());
        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：遍历操作
     */
    public static void demonstrateIteration() {
        System.out.println("========== ConcurrentHashMap - 遍历操作演示 ==========\n");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("key" + i, i);
        }

        // forEach: 遍历所有键值对
        System.out.println("--- forEach ---");
        map.forEach((k, v) -> System.out.println(k + " = " + v));

        // forEachKey: 遍历所有key
        System.out.println("\n--- forEachKey ---");
        map.forEachKey(2, k -> System.out.println("Key: " + k));

        // forEachValue: 遍历所有value
        System.out.println("\n--- forEachValue ---");
        map.forEachValue(2, v -> System.out.println("Value: " + v));

        // search: 搜索第一个满足条件的元素
        System.out.println("\n--- search ---");
        String found = map.search(2, (k, v) -> v > 5 ? k : null);
        System.out.println("找到第一个值大于5的key: " + found);

        // reduce: 归约操作
        System.out.println("\n--- reduce ---");
        Integer sum = map.reduceValues(2, Integer::sum);
        System.out.println("所有值的总和: " + sum);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateConcurrentWrite();
        demonstrateComputeMethods();
        demonstrateThreadSafety();
        demonstrateIteration();
    }
}

