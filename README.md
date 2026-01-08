# Java 并发编程学习项目

## 项目简介

这是一个专门用于学习 Java 并发编程的独立项目。包含从基础到高级的完整学习路径和实践代码。

## 项目结构

```
java-concurrent-learning/
├── pom.xml                          # Maven 配置文件
├── README.md                        # 项目说明
├── docs/                            # 工具/排错文档
│   └── guides/                      # Cursor/GitHub/README 等使用指南
└── src/
    ├── main/
    │   └── java/
    │       └── com/
            │           └── concurrent/
            │               ├── week1/       # Week 1: 并发基础
            │               │   ├── day1/        # Day 1: 线程创建方式
            │               │   ├── day2/        # Day 2: 线程生命周期
            │               │   ├── day3-04/     # Day 3-4: 线程安全问题
            │               │   └── day5-07/     # Day 5-7: synchronized 关键字
    │               ├── week2/       # Week 2: 锁机制深入
    │               ├── week3/       # Week 3: 并发工具类（上）
    │               ├── week4/       # Week 4: 并发工具类（下）
    │               ├── week5/       # Week 5: 并发集合
    │               ├── week6/       # Week 6: 线程池
    │               ├── week7/       # Week 7: 原子类
    │               └── week8/       # Week 8: CAS 原理与实战
    └── test/
        └── java/                    # 测试代码
```

**说明：**
- 每个 `week` 目录下包含该周的示例代码和 `insights` 文件夹
- `insights` 文件夹包含该周的深度思考、原理分析和学习笔记
- 代码和文档组织在一起，便于学习和查阅

## 学习计划

### Week 1: 并发基础
- Day 1: 线程创建方式
  - 代码：`week1/day1/ThreadCreationDemo.java`、`week1/day1/RunnableSeparationDemo.java`
  - 文档：`week1/day1/insights/` 目录下的相关文档
- Day 2: 线程生命周期（`week1/day2/ThreadLifecycleDemo.java`）
- Day 3-4: 线程安全问题（`week1/day3-04/ThreadSafetyDemo.java`）
- Day 5-7: synchronized 关键字（`week1/day5-07/SynchronizedDemo.java`）

### Week 2: 锁机制深入
- Day 1-3: ReentrantLock
- Day 4-5: ReadWriteLock
- Day 6-7: AQS 原理

### Week 3-4: 并发工具类
- CountDownLatch、CyclicBarrier、Semaphore
- Exchanger、CompletableFuture

### Week 5-6: 并发集合与线程池
- ConcurrentHashMap、CopyOnWriteArrayList、BlockingQueue
- ThreadPoolExecutor、Executors、ForkJoinPool

### Week 7-8: 原子类与实战
- AtomicInteger、AtomicReference、LongAdder
- CAS 原理、实战项目

## 快速开始

### 环境要求
- JDK 8 或更高版本
- Maven 3.6+（可选，用于构建）

### 编译项目
```bash
mvn compile
```

### 运行示例代码

#### 方式1：使用 javac + java
```bash
cd src/main/java
javac com/concurrent/week1/ThreadCreationDemo.java
java com.concurrent.week1.ThreadCreationDemo
```

#### 方式2：使用 Maven
```bash
mvn compile exec:java -Dexec.mainClass="com.concurrent.week1.ThreadCreationDemo"
```

#### 方式3：使用 IDE
- 使用 IntelliJ IDEA 或 Eclipse 打开项目
- 直接运行 main 方法

## 学习资源

### 推荐书籍
1. 《Java 并发编程实战》（Brian Goetz）- 必读
2. 《Java 并发编程的艺术》（方腾飞）- 深入理解
3. 《实战 Java 高并发程序设计》（葛一鸣）- 实战

### 在线资源
- 极客时间《Java 并发编程实战》
- Java 官方文档：java.util.concurrent 包

## 学习建议

### 每天学习时间
- **上班空闲**：1-2 小时
- **周末**：4-6 小时

### 学习方法
1. **先运行代码**：看效果
2. **阅读代码**：理解原理
3. **阅读 insights**：深入理解设计思想和原理
4. **修改代码**：实践练习
5. **总结笔记**：记录要点

## 学习检验

完成学习后，你应该能够：
- ✅ 理解线程安全的概念
- ✅ 熟练使用各种并发工具类
- ✅ 解决实际并发问题
- ✅ 阅读并发相关源码
- ✅ 进行并发性能优化

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
