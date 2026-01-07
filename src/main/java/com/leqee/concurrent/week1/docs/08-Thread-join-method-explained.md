# Thread.join() 方法详解

## 🎯 核心理解

**join() 方法的作用：**
- 让**当前线程**等待**目标线程**执行完成
- 当前线程会**阻塞**，直到目标线程结束
- 用于**控制线程执行顺序**和**等待结果**

---

## 📊 基本概念

### 方法签名

```java
// 无限期等待，直到目标线程完成
public final void join() throws InterruptedException

// 等待指定时间（毫秒）
public final synchronized void join(long millis) throws InterruptedException

// 等待指定时间（毫秒 + 纳秒）
public final synchronized void join(long millis, int nanos) throws InterruptedException
```

---

## 💡 工作原理

### 简单理解

```java
Thread t = new Thread(() -> {
    // 执行任务
});
t.start();

// 主线程等待 t 完成
t.join();  // 主线程在这里阻塞，直到 t 执行完成

// t 完成后，主线程继续执行
System.out.println("t 已完成，主线程继续");
```

**关键点：**
- `join()` 是**调用者线程**等待**目标线程**
- 调用 `t.join()` 的线程会阻塞
- 目标线程 `t` 完成后，调用者线程才会继续

---

## 🔍 详细示例

### 示例1：基本用法

```java
public class JoinBasicDemo {
    public static void main(String[] args) {
        System.out.println("主线程开始: " + Thread.currentThread().getName());
        
        Thread t = new Thread(() -> {
            System.out.println("子线程开始: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000); // 模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程完成: " + Thread.currentThread().getName());
        }, "子线程");
        
        t.start();
        System.out.println("主线程启动子线程后继续执行");
        
        try {
            System.out.println("主线程等待子线程完成...");
            t.join(); // 主线程阻塞，等待子线程完成
            System.out.println("子线程已完成，主线程继续执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("主线程结束");
        
        // 输出顺序：
        // 主线程开始: main
        // 主线程启动子线程后继续执行
        // 主线程等待子线程完成...
        // 子线程开始: 子线程
        // 子线程完成: 子线程
        // 子线程已完成，主线程继续执行  ← join() 后执行
        // 主线程结束
    }
}
```

---

### 示例2：控制执行顺序

```java
public class JoinOrderDemo {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println("任务1执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务1完成");
        }, "任务1");
        
        Thread t2 = new Thread(() -> {
            System.out.println("任务2执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2完成");
        }, "任务2");
        
        // 不使用 join() - 顺序不确定
        System.out.println("=== 不使用 join() ===");
        t1.start();
        t2.start();
        // 输出顺序不确定
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 使用 join() - 保证顺序
        System.out.println("\n=== 使用 join() ===");
        Thread t3 = new Thread(() -> {
            System.out.println("任务3执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务3完成");
        }, "任务3");
        
        Thread t4 = new Thread(() -> {
            System.out.println("任务4执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务4完成");
        }, "任务4");
        
        t3.start();
        try {
            t3.join(); // 等待任务3完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t4.start(); // 任务3完成后才启动任务4
        try {
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 输出顺序固定：
        // 任务3执行
        // 任务3完成
        // 任务4执行
        // 任务4完成
    }
}
```

---

### 示例3：等待多个线程完成

```java
public class JoinMultipleDemo {
    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        
        // 创建多个线程
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                System.out.println("线程-" + threadId + " 开始执行");
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程-" + threadId + " 完成");
            }, "Thread-" + i);
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        System.out.println("所有线程已启动，主线程等待它们完成...");
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join(); // 等待每个线程完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("所有线程已完成，主线程继续执行");
        
        // 输出：
        // 所有线程已启动，主线程等待它们完成...
        // 线程-0 开始执行
        // 线程-1 开始执行
        // ...（顺序可能不同）
        // 线程-0 完成
        // 线程-1 完成
        // ...（所有线程完成后）
        // 所有线程已完成，主线程继续执行
    }
}
```

---

### 示例4：带超时的 join()

```java
public class JoinTimeoutDemo {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("任务开始执行");
            try {
                Thread.sleep(5000); // 执行5秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务完成");
        });
        
        t.start();
        
        try {
            System.out.println("等待任务完成（最多等待2秒）...");
            t.join(2000); // 最多等待2秒
            
            if (t.isAlive()) {
                System.out.println("任务未在2秒内完成，主线程继续执行");
            } else {
                System.out.println("任务已完成");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 输出：
        // 任务开始执行
        // 等待任务完成（最多等待2秒）...
        // 任务未在2秒内完成，主线程继续执行
        // （2秒后主线程继续，但任务还在执行）
        // 任务完成（5秒后）
    }
}
```

---

## 🔄 join() vs 其他方法

### join() vs sleep()

| 特性 | join() | sleep() |
|------|--------|---------|
| **等待对象** | 等待其他线程完成 | 等待指定时间 |
| **依赖关系** | 依赖目标线程状态 | 不依赖其他线程 |
| **使用场景** | 等待线程完成 | 定时等待 |

```java
// join() - 等待线程完成
Thread t = new Thread(() -> { ... });
t.start();
t.join(); // 等待 t 完成，时间不确定

// sleep() - 等待指定时间
Thread.sleep(2000); // 等待2秒，不依赖其他线程
```

---

### join() vs wait()

| 特性 | join() | wait() |
|------|--------|--------|
| **作用对象** | 等待线程完成 | 等待对象通知 |
| **调用方式** | 线程对象调用 | 对象锁调用 |
| **使用场景** | 线程同步 | 线程间通信 |

