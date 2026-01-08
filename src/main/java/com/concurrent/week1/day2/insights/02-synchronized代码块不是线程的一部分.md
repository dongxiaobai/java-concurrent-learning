# synchronized 代码块不是线程的一部分

## ❌ 常见误解

**误解：** synchronized 代码块是放在 Thread 代码块里面，属于线程的一部分。

**正确理解：** synchronized 代码块**不是线程的一部分**，而是作用在**共享资源（对象）**上的同步机制。

---

## 💡 核心概念

### 1. synchronized 作用在对象上，不是线程上

```java
//  错误理解：synchronized 是线程的一部分
Thread t = new Thread(() -> {
    synchronized (...) {  // 不是这样的！
        // 代码
    }
});

//  正确理解：synchronized 是保护共享资源的
public class Counter {
    private int count = 0;  // 共享资源
    private final Object lock = new Object();  // 锁对象
    
    public void increment() {  // 普通方法，不是线程方法
        synchronized (lock) {  // 保护共享资源 count
            count++;
        }
    }
}
```

**关键点：**
- `synchronized` 作用在**对象（锁对象）**上
- 多个线程访问同一个对象时，`synchronized` 确保同步
- `synchronized` 代码块可以放在**任何方法**中，不限于线程的 `run()` 方法

---

## 📊 详细对比

### 方式1：在普通方法中使用 synchronized（推荐）

```java
// 共享资源类
public class SafeCounter {
    private int count = 0;  // 共享变量
    private final Object lock = new Object();  // 锁对象
    
    // synchronized 代码块在普通方法中
    public void increment() {
        synchronized (lock) {  // 保护共享资源
            count++;
        }
    }
    
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}

// 使用：多个线程调用同一个对象的方法
SafeCounter counter = new SafeCounter();  // 共享对象

Thread t1 = new Thread(() -> {
    counter.increment();  // 线程1调用方法
});

Thread t2 = new Thread(() -> {
    counter.increment();  // 线程2调用方法
});

t1.start();
t2.start();
```

**要点：**
- ✅ `synchronized` 代码块在**普通方法**中
- ✅ 多个线程调用**同一个对象**的方法
- ✅ `synchronized` 保护的是**共享对象**（`counter`）的资源（`count`）

---

### 方式2：在 Thread 的 run() 方法中使用 synchronized

```java
public class Counter {
    private static int count = 0;  // 静态共享变量
    private static final Object lock = new Object();  // 静态锁对象
    
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            // synchronized 在 run() 方法中
            synchronized (lock) {  // 保护共享资源
                count++;
                System.out.println("Thread-1: " + count);
            }
        });
        
        Thread t2 = new Thread(() -> {
            // synchronized 在 run() 方法中
            synchronized (lock) {  // 保护共享资源
                count++;
                System.out.println("Thread-2: " + count);
            }
        });
        
        t1.start();
        t2.start();
    }
}
```

**要点：**
- ⚠️ 虽然 `synchronized` 在 `run()` 方法中，但它**仍然不是线程的一部分**
- ✅ `synchronized` 保护的是**共享资源**（`count`），不是线程本身
- ✅ 多个线程都访问同一个锁对象（`lock`），实现同步

---

## 🔍 关键理解

### synchronized 代码块的本质

```
synchronized (锁对象) {
    // 代码
}
```

**核心要点：**
1. **锁的是对象**：`synchronized (锁对象)` - 锁的是括号里的对象
2. **不是锁线程**：`synchronized` 不会锁住线程本身
3. **保护共享资源**：确保多个线程访问共享资源时同步
4. **可以放在任何方法中**：不一定在 `run()` 方法里

---

## 📋 示例对比

### 示例1：普通方法中使用（推荐）

```java
public class BankAccount {
    private double balance = 0;  // 共享资源：账户余额
    private final Object lock = new Object();
    
    // synchronized 在普通方法中
    public void deposit(double amount) {
        synchronized (lock) {  // 保护 balance
            balance += amount;
        }
    }
    
    public void withdraw(double amount) {
        synchronized (lock) {  // 保护 balance
            if (balance >= amount) {
                balance -= amount;
            }
        }
    }
}

// 使用
BankAccount account = new BankAccount();  // 共享账户

Thread t1 = new Thread(() -> {
    account.deposit(100);  // 线程1存钱
});

Thread t2 = new Thread(() -> {
    account.withdraw(50);  // 线程2取钱
});
```

**说明：**
- ✅ `synchronized` 在普通方法 `deposit()` 和 `withdraw()` 中
- ✅ 不是线程的一部分，而是**账户对象**的同步机制
- ✅ 保护的是**账户余额**（`balance`）这个共享资源

---

### 示例2：Thread 的 run() 方法中使用

```java
public class CounterExample {
    private static int count = 0;
    private static final Object lock = new Object();
    
    public static void main(String[] args) {
        // 线程1
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {  // 在 run() 方法中
                    count++;  // 保护共享资源
                }
            }
        });
        
        // 线程2
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (lock) {  // 在 run() 方法中
                    count++;  // 保护共享资源
                }
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Count: " + count);  // 2000
    }
}
```

