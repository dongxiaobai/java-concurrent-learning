package com.leqee.concurrent.week7;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Week 7 - Day 3-4: AtomicReference 原子引用
 * 
 * 学习目标：
 * 1. AtomicReference 的基本用法
 * 2. 原子更新对象引用
 * 3. CAS 操作在对象引用上的应用
 * 4. 实际应用场景：无锁数据结构
 */
public class AtomicReferenceDemo {

    /**
     * 简单的用户类
     */
    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    /**
     * 场景1：基本操作
     */
    public static void demonstrateBasicOperations() {
        System.out.println("========== AtomicReference - 基本操作演示 ==========\n");

        AtomicReference<User> atomicUser = new AtomicReference<>();

        User user1 = new User("Alice", 25);
        User user2 = new User("Bob", 30);

        // set 和 get
        atomicUser.set(user1);
        System.out.println("设置 user1: " + atomicUser.get());

        atomicUser.set(user2);
        System.out.println("设置 user2: " + atomicUser.get());

        // compareAndSet
        User user3 = new User("Charlie", 35);
        boolean success = atomicUser.compareAndSet(user2, user3);
        System.out.println("compareAndSet(user2, user3): " + success);
        System.out.println("当前值: " + atomicUser.get());

        boolean fail = atomicUser.compareAndSet(user2, user1);
        System.out.println("compareAndSet(user2, user1): " + fail + " (失败，因为当前值不是 user2)");
        System.out.println("当前值: " + atomicUser.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景2：原子更新对象属性
     */
    public static void demonstrateUpdateObject() {
        System.out.println("========== AtomicReference - 原子更新对象 ==========\n");

        AtomicReference<User> atomicUser = new AtomicReference<>(new User("Alice", 25));

        // getAndUpdate
        User oldUser = atomicUser.getAndUpdate(user -> {
            return new User(user.getName(), user.getAge() + 1);
        });
        System.out.println("旧用户: " + oldUser);
        System.out.println("新用户: " + atomicUser.get());

        // updateAndGet
        User newUser = atomicUser.updateAndGet(user -> {
            return new User(user.getName().toUpperCase(), user.getAge());
        });
        System.out.println("更新后: " + newUser);

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景3：无锁栈实现
     */
    static class LockFreeStack<T> {
        private static class Node<T> {
            final T value;
            Node<T> next;

            Node(T value) {
                this.value = value;
            }
        }

        private final AtomicReference<Node<T>> head = new AtomicReference<>();

        public void push(T value) {
            Node<T> newHead = new Node<>(value);
            Node<T> oldHead;
            do {
                oldHead = head.get();
                newHead.next = oldHead;
            } while (!head.compareAndSet(oldHead, newHead));
        }

        public T pop() {
            Node<T> oldHead;
            Node<T> newHead;
            do {
                oldHead = head.get();
                if (oldHead == null) {
                    return null;
                }
                newHead = oldHead.next;
            } while (!head.compareAndSet(oldHead, newHead));
            return oldHead.value;
        }

        public boolean isEmpty() {
            return head.get() == null;
        }
    }

    public static void demonstrateLockFreeStack() {
        System.out.println("========== AtomicReference - 无锁栈演示 ==========\n");

        LockFreeStack<String> stack = new LockFreeStack<>();

        // 单线程操作
        stack.push("A");
        stack.push("B");
        stack.push("C");

        System.out.println("弹出: " + stack.pop());
        System.out.println("弹出: " + stack.pop());
        System.out.println("弹出: " + stack.pop());
        System.out.println("是否为空: " + stack.isEmpty());

        // 多线程操作
        System.out.println("\n--- 多线程操作 ---");
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    stack.push("Thread-" + threadId + "-Item-" + j);
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

        int count = 0;
        while (!stack.isEmpty()) {
            stack.pop();
            count++;
        }
        System.out.println("总共弹出: " + count + " 个元素");

        System.out.println("\n========== 演示完成 ==========\n");
    }

    /**
     * 场景4：版本号更新
     */
    public static void demonstrateVersionControl() {
        System.out.println("========== AtomicReference - 版本控制演示 ==========\n");

        AtomicReference<String> version = new AtomicReference<>("v1.0");

        Thread[] updaters = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int updaterId = i;
            updaters[i] = new Thread(() -> {
                String currentVersion = version.get();
                String newVersion = "v" + (updaterId + 2) + ".0";
                boolean success = version.compareAndSet(currentVersion, newVersion);
                System.out.println("Updater-" + updaterId + 
                                 " 尝试从 " + currentVersion + 
                                 " 更新到 " + newVersion + 
                                 ": " + (success ? "成功" : "失败"));
            });
        }

        for (Thread updater : updaters) {
            updater.start();
        }

        for (Thread updater : updaters) {
            try {
                updater.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("最终版本: " + version.get());

        System.out.println("\n========== 演示完成 ==========\n");
    }

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateUpdateObject();
        demonstrateLockFreeStack();
        demonstrateVersionControl();
    }
}

