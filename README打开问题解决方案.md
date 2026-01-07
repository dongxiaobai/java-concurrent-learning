# README.md 无法打开的解决方案

## 🔍 问题诊断

文件状态正常：
- ✅ 文件存在
- ✅ 编码正确（UTF-8）
- ✅ 内容完整（123 行）
- ✅ 格式正确

问题可能在 Cursor 的设置或缓存。

---

## 🔧 解决方案

### 方案1：使用命令面板打开（最简单）

1. 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
2. 输入：`File: Open File`
3. 输入：`README`
4. 选择 `README.md`

---

### 方案2：检查并临时禁用 Markdown 预览设置

如果设置了 Markdown 默认预览，可能导致问题：

1. 打开设置：`Cmd + ,` (macOS) 或 `Ctrl + ,` (Windows/Linux)
2. 搜索：`workbench.editorAssociations`
3. 临时注释掉或删除：
   ```json
   // "workbench.editorAssociations": {
   //   "*.md": "vscode.markdown.preview.editor"
   // }
   ```
4. 保存设置
5. 尝试打开 README.md

---

### 方案3：使用文本编辑器模式打开

1. 右键点击 `README.md`
2. 选择 **"Open With..."** → **"Text Editor"**
3. 或者使用命令面板：
   - `Cmd + Shift + P` → `Reopen Editor With...` → `Text Editor`

---

### 方案4：清除 Cursor 缓存

```bash
# macOS
rm -rf ~/Library/Application\ Support/Cursor/Cache

# Windows
# 删除：%APPDATA%\Cursor\Cache

# Linux
rm -rf ~/.config/Cursor/Cache
```

然后重启 Cursor。

---

### 方案5：检查文件权限

```bash
# 检查文件权限
ls -la README.md

# 如果需要，修改权限
chmod 644 README.md
```

---

### 方案6：重新创建 README.md

如果以上都不行，可以备份后重新创建：

```bash
# 备份
cp README.md README.md.backup

# 重新创建（内容不变）
cat README.md.backup > README.md
```

---

### 方案7：使用绝对路径打开

```bash
# 获取绝对路径
realpath README.md

# 在 Cursor 中使用 Cmd + O (macOS) 或 Ctrl + O (Windows/Linux)
# 粘贴绝对路径
```

---

## 🎯 快速尝试顺序

1. ✅ **命令面板打开** (`Cmd + Shift + P` → `File: Open File`)
2. ✅ **检查 Markdown 预览设置**
3. ✅ **使用文本编辑器模式**
4. ✅ **清除缓存并重启**
5. ✅ **检查文件权限**

---

## 💡 如果还是不行

请告诉我：
1. **具体的错误信息**是什么？
2. **点击文件时**有什么反应？（没反应 / 显示错误 / 其他）
3. **其他 .md 文件**能正常打开吗？

这样我可以提供更针对性的帮助。

