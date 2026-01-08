# 线程启动与 sleep 的时序控制详解

## 🎯 问题代码

```java
t1.start();
Thread.sleep(100);  // 确保 t1 先获取锁
t2.start();
Thread.sleep(200);
```

这段代码经常出现在多线程演示中，很多人不理解为什么要这样写。下面详细解释。

---

## 📊 代码分解

### 完整示例

```java
Object lock = new Object();

// 线程1：持有锁
Thread t1 = new Thread(() -> {
    synchronized (lock) {
        System.out.println("Thread-1 获取到锁，开始执行");
        try {
            Thread.sleep(3000); // 持有锁 3 秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread-1 释放锁");
    }
}, "Thread-1");

// 线程2：等待获取锁
Thread t2 = new Thread(() -> {
    System.out.println("Thread-2 尝试获取锁");
    synchronized (lock) {
        System.out.println("Thread-2 获取到锁");
    }
}, "Thread-2");

// 关键代码段
t1.start();
Thread.sleep(100);  // 确保 t1 先获取锁
t2.start();
Thread.sleep(200);  // 等待 t2 进入 BLOCKED 状态
```

---

## 🔍 逐行详解

### 第1行：`t1.start()`

```java
t1.start();
```

**作用：**
- 启动线程 `t1`
- 调用 `start()` 后，线程进入 `RUNNABLE` 状态
- **注意**：`start()` 方法会立即返回，不会等待线程执行完成

**执行流程：**
```
主线程执行 t1.start()
  ↓
t1 线程开始运行（异步执行）
  ↓
主线程继续执行下一行代码（不等待 t1 完成）
```

**关键点：**
- ✅ `start()` 是异步的，不会阻塞主线程
- ⚠️ 调用 `start()` 后，t1 线程**可能还没获取到锁**（只是开始运行）
- ⚠️ 由于线程调度的不确定性，t1 可能立即获取锁，也可能稍后才获取

---

### 第2行：`Thread.sleep(100)`

```java
Thread.sleep(100);  // 确保 t1 先获取锁
```

**作用：**
- 让**主线程**休眠 100 毫秒
- 在这 100 毫秒内，给 t1 线程**足够的时间**去获取锁

**为什么需要 sleep？**

#### 问题：线程调度的不确定性

```java
// 没有 sleep 的情况
t1.start();  // t1 开始运行，但可能还没获取到锁
t2.start();  // t2 也开始运行

// 问题：t1 和 t2 可能几乎同时尝试获取锁
// 结果：不确定哪个线程先获取到锁
```

**线程执行的不确定性：**

```
时间轴（没有 sleep）：

t1.start()           t2.start()
  |                    |
  v                    v
[主线程]            [主线程]
  |                    |
  v                    v
[t1 线程开始]      [t2 线程开始]
  |                    |
  v                    v
[尝试获取锁]        [尝试获取锁]
  |                    |
  ? ← 谁先获取？不确定！
```

#### 解决方案：使用 sleep 给时间差

```java
t1.start();
Thread.sleep(100);  // 主线程休眠，给 t1 时间

// 100 毫秒后，主线程继续执行
t2.start();
```

**执行流程：**

```
时间轴（有 sleep）：

0ms:  t1.start()
      ↓
     [主线程休眠 100ms]
     [t1 线程在运行]
     [t1 获取锁，开始执行 synchronized 代码块]
      ↓
100ms: 主线程醒来，执行 t2.start()
      ↓
     [t2 线程开始运行]
     [t2 尝试获取锁，但锁被 t1 持有]
     [t2 进入 BLOCKED 状态]
```

**关键点：**
- ✅ `sleep(100)` 给 t1 **100 毫秒的时间窗口**去获取锁
- ✅ 主线程休眠期间，t1 有机会执行并获取锁
- ✅ 100 毫秒后，t1 应该已经获取到锁了
- ⚠️ 这不是 100% 保证，但在正常情况下足够了

---

### 第3行：`t2.start()`

```java
t2.start();
```

**作用：**
- 启动线程 `t2`
- 此时 t1 应该已经获取到锁了（因为主线程已经休眠了 100ms）

**执行情况：**
```
t2 开始运行
  ↓
t2 尝试进入 synchronized (lock) 代码块
  ↓
发现锁被 t1 持有
  ↓
t2 进入 BLOCKED 状态（阻塞）
```

---

### 第4行：`Thread.sleep(200)`

```java
Thread.sleep(200);  // 等待 t2 进入 BLOCKED 状态
```

**作用：**
- 让**主线程**再休眠 200 毫秒
- 给 t2 线程**足够的时间**进入 BLOCKED 状态

**为什么需要再次 sleep？**