**说明：**
- ⚠️ `synchronized` 虽然在 `run()` 方法中，但**仍然不是线程的一部分**
- ✅ 两个线程都访问**同一个锁对象**（`lock`）
- ✅ `synchronized` 保护的是**共享资源**（`count`），不是线程本身
- ⚠️ 如果两个线程使用不同的锁对象，同步就会失效

---

## 🎯 核心要点总结

### 1. synchronized 不是线程的一部分

```java
//  错误理解
Thread t = new Thread(() -> {
    synchronized (...) {  // 认为这是线程的一部分
        // 代码
    }
});

//  正确理解
public class Resource {
    private int data = 0;  // 共享资源
    private final Object lock = new Object();  // 锁对象
    
    public void update() {  // 普通方法
        synchronized (lock) {  // 保护共享资源 data
            data++;  // 不是保护线程，是保护数据
        }
    }
}
```

---

### 2. synchronized 作用在对象（锁）上

```java
Object lock1 = new Object();
Object lock2 = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lock1) {  // 锁的是 lock1 对象
        // 代码
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lock1) {  // 也锁 lock1，会等待 t1
        // 代码
    }
});

Thread t3 = new Thread(() -> {
    synchronized (lock2) {  // 锁的是 lock2，不受影响
        // 代码
    }
});
```

**要点：**
- 多个线程使用**同一个锁对象**时，会互相阻塞
- 多个线程使用**不同的锁对象**时，不会互相影响
- `synchronized` 锁定的是**锁对象**，不是线程

---

### 3. synchronized 可以放在任何方法中

```java
public class Example {
    private int count = 0;
    private final Object lock = new Object();
    
    //  可以放在普通方法中
    public void method1() {
        synchronized (lock) {
            count++;
        }
    }
    
    //  可以放在静态方法中
    public static void method2() {
        synchronized (Example.class) {
            // 静态同步
        }
    }
    
    //  也可以放在 Thread 的 run() 方法中
    public void method3() {
        Thread t = new Thread(() -> {
            synchronized (lock) {
                count++;
            }
        });
        t.start();
    }
}
```

**要点：**
- ✅ `synchronized` 可以放在**任何方法**中
- ✅ 不一定要在 `Thread` 的 `run()` 方法中
- 💡 **推荐**放在普通方法中，封装在资源类里

---

## 💡 形象比喻

### 银行账户的例子

```java
// 银行账户 = 共享资源
public class BankAccount {
    private double balance = 1000;  // 共享资源：余额
    private final Object lock = new Object();  // 锁对象：相当于银行的门禁
    
    // synchronized = 门禁系统
    public void withdraw(double amount) {
        synchronized (lock) {  // 进入门禁（获取锁）
            if (balance >= amount) {
                balance -= amount;  // 操作共享资源（取钱）
            }
        }  // 离开门禁（释放锁）
    }
}

// 多个客户（线程）来取钱
Thread customer1 = new Thread(() -> {
    account.withdraw(100);  // 客户1通过门禁系统取钱
});

Thread customer2 = new Thread(() -> {
    account.withdraw(100);  // 客户2也通过门禁系统取钱
});
```

**比喻：**
- **银行账户** = 共享资源（`balance`）
- **门禁系统** = `synchronized` 代码块
- **锁对象** = 门禁的锁
- **多个客户** = 多个线程

**要点：**
- 门禁系统（`synchronized`）不是客户（线程）的一部分
- 门禁系统保护的是银行账户（共享资源）
- 多个客户都要通过同一个门禁系统（同一个锁对象）

---

## 📝 总结

### synchronized 代码块的本质

| 项目 | 说明 |
|------|------|
| **作用对象** | 锁对象（括号里的对象），不是线程 |
| **保护目标** | 共享资源（变量、对象状态），不是线程 |
| **放置位置** | 可以放在任何方法中，不限于 `run()` |
| **使用场景** | 多个线程访问同一个共享资源时 |

### 记忆要点

> **synchronized 代码块 = 锁对象 + 共享资源**
> 
> - **锁对象**：多个线程都要获取的对象
> - **共享资源**：需要被保护的数据
> - **线程**：只是调用者，不是被保护的对象

### 正确理解

```java
//  正确理解
public class Resource {
    private int data = 0;  // 共享资源
    private final Object lock = new Object();  // 锁对象
    
    public void update() {
        synchronized (lock) {  // 保护 data，不是保护线程
            data++;  // 多个线程访问时，保证 data 的线程安全
        }
    }
}

// 多个线程调用
Resource resource = new Resource();  // 共享资源对象

Thread t1 = new Thread(() -> resource.update());
Thread t2 = new Thread(() -> resource.update());
```

**关键：**
- `synchronized` **不属于线程**
- `synchronized` **属于资源对象**
- `synchronized` **保护共享资源**

---

## ✅ 学习检验

完成学习后，你应该能够：

- ✅ 理解 `synchronized` 不是线程的一部分
- ✅ 理解 `synchronized` 作用在锁对象上
- ✅ 理解 `synchronized` 保护的是共享资源
- ✅ 知道 `synchronized` 可以放在任何方法中
- ✅ 能够区分线程和共享资源的关系

