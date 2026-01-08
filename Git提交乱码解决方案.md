# Git 提交信息乱码解决方案

## ❌ 问题

GitHub 上的 commit 信息显示为乱码，例如：
```
閲嶆瀯鏂囨。缁撴瀯锛氬皢鏂囨。绉诲姩鍒皐eek鐩綍涓嬬殑insights鏂囦欢澶癸紝鏇存柊README
```

## 🔍 原因

这是 Git 编码配置问题，可能的原因：
1. Git 没有正确设置 UTF-8 编码
2. 终端编码和 Git 编码不一致
3. GitHub 显示时的编码问题

## 🔧 解决方案

### 方案1：配置 Git 使用 UTF-8 编码（推荐）

```bash
# 设置 Git 使用 UTF-8 编码
git config --global core.quotepath false          # 不对路径进行编码转换
git config --global i18n.commitencoding utf-8     # 提交信息使用 UTF-8
git config --global i18n.logoutputencoding utf-8  # 日志输出使用 UTF-8

# macOS 额外设置
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
```

### 方案2：在 ~/.zshrc 或 ~/.bashrc 中添加

```bash
# 添加编码设置
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# 设置 Git 编码
git config --global core.quotepath false
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```

### 方案3：修改已提交的 commit 信息（如果刚提交）

如果最新的提交信息是乱码，可以修改：

```bash
# 修改最后一次提交信息
git commit --amend -m "正确的提交信息"

# 强制推送（谨慎使用）
git push --force origin main
```

⚠️ **注意：** 如果已经推送到远程，强制推送可能会影响其他人。

## ✅ 验证配置

```bash
# 查看 Git 编码配置
git config --list | grep -E "(quotepath|encoding)"

# 应该看到：
# core.quotepath=false
# i18n.commitencoding=utf-8
# i18n.logoutputencoding=utf-8
```

## 📝 最佳实践

### 1. 提交时使用英文（推荐）

```bash
# ✅ 推荐：使用英文
git commit -m "refactor: reorganize project structure"

# ❌ 不推荐：使用中文（可能在某些环境下显示乱码）
git commit -m "重构：重新组织项目结构"
```

### 2. 如果必须使用中文

确保：
- Git 编码配置正确
- 终端编码为 UTF-8
- 环境变量设置正确

### 3. 混合使用

```bash
# 提交信息用英文，详细的变更说明可以写在 PR 描述中
git commit -m "refactor: reorganize project structure"
```

## 🔍 诊断命令

```bash
# 查看当前编码设置
echo $LANG
echo $LC_ALL

# 查看 Git 配置
git config --list

# 测试提交信息显示
git log --oneline -5
```

## 💡 快速修复

**立即执行：**

```bash
# 1. 配置 Git 编码
git config --global core.quotepath false
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8

# 2. 设置环境变量（当前会话）
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8

# 3. 验证
git log --oneline -3
```

**永久设置（添加到 ~/.zshrc）：**

```bash
# 添加到 ~/.zshrc
echo 'export LANG=en_US.UTF-8' >> ~/.zshrc
echo 'export LC_ALL=en_US.UTF-8' >> ~/.zshrc
source ~/.zshrc
```

## 📚 参考

- Git 官方文档：字符编码问题
- GitHub：建议使用英文提交信息，避免编码问题