#### 问题：状态转换需要时间

```java
t2.start();  // t2 开始运行
System.out.println(t2.getState());  //  可能还没进入 BLOCKED 状态

// 因为：
// - t2.start() 后，t2 线程才开始启动
// - 线程启动需要时间
// - 尝试获取锁需要时间
// - 进入 BLOCKED 状态需要时间
```

**执行流程：**

```
t2.start()
  ↓
[t2 线程启动中...] ← 需要几毫秒
  ↓
[t2 尝试获取锁...] ← 需要几毫秒
  ↓
[t2 发现锁被占用] ← 需要几毫秒
  ↓
[t2 进入 BLOCKED 状态] ← 需要几毫秒

总计：可能需要 10-50 毫秒
```

**使用 sleep(200) 后：**

```java
t2.start();
Thread.sleep(200);  // 等待 t2 进入 BLOCKED 状态
System.out.println(t2.getState());  //  此时应该是 BLOCKED
```

**关键点：**
- ✅ 给 t2 **足够的时间**完成状态转换
- ✅ 确保在检查状态时，t2 已经进入 BLOCKED 状态
- ✅ 200 毫秒是一个安全的时间窗口

---

## 📋 完整时序图

### 没有 sleep 的时序（不确定）

```
主线程                    t1 线程                    t2 线程
  |                         |                         |
  |--- t1.start() --------> |                         |
  |                         | [开始运行]              |
  |--- t2.start() -------------------------->        |
  |                         |                         | [开始运行]
  |                         | [尝试获取锁]            | [尝试获取锁]
  |                         |                         |
  |                         ? ← 谁先获取？不确定！    ?
  |                         |                         |
```

### 有 sleep 的时序（确定）

```
主线程                    t1 线程                    t2 线程
  |                         |                         |
  |--- t1.start() --------> |                         |
  |                         | [开始运行]              |
  | [休眠 100ms]            | [获取锁]                |
  |                         | [执行 synchronized]     |
  |--- sleep(100) --------> |                         |
  |                         |                         |
  | [醒来]                  | [继续持有锁]            |
  |--- t2.start() -------------------------->        |
  |                         |                         | [开始运行]
  |--- sleep(200) --------> |                         | [尝试获取锁]
  |                         |                         | [发现锁被占用]
  |                         |                         | [进入 BLOCKED 状态]
  | [醒来]                  |                         | [BLOCKED]
  |--- 检查 t2.getState() ----------------->         |
  |                         |                         | [BLOCKED] 
```

---

## 💡 为什么需要这种时序控制？

### 目的：演示特定的线程状态

```java
// 目的：演示 BLOCKED 状态
// 需要：
// 1. t1 先获取锁（这样 t2 才能被阻塞）
// 2. t2 尝试获取锁但被阻塞
// 3. 在 t2 处于 BLOCKED 状态时，检查它的状态
```

**如果不使用 sleep：**

```java
t1.start();
t2.start();  // ❌ 可能 t2 先获取到锁，演示失败
System.out.println(t2.getState());  // 可能不是 BLOCKED
```

---

## 🔍 深入理解

### 1. 为什么 start() 后需要等待？

**原因：异步执行**

```java
t1.start();  // 异步启动，立即返回
// ❌ 此时 t1 可能还没开始执行
// ❌ 此时 t1 可能还没获取到锁

Thread.sleep(100);  // ✅ 给 t1 时间执行
// ✅ 此时 t1 应该已经获取到锁了
```

**类比：**
- `start()` 就像"按下一个按钮"
- 按钮按下后，机器开始工作，但需要时间
- `sleep()` 就是"等待机器开始工作"

---

### 2. sleep 时间的选择

**为什么是 100ms 和 200ms？**

```java
Thread.sleep(100);  // 确保 t1 先获取锁
Thread.sleep(200);  // 等待 t2 进入 BLOCKED 状态
```

**考虑因素：**

| 时间 | 用途 | 为什么是这个值 |
|------|------|----------------|
| 100ms | 给 t1 时间获取锁 | 线程启动通常需要 10-50ms，100ms 足够 |
| 200ms | 给 t2 时间进入 BLOCKED | 状态转换需要时间，200ms 更安全 |

**实际执行时间：**
- 线程启动：通常 < 50ms
- 获取锁：通常 < 10ms
- 状态转换：通常 < 50ms

**100ms 和 200ms 的选择：**
- ✅ 足够覆盖正常情况
- ✅ 不会太长，影响演示速度
- ⚠️ 不是 100% 保证，但在演示代码中足够

---

### 3. 有没有更好的方法？

#### 方法1：使用 CountDownLatch（更可靠）

