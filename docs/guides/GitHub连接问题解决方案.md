# GitHub 连接问题解决方案

## ❌ 错误信息

```
Failed to connect to github.com port 443 after 75037 ms: Couldn't connect to server
```

这个错误通常是因为网络无法连接到 GitHub（443 端口是 HTTPS 端口）。

---

## 🔧 解决方案

### 方案1：使用 SSH 代替 HTTPS（推荐）

SSH 连接通常比 HTTPS 更稳定。

#### 步骤1：检查是否已有 SSH 密钥

```bash
ls -al ~/.ssh
```

如果看到 `id_rsa` 或 `id_ed25519`，说明已有密钥。

#### 步骤2：生成 SSH 密钥（如果没有）

```bash
# 生成 SSH 密钥（替换为你的邮箱）
ssh-keygen -t ed25519 -C "your_email@example.com"

# 按 Enter 使用默认路径
# 设置密码（可选，直接 Enter 跳过）
```

#### 步骤3：复制公钥

```bash
# macOS/Linux
cat ~/.ssh/id_ed25519.pub
# 或
cat ~/.ssh/id_rsa.pub

# 复制输出的内容
```

#### 步骤4：添加到 GitHub

1. 登录 GitHub
2. 点击右上角头像 → **Settings**
3. 左侧菜单 → **SSH and GPG keys**
4. 点击 **New SSH key**
5. Title: 填写名称（如 "My Mac"）
6. Key: 粘贴刚才复制的公钥
7. 点击 **Add SSH key**

#### 步骤5：测试 SSH 连接

```bash
ssh -T git@github.com
```

如果看到：
```
Hi YOUR_USERNAME! You've successfully authenticated...
```
说明 SSH 配置成功！

#### 步骤6：使用 SSH URL 添加远程仓库

```bash
# 删除之前的 HTTPS 远程仓库（如果已添加）
git remote remove origin

# 使用 SSH URL 添加（替换 YOUR_USERNAME）
git remote add origin git@github.com:YOUR_USERNAME/java-concurrent-learning.git

# 验证
git remote -v

# 推送
git push -u origin main
```

---

### 方案2：配置 Git 代理（如果有代理）

如果你有代理服务器（VPN、Clash、V2Ray 等），可以配置 Git 使用代理。

#### HTTP/HTTPS 代理

```bash
# 设置 HTTP 代理（替换为你的代理地址和端口）
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy https://127.0.0.1:7890

# 或者使用 socks5 代理
git config --global http.proxy socks5://127.0.0.1:7890
git config --global https.proxy socks5://127.0.0.1:7890
```

**常见代理端口：**
- Clash: `7890`
- V2Ray: `1080` 或 `10808`
- Shadowsocks: `1080`

#### 只对 GitHub 使用代理

```bash
# 只对 GitHub 使用代理
git config --global http.https://github.com.proxy http://127.0.0.1:7890
git config --global https.https://github.com.proxy https://127.0.0.1:7890
```

#### 取消代理设置

```bash
git config --global --unset http.proxy
git config --global --unset https.proxy
```

---

### 方案3：使用 GitHub 镜像站

如果无法直接访问 GitHub，可以使用镜像站。

#### 使用 GitHub 镜像（仅限克隆，推送仍需原地址）

```bash
# 克隆时使用镜像（示例）
git clone https://github.com.cnpmjs.org/USERNAME/REPO.git
```

**注意：** 镜像站通常只支持克隆，不支持推送。

---

### 方案4：修改 hosts 文件（临时方案）

#### macOS/Linux

```bash
# 编辑 hosts 文件
sudo nano /etc/hosts

# 添加以下内容（IP 地址可能会变化，需要查找最新 IP）
140.82.112.3 github.com
140.82.112.4 github.com
```

#### Windows

1. 以管理员身份打开记事本
2. 打开文件：`C:\Windows\System32\drivers\etc\hosts`
3. 添加相同内容
4. 保存

**查找最新 IP：**
- 访问 https://www.ipaddress.com
- 查询 `github.com` 的 IP 地址
- 更新 hosts 文件

---

### 方案5：使用 Gitee（码云）作为中转

如果以上方法都不行，可以使用 Gitee 作为中转。

#### 步骤1：在 Gitee 创建仓库

1. 登录 [Gitee](https://gitee.com)
2. 点击右上角 **+** → **新建仓库**
3. 填写信息并创建

#### 步骤2：推送到 Gitee

```bash
# 添加 Gitee 远程仓库
git remote add gitee https://gitee.com/YOUR_USERNAME/java-concurrent-learning.git

# 推送到 Gitee
git push -u gitee main
```

#### 步骤3：从 Gitee 同步到 GitHub（可选）

在 Gitee 仓库设置中可以配置同步到 GitHub。

---

## 🎯 推荐方案优先级

1. **方案1：使用 SSH** ⭐⭐⭐⭐⭐
   - 最稳定
   - 不需要代理
   - 一次配置，长期使用

2. **方案2：配置代理** ⭐⭐⭐⭐
   - 如果有代理，最简单
   - 需要代理服务

3. **方案5：使用 Gitee** ⭐⭐⭐
   - 国内访问快
   - 可以作为中转

4. **方案4：修改 hosts** ⭐⭐
   - 临时方案
   - IP 可能变化

---

## 🔍 诊断命令

### 检查网络连接

```bash
# 测试 GitHub 连接
ping github.com

# 测试 HTTPS 连接
curl -I https://github.com

# 测试 SSH 连接
ssh -T git@github.com
```

### 检查 Git 配置

```bash
# 查看所有 Git 配置
git config --list

# 查看远程仓库
git remote -v

# 查看代理设置
git config --global --get http.proxy
git config --global --get https.proxy
```

---

## 💡 快速解决步骤（推荐）

### 最简单的方法：使用 SSH

```bash
# 1. 生成 SSH 密钥（如果还没有）
ssh-keygen -t ed25519 -C "your_email@example.com"

# 2. 复制公钥
cat ~/.ssh/id_ed25519.pub

# 3. 添加到 GitHub（网页操作）
# GitHub → Settings → SSH and GPG keys → New SSH key

# 4. 测试连接
ssh -T git@github.com

# 5. 使用 SSH URL 添加远程仓库
git remote add origin git@github.com:YOUR_USERNAME/java-concurrent-learning.git

# 6. 推送
git push -u origin main
```

---

## 📝 常见问题

### Q1: SSH 连接还是失败？

**检查：**
```bash
# 查看 SSH 详细日志
ssh -vT git@github.com
```

**可能原因：**
- 防火墙阻止
- 网络环境限制
- 需要使用代理

### Q2: 代理设置后还是不行？

**检查代理是否正常工作：**
```bash
# 测试代理
curl -x http://127.0.0.1:7890 https://github.com
```

**确认代理端口：**
- 查看代理软件的设置
- 确认端口号是否正确

### Q3: 想同时使用多个远程仓库？

```bash
# 添加多个远程仓库
git remote add origin git@github.com:USERNAME/REPO.git
git remote add gitee https://gitee.com/USERNAME/REPO.git

# 推送到不同仓库
git push origin main
git push gitee main
```

---

## ✅ 验证成功

推送成功后，你应该看到：

```
Enumerating objects: 32, done.
Counting objects: 100% (32/32), done.
Compressing objects: 100% (32/32), done.
Writing objects: 100% (32/32), done.
To github.com:USERNAME/java-concurrent-learning.git
 * [new branch]      main -> main
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

---

## 🎉 完成！

选择最适合你的方案，通常 **SSH 方式**是最稳定的选择！

