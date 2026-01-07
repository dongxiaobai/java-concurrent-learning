# start() 和 run() 方法详解

## 🎯 核心区别

### start() 方法
- ✅ **启动新线程**：JVM 会创建新线程并调用 `run()` 方法
- ✅ **异步执行**：主线程不会等待，继续执行
- ✅ **只能调用一次**：多次调用会抛出 `IllegalThreadStateException`

### run() 方法
- ❌ **普通方法调用**：不会创建新线程
- ❌ **同步执行**：在当前线程中执行
- ⚠️ **注意**：不要直接调用 `run()`，应该调用 `start()`

---

## 📊 对比表格

| 特性 | start() | run() |
|------|---------|-------|
| **创建新线程** | ✅ 是 | ❌ 否 |
| **执行方式** | 异步（并行） | 同步（顺序） |
| **调用次数** | 只能一次 | 可以多次 |
| **线程状态** | NEW → RUNNABLE | 不改变线程状态 |
| **使用场景** | 启动线程 | 普通方法调用 |

---

## 💡 详细示例

### 示例1：start() 方法 - 创建新线程

```java
public class StartDemo {
    public static void main(String[] args) {
        System.out.println("主线程开始: " + Thread.currentThread().getName());
        
        Thread t = new Thread(() -> {
            System.out.println("子线程执行: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程完成: " + Thread.currentThread().getName());
        });
        
        System.out.println("调用 start() 前，线程状态: " + t.getState()); // NEW
        
        t.start(); // ✅ 启动新线程
        
        System.out.println("调用 start() 后，线程状态: " + t.getState()); // RUNNABLE
        System.out.println("主线程继续执行: " + Thread.currentThread().getName());
        
        // 输出结果（顺序可能不同）：
        // 主线程开始: main
        // 调用 start() 前，线程状态: NEW
        // 调用 start() 后，线程状态: RUNNABLE
        // 主线程继续执行: main
        // 子线程执行: Thread-0
        // 子线程完成: Thread-0
    }
}
```

**关键点：**
- `start()` 后，主线程立即继续执行，不等待子线程
- 子线程在新线程中执行，线程名是 `Thread-0`（不是 `main`）
- 执行顺序不确定（由 JVM 调度）

---

### 示例2：run() 方法 - 普通方法调用

```java
public class RunDemo {
    public static void main(String[] args) {
        System.out.println("主线程开始: " + Thread.currentThread().getName());
        
        Thread t = new Thread(() -> {
            System.out.println("执行任务: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务完成: " + Thread.currentThread().getName());
        });
        
        System.out.println("调用 run() 前，线程状态: " + t.getState()); // NEW
        
        t.run(); // ❌ 普通方法调用，不创建新线程
        
        System.out.println("调用 run() 后，线程状态: " + t.getState()); // NEW（未改变）
        System.out.println("主线程继续执行: " + Thread.currentThread().getName());
        
        // 输出结果（顺序固定）：
        // 主线程开始: main
        // 调用 run() 前，线程状态: NEW
        // 执行任务: main          ← 注意：线程名是 main，不是 Thread-0
        // 任务完成: main
        // 调用 run() 后，线程状态: NEW
        // 主线程继续执行: main
    }
}
```

**关键点：**
- `run()` 在当前线程（main）中执行
- 主线程会等待 `run()` 执行完成才继续
- 线程状态没有改变（仍然是 NEW）
- 执行顺序是固定的（同步执行）

---

### 示例3：对比执行顺序

```java
public class CompareDemo {
    public static void main(String[] args) {
        System.out.println("=== 使用 start() ===");
        Thread t1 = new Thread(() -> {
            System.out.println("任务1开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务1完成");
        });
        
        t1.start(); // ✅ 异步执行
        System.out.println("主线程继续"); // 立即执行，不等待任务1
        
        try {
            t1.join(); // 等待任务1完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n=== 使用 run() ===");
        Thread t2 = new Thread(() -> {
            System.out.println("任务2开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2完成");
        });
        
        t2.run(); // ❌ 同步执行
        System.out.println("主线程继续"); // 等待任务2完成后才执行
        
        // 输出结果：
        // === 使用 start() ===
        // 主线程继续          ← 立即执行
        // 任务1开始
        // 任务1完成
        //
        // === 使用 run() ===
        // 任务2开始
        // 任务2完成
        // 主线程继续          ← 等待任务2完成后才执行
    }
}
```

---

### 示例4：多次调用 start() 会报错