```java
CountDownLatch latch = new CountDownLatch(1);

Thread t1 = new Thread(() -> {
    synchronized (lock) {
        latch.countDown();  // 通知主线程：t1 已获取锁
        // 持有锁...
    }
});

t1.start();
latch.await();  //  等待 t1 获取锁（更可靠）
t2.start();
Thread.sleep(200);
```

**优点：**
- ✅ 更可靠，不依赖时间
- ✅ 精确知道 t1 什么时候获取到锁

**缺点：**
- ⚠️ 代码更复杂
- ⚠️ 需要理解 CountDownLatch（后续会学）

---

#### 方法2：使用 while 循环检查（更可靠）

```java
t1.start();

// 等待 t1 获取锁
while (!t1AcquiredLock) {
    Thread.sleep(10);
}

t2.start();

// 等待 t2 进入 BLOCKED 状态
while (t2.getState() != Thread.State.BLOCKED) {
    Thread.sleep(10);
}
```

**优点：**
-  精确等待状态
-  不依赖固定时间

**缺点：**
-  代码更复杂
-  需要额外的状态标志

---

#### 方法3：使用 sleep（简单但不够可靠）

```java
t1.start();
Thread.sleep(100);  // 简单，但在极端情况下可能不够
t2.start();
Thread.sleep(200);
```

**优点：**
-  代码简单
-  容易理解

**缺点：**
-  不够可靠（依赖时间）
-  在某些情况下可能失败

**结论：**
- 在**演示代码**中，使用 `sleep` 是**可以接受的**
- 在**生产代码**中，应该使用更可靠的方法（如 CountDownLatch）

---

##  实际应用场景

### 场景1：演示线程状态

```java
// 目的：演示 BLOCKED 状态
t1.start();
Thread.sleep(100);  // 确保 t1 先获取锁
t2.start();
Thread.sleep(200);  // 确保 t2 进入 BLOCKED 状态
System.out.println(t2.getState());  // 输出：BLOCKED
```

---

### 场景2：测试并发行为

```java
// 目的：测试两个线程的竞争
t1.start();
Thread.sleep(100);  // 让 t1 先开始
t2.start();
Thread.sleep(200);  // 等待两个线程都开始
// 然后观察它们的行为
```

---

### 场景3：模拟实际场景

```java
// 模拟：用户 A 先登录系统
userThread1.start();  // 用户 A
Thread.sleep(100);    // 用户 A 先登录
userThread2.start();  // 用户 B
Thread.sleep(200);    // 等待两个用户都登录
// 然后测试系统行为
```

---

## 📝 关键要点总结

### 1. start() 是异步的

```java
t1.start();  // 立即返回，不等待线程执行
// t1 可能还没开始执行
```

---

### 2. sleep() 的作用

```java
Thread.sleep(100);  // 让当前线程（主线程）休眠
// 在这期间，其他线程可以执行
```

---

### 3. 为什么要 sleep？

```java
// 目的：给其他线程执行时间
t1.start();
Thread.sleep(100);  // 给 t1 时间获取锁
t2.start();
Thread.sleep(200);  // 给 t2 时间进入 BLOCKED 状态
```

---

### 4. 时间的选择

- **100ms**：给线程启动和获取锁的时间
- **200ms**：给状态转换的时间
- **不是绝对可靠**，但在演示代码中足够

---

### 5. 更可靠的方法

- 使用 `CountDownLatch`（后续会学）
- 使用 `while` 循环检查状态
- 使用 `sleep`（简单但不够可靠）

---

##  学习检验

完成学习后，你应该能够：

-  理解 `start()` 是异步的
-  理解 `sleep()` 的作用和为什么需要它
-  理解为什么要等待线程状态转换
-  知道如何选择 sleep 的时间
-  了解更可靠的替代方法

---

##  常见问题

### Q1: 为什么 sleep(100) 就一定能保证 t1 先获取锁？

**A:** 不能 100% 保证，但在正常情况下足够。100ms 给线程启动和获取锁提供了充足的时间窗口。如果需要在生产代码中保证，应该使用 CountDownLatch 等同步工具。

---

### Q2: 如果 sleep(10) 行不行？

**A:** 可能行，但不够安全。线程启动和状态转换可能需要几十毫秒，10ms 可能不够。100ms 是一个更安全的选择。

---

### Q3: 能不能不用 sleep？

**A:** 可以，但需要其他同步机制（如 CountDownLatch、while 循环检查）。在演示代码中，sleep 是最简单的方法。

---

### Q4: sleep 会阻塞主线程吗？

**A:** 是的，`Thread.sleep()` 会阻塞**调用它的线程**（这里是主线程）。在 sleep 期间，主线程不会执行，但其他线程（如 t1、t2）会继续执行。

