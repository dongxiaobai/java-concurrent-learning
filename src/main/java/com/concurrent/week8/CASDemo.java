package com.concurrent.week8;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Week 8 - Day 1-4: CAS (Compare-And-Swap) 原理与实战
 * 
 * 学习目标：
 * 1. CAS 操作的基本原理
 * 2. CAS 的优缺点
 * 3. ABA 问题及解决方案
 * 4. CAS 在 Java 中的应用
 * 5. 自旋锁的实现
 */
public class CASDemo {

    /**
     * 场景1：CAS 基本操作演示
     */
    public static void demonstrateCASBasic() {
        System.out.println("========== CAS - 基本操作演示 ==========\n");

        AtomicInteger atomicInt = new AtomicInteger(10);

        System.out.println("当前值: " + atomicInt.get());

        // CAS 操作：比较当前值是否为10，如果是则设置为20
        boolean success1 = atomicInt.compareAndSet(10, 20);
        System.out.println("compareAndSet(10, 20): " + success1);
        System.out.println("当前值: " + atomicInt.get());

        // CAS 操作失败：当前值不是10，所以不会更新
        boolean success2 = atomicInt.compareAndSet(10, 30);
        System.out.println("compareAndSet(10, 30): " + success2);
        System.out.println("当前值: " + atomicInt.get());

        // CAS 操作成功：当前值是20，更新为30
        boolean success3 = atomicInt.compareAndSet(20, 30);
        System.out.println("compareAndSet(20, 30): " + success3);
        System.out.println("当前值: " + atomicInt.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：CAS 自旋实现
     */
    public static void demonstrateCASSpin() {
        System.out.println("========== CAS - 自旋实现演示 ==========\n");

        AtomicInteger counter = new AtomicInteger(0);

        // 使用 CAS 自旋实现原子递增
        int oldValue, newValue;
        do {
            oldValue = counter.get();
            newValue = oldValue + 1;
        } while (!counter.compareAndSet(oldValue, newValue));

        System.out.println("自旋后值: " + counter.get());

        // 多线程自旋
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    int old, newVal;
                    do {
                        old = counter.get();
                        newVal = old + 1;
                    } while (!counter.compareAndSet(old, newVal));
                }
            });
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

        System.out.println("最终值: " + counter.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：ABA 问题演示
     */
    public static void demonstrateABAProblem() {
        System.out.println("========== CAS - ABA 问题演示 ==========\n");

        AtomicInteger value = new AtomicInteger(10);

        // 线程1：读取值，准备更新
        Thread thread1 = new Thread(() -> {
            int current = value.get();
            System.out.println("Thread-1 读取值: " + current);
            try {
                Thread.sleep(2000); // 模拟处理时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 尝试更新（可能失败，因为值已经被其他线程修改过）
            boolean success = value.compareAndSet(current, 20);
            System.out.println("Thread-1 CAS(" + current + ", 20): " + success);
        });

        // 线程2：修改值，然后又改回来（ABA问题）
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread-2 修改值 10 -> 15");
            value.compareAndSet(10, 15);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread-2 修改值 15 -> 10");
            value.compareAndSet(15, 10);
            System.out.println("Thread-2 完成，当前值: " + value.get());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n说明：ABA 问题可能导致某些场景下的逻辑错误");
        System.out.println("解决方案：使用 AtomicStampedReference 或 AtomicMarkableReference");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：使用 AtomicStampedReference 解决 ABA 问题
     */
    public static void demonstrateABA解决方案() {
        System.out.println("========== CAS - ABA 问题解决方案 ==========\n");

        java.util.concurrent.atomic.AtomicStampedReference<Integer> stampedRef = 
            new java.util.concurrent.atomic.AtomicStampedReference<>(10, 0);

        // 线程1：读取值和版本号
        Thread thread1 = new Thread(() -> {
            int[] stampHolder = new int[1];
            int current = stampedRef.get(stampHolder);
            int stamp = stampHolder[0];
            System.out.println("Thread-1 读取值: " + current + ", 版本: " + stamp);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 使用版本号进行 CAS
            boolean success = stampedRef.compareAndSet(current, 20, stamp, stamp + 1);
            System.out.println("Thread-1 CAS(值=" + current + ", 版本=" + stamp + "): " + success);
        });

        // 线程2：修改值并更新版本号
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int[] stampHolder = new int[1];
            int current = stampedRef.get(stampHolder);
            System.out.println("Thread-2 修改值 " + current + " -> 15, 版本 " + stampHolder[0] + " -> " + (stampHolder[0] + 1));
            stampedRef.compareAndSet(current, 15, stampHolder[0], stampHolder[0] + 1);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            current = stampedRef.get(stampHolder);
            System.out.println("Thread-2 修改值 " + current + " -> 10, 版本 " + stampHolder[0] + " -> " + (stampHolder[0] + 1));
            stampedRef.compareAndSet(current, 10, stampHolder[0], stampHolder[0] + 1);
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[] stampHolder = new int[1];
        System.out.println("最终值: " + stampedRef.get(stampHolder) + ", 版本: " + stampHolder[0]);

        System.out.println("\n说明：使用版本号可以检测到值被修改过，避免 ABA 问题");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景5：CAS 优缺点总结
     */
    public static void demonstrateCASProsAndCons() {
        System.out.println("========== CAS - 优缺点总结 ==========\n");

        System.out.println("CAS 的优点:");
        System.out.println("1. 无锁操作，避免线程阻塞和唤醒的开销");
        System.out.println("2. 性能高，适合高并发场景");
        System.out.println("3. 避免了死锁问题");
        System.out.println("\nCAS 的缺点:");
        System.out.println("1. ABA 问题：值被修改后又改回原值");
        System.out.println("2. 自旋开销：如果长时间失败，CPU 占用高");
        System.out.println("3. 只能保证一个变量的原子性");
        System.out.println("4. 实现复杂，需要底层硬件支持");
        System.out.println("\n适用场景:");
        System.out.println("- 读多写少的场景");
        System.out.println("- 简单的原子操作（如计数、累加）");
        System.out.println("- 对性能要求高的场景");
        System.out.println("\n不适用场景:");
        System.out.println("- 复杂的同步逻辑");
        System.out.println("- 需要多个变量的原子操作");
        System.out.println("- 长时间自旋的场景");

        System.out.println("\n========== 说明完成 ==========\n");
    }

    /**
     * 场景6：简单自旋锁实现
     */
    static class SimpleSpinLock {
        private final AtomicInteger state = new AtomicInteger(0); // 0: 未锁定, 1: 已锁定

        public void lock() {
            while (!state.compareAndSet(0, 1)) {
                // 自旋等待
            }
        }

        public void unlock() {
            state.set(0);
        }
    }

    public static void demonstrateSpinLock() {
        System.out.println("========== CAS - 自旋锁实现演示 ==========\n");

        SimpleSpinLock spinLock = new SimpleSpinLock();
        int[] counter = {0};

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                spinLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " 获取锁");
                    counter[0]++;
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getName() + " counter: " + counter[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    spinLock.unlock();
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

        System.out.println("最终 counter: " + counter[0]);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateCASBasic();
        demonstrateCASSpin();
        demonstrateABAProblem();
        demonstrateABA解决方案();
        demonstrateCASProsAndCons();
        demonstrateSpinLock();
    }
}