```java
public class StartErrorDemo {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("执行任务");
        });
        
        t.start(); // ✅ 第一次调用，正常
        
        try {
            t.start(); // ❌ 第二次调用，抛出异常
        } catch (IllegalThreadStateException e) {
            System.out.println("错误: " + e.getMessage());
            // 输出: 错误: null
        }
        
        // 但是 run() 可以多次调用
        t.run(); // ✅ 可以多次调用
        t.run(); // ✅ 可以多次调用
    }
}
```

**关键点：**
- `start()` 只能调用一次
- `run()` 可以多次调用（但通常没有意义）

---

### 示例5：线程状态变化

```java
public class StateDemo {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("任务执行中");
        });
        
        System.out.println("创建后: " + t.getState()); // NEW
        
        t.start();
        System.out.println("start() 后: " + t.getState()); // RUNNABLE
        
        try {
            Thread.sleep(100);
            System.out.println("执行中: " + t.getState()); // TERMINATED（如果执行很快）
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 如果直接调用 run()
        Thread t2 = new Thread(() -> {
            System.out.println("任务执行中");
        });
        
        System.out.println("创建后: " + t2.getState()); // NEW
        t2.run(); // 调用 run()
        System.out.println("run() 后: " + t2.getState()); // NEW（状态未改变）
    }
}
```

---

## 🔍 深入理解

### start() 方法的工作原理

```java
// Thread 类的 start() 方法（简化版）
public synchronized void start() {
    if (threadStatus != 0)  // 检查线程状态
        throw new IllegalThreadStateException();
    
    group.add(this);  // 添加到线程组
    
    boolean started = false;
    try {
        start0();  // 调用本地方法，创建新线程
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
        }
    }
}

private native void start0();  // 本地方法，由 JVM 实现
```

**关键点：**
1. `start()` 是同步方法，保证线程安全
2. 检查线程状态，防止重复启动
3. 调用本地方法 `start0()` 创建新线程
4. JVM 会创建新线程并调用 `run()` 方法

---

### run() 方法的工作原理

```java
// Thread 类的 run() 方法
@Override
public void run() {
    if (target != null) {
        target.run();  // 如果传入了 Runnable，调用它的 run()
    }
}
```

**关键点：**
1. `run()` 是普通方法，不是本地方法
2. 如果传入了 `Runnable`，调用 `target.run()`
3. 直接调用 `run()` 不会创建新线程
4. 在当前线程中同步执行

---

## ⚠️ 常见错误

### 错误1：直接调用 run()

```java
// ❌ 错误示例
Thread t = new Thread(() -> {
    System.out.println("执行任务");
});
t.run();  // 错误：不会创建新线程

// ✅ 正确示例
Thread t = new Thread(() -> {
    System.out.println("执行任务");
});
t.start();  // 正确：创建新线程
```

---

### 错误2：多次调用 start()

```java
// ❌ 错误示例
Thread t = new Thread(() -> {
    System.out.println("执行任务");
});
t.start();
t.start();  // 错误：会抛出 IllegalThreadStateException

// ✅ 正确示例
Thread t = new Thread(() -> {
    System.out.println("执行任务");
});
t.start();  // 只能调用一次
```

---

### 错误3：在 run() 中调用 start()

```java
// ❌ 错误示例（可能导致问题）
Thread t = new Thread(() -> {
    Thread t2 = new Thread(() -> {
        System.out.println("子任务");
    });
    t2.start();  // 虽然可以，但通常不是好的设计
});
t.start();
```

---

## ✅ 最佳实践

### 1. 总是使用 start() 启动线程

```java
// ✅ 推荐
Thread t = new Thread(() -> {
    // 任务逻辑
});
t.start();
```

### 2. 不要直接调用 run()

```java
// ❌ 不推荐
Thread t = new Thread(() -> {
    // 任务逻辑
});
t.run();  // 不会创建新线程，失去了多线程的意义
```

### 3. 使用线程池管理线程

```java
// ✅ 推荐：使用线程池
ExecutorService executor = Executors.newFixedThreadPool(5);
executor.execute(() -> {
    // 任务逻辑
});
```

---

## 📝 总结

### start() 方法
- ✅ **作用**：启动新线程，异步执行
- ✅ **特点**：创建新线程，不阻塞主线程
- ✅ **使用**：启动线程时使用
- ⚠️ **限制**：只能调用一次

### run() 方法
- ❌ **作用**：普通方法调用，同步执行
- ❌ **特点**：不创建新线程，阻塞当前线程
- ❌ **使用**：不应该直接调用
- ✅ **限制**：可以多次调用（但没有意义）

### 核心要点

1. **start() 创建新线程，run() 不创建**
2. **start() 异步执行，run() 同步执行**
3. **start() 只能调用一次，run() 可以多次**
4. **总是使用 start() 启动线程**

### 记住这句话

> **start() 是启动线程，run() 是执行任务。不要直接调用 run()，应该调用 start()！**

