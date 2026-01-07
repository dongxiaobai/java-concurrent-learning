# README.md 打开错误修复方案

## ❌ 错误信息

```
无法打开"README.md"
Assertion Failed: Argument is `undefined` or `null`.
```

这是 Cursor 内部的断言失败错误，可能是 Cursor 的 bug。

---

## 🔧 解决方案

### 方案1：使用命令面板打开（推荐）

1. 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
2. 输入：`File: Open File`
3. 输入：`README`
4. 选择文件

**或者：**

1. 按 `Cmd + Shift + P`
2. 输入：`File: Reopen Editor`
3. 选择 README.md

---

### 方案2：使用绝对路径

1. 获取文件绝对路径：
   ```bash
   realpath README.md
   # 输出：/Users/dongxiaobai/java-concurrent-learning/README.md
   ```

2. 在 Cursor 中：
   - 按 `Cmd + O` (macOS) 或 `Ctrl + O` (Windows/Linux)
   - 粘贴绝对路径：`/Users/dongxiaobai/java-concurrent-learning/README.md`
   - 回车

---

### 方案3：在 Finder 中打开

1. 在文件浏览器中右键点击 `README.md`
2. 选择 **"Reveal in Finder"** (macOS) 或 **"Reveal in File Explorer"** (Windows)
3. 双击文件，用系统默认应用打开
4. 如果能打开，说明文件没问题，是 Cursor 的问题

---

### 方案4：清除 Cursor 工作区缓存

```bash
# macOS
rm -rf ~/Library/Application\ Support/Cursor/User/workspaceStorage

# Windows
# 删除：%APPDATA%\Cursor\User\workspaceStorage

# Linux
rm -rf ~/.config/Cursor/User/workspaceStorage
```

然后重启 Cursor。

---

### 方案5：重新加载窗口

1. 按 `Cmd + Shift + P`
2. 输入：`Developer: Reload Window`
3. 回车

---

### 方案6：检查文件扩展名关联

1. 打开设置：`Cmd + ,`
2. 搜索：`files.associations`
3. 检查是否有冲突的设置
4. 临时移除 Markdown 相关的关联设置

---

### 方案7：使用命令行打开

```bash
# macOS
open -a "Cursor" README.md

# 或者直接用系统默认编辑器
open README.md
```

---

### 方案8：重新创建文件（最后手段）

如果以上都不行，可以重新创建：

```bash
# 备份原文件
cp README.md README.md.backup

# 重新创建（内容不变）
cat README.md.backup > README_new.md
mv README_new.md README.md
```

---

## 🎯 推荐操作顺序

1. ✅ **命令面板打开** (`Cmd + Shift + P` → `File: Open File`)
2. ✅ **重新加载窗口** (`Cmd + Shift + P` → `Developer: Reload Window`)
3. ✅ **清除工作区缓存**（然后重启 Cursor）
4. ✅ **使用绝对路径打开**
5. ✅ **在 Finder 中打开验证文件**

---

## 💡 临时解决方案

如果急需查看 README 内容：

```bash
# 在终端查看
cat README.md

# 或用 less 查看（可以上下滚动）
less README.md
# 按 q 退出
```

---

## 🔍 诊断信息

文件状态：
- ✅ 文件存在：`/Users/dongxiaobai/java-concurrent-learning/README.md`
- ✅ 权限正常：`644`
- ✅ 编码正确：`UTF-8`
- ✅ 内容完整：`123 行`

**结论：文件本身没问题，是 Cursor 的内部错误。**

---

## 📝 如果还是不行

1. **更新 Cursor** 到最新版本
2. **重启电脑**
3. **报告 bug** 给 Cursor 团队

---

## ✅ 快速命令

```bash
# 查看文件
cat README.md

# 用系统默认应用打开
open README.md  # macOS

# 获取绝对路径
realpath README.md
```

