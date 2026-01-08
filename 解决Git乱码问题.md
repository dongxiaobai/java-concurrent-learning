# 解决 Git 提交信息乱码问题

## ✅ 已完成的修复

### 1. 配置 Git 使用 UTF-8 编码

```bash
git config --global core.quotepath false
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```

### 2. 修改了最新的乱码提交

将乱码的提交信息改为英文：
- 原：`閲嶆瀯鏂囨。缁撴瀯锛氬皢鏂囨。绉诲姩鍒皐eek鐩綍涓嬬殑insights鏂囦欢澶癸紝鏇存柊README`
- 新：`refactor: reorganize project structure, move insights to week1 directory`

---

## 📝 未来提交建议

### 推荐：使用英文提交信息

**优点：**
- ✅ 避免编码问题
- ✅ 国际通用
- ✅ GitHub 显示正常

**格式：**
```bash
git commit -m "type: description"

# 示例：
git commit -m "feat: add new feature"
git commit -m "fix: fix bug"
git commit -m "refactor: reorganize code"
git commit -m "docs: update documentation"
```

### 如果必须使用中文

确保：
1. ✅ Git 编码配置正确（已完成）
2. ✅ 终端编码为 UTF-8
3. ✅ 环境变量设置正确

---

## 🔧 永久配置（添加到 ~/.zshrc）

```bash
# 添加编码设置
echo 'export LANG=en_US.UTF-8' >> ~/.zshrc
echo 'export LC_ALL=en_US.UTF-8' >> ~/.zshrc
source ~/.zshrc
```

---

## 🎯 提交信息规范（Conventional Commits）

推荐使用规范的提交信息格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）：**
- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

**示例：**
```bash
git commit -m "feat(week1): add thread lifecycle demo"
git commit -m "fix: resolve encoding issue in commit messages"
git commit -m "docs: update README with new structure"
```

