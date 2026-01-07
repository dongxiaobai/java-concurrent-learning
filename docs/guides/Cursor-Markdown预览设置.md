# Cursor Markdown 预览设置

## 🎯 目标

让 Cursor 双击 `.md` 文件时直接显示预览，而不是源代码。

---

## 🔧 方法1：修改设置（推荐）

### 步骤1：打开设置

1. 按 `Cmd + ,` (macOS) 或 `Ctrl + ,` (Windows/Linux) 打开设置
2. 或者：`File` → `Preferences` → `Settings`

### 步骤2：搜索设置

在设置搜索框中输入：`workbench.editorAssociations`

### 步骤3：添加配置

点击 **"Edit in settings.json"**，添加以下配置：

```json
{
  "workbench.editorAssociations": {
    "*.md": "vscode.markdown.preview.editor"
  }
}
```

**效果：** 所有 `.md` 文件都会默认以预览模式打开。

---

## 🔧 方法2：使用快捷键

### 快速预览快捷键

- **macOS**: `Cmd + Shift + V`
- **Windows/Linux**: `Ctrl + Shift + V`

### 并排预览

- **macOS**: `Cmd + K V`
- **Windows/Linux**: `Ctrl + K V`

**说明：** 先按 `Cmd + K`，松开后再按 `V`

---

## 🔧 方法3：右键菜单

1. 右键点击 `.md` 文件
2. 选择 **"Open Preview"** 或 **"Open Preview to the Side"**

---

## 🔧 方法4：安装扩展（可选）

### Markdown Preview Enhanced

1. 打开扩展面板：`Cmd + Shift + X` (macOS) 或 `Ctrl + Shift + X` (Windows/Linux)
2. 搜索：`Markdown Preview Enhanced`
3. 安装后，右键 `.md` 文件可以选择预览方式

---

## ⚙️ 详细配置选项

### 完整设置示例

在 `settings.json` 中添加：

```json
{
  // Markdown 文件默认以预览模式打开
  "workbench.editorAssociations": {
    "*.md": "vscode.markdown.preview.editor"
  },
  
  // Markdown 预览设置
  "markdown.preview.breaks": true,                    // 换行符显示为换行
  "markdown.preview.linkify": true,                   // 自动识别链接
  "markdown.preview.typographer": true,              // 启用排版功能
  "markdown.preview.fontSize": 14,                    // 预览字体大小
  "markdown.preview.lineHeight": 1.6,                 // 预览行高
  "markdown.preview.scrollPreviewWithEditor": true,   // 预览与编辑器同步滚动
  "markdown.preview.scrollEditorWithPreview": true    // 编辑器与预览同步滚动
}
```

---

## 🎨 自定义预览样式

### 修改预览主题

```json
{
  "markdown.preview.breaks": true,
  "markdown.preview.fontSize": 16,
  "markdown.preview.lineHeight": 1.8
}
```

### 使用自定义 CSS（高级）

1. 创建文件：`.vscode/markdown.css`
2. 添加自定义样式
3. 在设置中引用：

```json
{
  "markdown.styles": [".vscode/markdown.css"]
}
```

---

## 📝 常用快捷键

| 功能 | macOS | Windows/Linux |
|------|-------|---------------|
| **打开预览** | `Cmd + Shift + V` | `Ctrl + Shift + V` |
| **并排预览** | `Cmd + K V` | `Ctrl + K V` |
| **切换预览/源码** | `Cmd + Shift + V` | `Ctrl + Shift + V` |
| **刷新预览** | `Cmd + Shift + R` | `Ctrl + Shift + R` |

---

## 🔄 切换预览和源码视图

### 方法1：快捷键

- **macOS**: `Cmd + Shift + V`
- **Windows/Linux**: `Ctrl + Shift + V`

### 方法2：命令面板

1. 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
2. 输入：`Markdown: Open Preview`
3. 选择相应命令

---

## 💡 推荐配置

### 最佳实践设置

```json
{
  // 1. Markdown 文件默认预览
  "workbench.editorAssociations": {
    "*.md": "vscode.markdown.preview.editor"
  },
  
  // 2. 预览设置
  "markdown.preview.breaks": true,
  "markdown.preview.fontSize": 14,
  "markdown.preview.lineHeight": 1.6,
  
  // 3. 同步滚动
  "markdown.preview.scrollPreviewWithEditor": true,
  "markdown.preview.scrollEditorWithPreview": true
}
```

---

## 🎯 快速设置步骤

### 最简单的方法：

1. **打开设置文件**
   - 按 `Cmd + Shift + P` (macOS) 或 `Ctrl + Shift + P` (Windows/Linux)
   - 输入：`Preferences: Open User Settings (JSON)`
   - 回车

2. **添加配置**
   ```json
   {
     "workbench.editorAssociations": {
       "*.md": "vscode.markdown.preview.editor"
     }
   }
   ```

3. **保存**
   - `Cmd + S` (macOS) 或 `Ctrl + S` (Windows/Linux)

4. **测试**
   - 双击任意 `.md` 文件
   - 应该直接显示预览

---

## ⚠️ 注意事项

### 如果需要编辑源码

如果设置了默认预览，但需要编辑源码：

1. **方法1：右键菜单**
   - 右键文件 → **"Open With..."** → **"Text Editor"**

2. **方法2：快捷键**
   - 在预览模式下按 `Cmd + Shift + V` (macOS) 或 `Ctrl + Shift + V` (Windows/Linux) 切换

3. **方法3：命令面板**
   - `Cmd + Shift + P` → 输入 `Reopen Editor With...` → 选择 `Text Editor`

---

## 🔍 验证设置

设置完成后：

1. 关闭所有打开的 `.md` 文件
2. 双击任意 `.md` 文件
3. 应该直接显示预览，而不是源代码

---

## 📚 相关设置说明

### workbench.editorAssociations

这个设置可以让你为特定文件类型指定默认编辑器：

```json
{
  "workbench.editorAssociations": {
    "*.md": "vscode.markdown.preview.editor",      // Markdown 预览
    "*.png": "imagePreview.previewEditor",          // 图片预览
    "*.pdf": "pdf.preview.editor"                   // PDF 预览
  }
}
```

---

## ✅ 完成！

设置完成后，所有 `.md` 文件都会默认以预览模式打开，阅读体验更好！

如果需要编辑，使用快捷键 `Cmd + Shift + V` (macOS) 或 `Ctrl + Shift + V` (Windows/Linux) 切换到源码视图。