```java
// join() - 等待线程
Thread t = new Thread(() -> { ... });
t.start();
t.join(); // 等待线程完成

// wait() - 等待对象通知
synchronized (obj) {
    obj.wait(); // 等待 obj.notify()
}
```

---

## 🎯 使用场景

### 场景1：等待子线程完成后再继续

```java
public class WaitForCompletion {
    public static void main(String[] args) {
        Thread dataLoader = new Thread(() -> {
            // 加载数据
            loadData();
        });
        
        dataLoader.start();
        
        try {
            dataLoader.join(); // 等待数据加载完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 数据加载完成后才执行
        processData();
    }
}
```

---

### 场景2：并行计算后汇总结果

```java
public class ParallelComputation {
    public static void main(String[] args) {
        int[] results = new int[10];
        Thread[] threads = new Thread[10];
        
        // 并行计算
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = calculate(index);
            });
            threads[i].start();
        }
        
        // 等待所有计算完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 汇总结果
        int sum = 0;
        for (int result : results) {
            sum += result;
        }
        System.out.println("总和: " + sum);
    }
}
```

---

### 场景3：控制任务执行顺序

```java
public class SequentialTasks {
    public static void main(String[] args) {
        // 任务1：初始化
        Thread initTask = new Thread(() -> {
            initialize();
        });
        
        // 任务2：处理数据（依赖任务1）
        Thread processTask = new Thread(() -> {
            processData();
        });
        
        // 任务3：保存结果（依赖任务2）
        Thread saveTask = new Thread(() -> {
            saveResult();
        });
        
        initTask.start();
        try {
            initTask.join(); // 等待初始化完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        processTask.start();
        try {
            processTask.join(); // 等待处理完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        saveTask.start();
        try {
            saveTask.join(); // 等待保存完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("所有任务按顺序完成");
    }
}
```

---

## ⚠️ 注意事项

### 1. join() 会阻塞当前线程

```java
Thread t = new Thread(() -> { ... });
t.start();
t.join(); // ⚠️ 当前线程阻塞，不会继续执行后面的代码
System.out.println("这行代码要等 t 完成后才执行");
```

---

### 2. join() 可能抛出 InterruptedException

```java
try {
    t.join();
} catch (InterruptedException e) {
    // ⚠️ 必须处理中断异常
    System.out.println("等待被中断");
    Thread.currentThread().interrupt(); // 重新设置中断标志
}
```

---

### 3. 如果线程已经完成，join() 立即返回

```java
Thread t = new Thread(() -> { ... });
t.start();
t.join(); // 等待完成

// 如果再次调用 join()
t.join(); // ✅ 立即返回，因为线程已经完成
```

---

### 4. join(0) 等同于 join()

```java
t.join(0);  // 等同于 t.join()
t.join();   // 无限期等待
```

---

## 💡 最佳实践

### 1. 总是处理 InterruptedException

```java
try {
    thread.join();
} catch (InterruptedException e) {
    // 记录日志或重新设置中断标志
    Thread.currentThread().interrupt();
    // 或者根据业务逻辑决定如何处理
}
```

---

### 2. 使用超时避免无限等待

```java
// ✅ 推荐：使用超时
if (thread.join(5000)) {
    // 线程在5秒内完成
} else {
    // 超时处理
    System.out.println("线程执行超时");
}

// ❌ 不推荐：无限等待（可能导致死锁）
thread.join(); // 如果线程永远不会完成，会一直阻塞
```

---

### 3. 检查线程状态

```java
thread.start();
thread.join();

if (thread.isAlive()) {
    System.out.println("线程仍在运行");
} else {
    System.out.println("线程已完成");
}
```

---

### 4. 使用 CountDownLatch 替代多个 join()

```java
// ❌ 不推荐：多个 join()
for (Thread thread : threads) {
    thread.join();
}

// ✅ 推荐：使用 CountDownLatch
CountDownLatch latch = new CountDownLatch(threads.length);
for (Thread thread : threads) {
    thread.start();
}
latch.await(); // 等待所有线程完成
```

---

## 🔍 深入理解：join() 的实现原理

### 简化版实现

```java
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;
    
    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }
    
    if (millis == 0) {
        while (isAlive()) {
            wait(0); // 等待，直到线程完成
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break; // 超时
            }
            wait(delay); // 等待指定时间
            now = System.currentTimeMillis() - base;
        }
    }
}
```

**关键点：**
1. `join()` 内部使用 `wait()` 实现
2. 线程完成后会调用 `notifyAll()` 唤醒等待的线程
3. 使用 `synchronized` 保证线程安全

---

## 📝 总结

### 核心要点

1. **join() 的作用**
   - 让当前线程等待目标线程完成
   - 当前线程会阻塞

2. **使用场景**
   - 等待子线程完成
   - 控制线程执行顺序
   - 并行计算后汇总结果

3. **注意事项**
   - 会阻塞当前线程
   - 可能抛出 InterruptedException
   - 建议使用超时版本

4. **最佳实践**
   - 总是处理异常
   - 使用超时避免无限等待
   - 多个线程时考虑使用 CountDownLatch

### 记住这句话

> **join() 是"等待线程完成"的方法。调用 join() 的线程会阻塞，直到目标线程执行完成。**

### 类比理解

**join() 就像：**
- 家长等待孩子完成作业
- 主线程等待子线程完成任务
- 等待所有参与者到达后再开始

---

## 🎯 快速参考

```java
// 基本用法
thread.join();                    // 无限期等待
thread.join(5000);                // 等待5秒
thread.join(5000, 0);             // 等待5秒0纳秒

// 检查线程状态
thread.isAlive();                  // 线程是否还在运行

// 处理异常
try {
    thread.join();
} catch (InterruptedException e) {
    // 处理中断
}
```

