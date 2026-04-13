# 员工论坛系统 - 快速启动指南

## 🚀 快速启动

### 前置要求
- Node.js 16+
- JDK 8+
- MySQL 5.7
- Redis 7+
- Git

### 方法一: Docker Compose 一键启动 (推荐)

```bash
# 1. 克隆项目
git clone git@github.com:happyallday/bbs.git
cd bbs

# 2. 配置环境变量
cd docker
cp .env.example .env
# 编辑.env文件，配置数据库密码和企业微信配置

# 3. 启动服务
docker-compose up -d

# 4. 初始化数据库
docker exec -it bbs-mysql mysql -u root -p
# 输入root密码后执行:
source /docker-entrypoint-initdb.d/init.sql
source /docker-entrypoint-initdb.d/sample_data.sql
exit

# 5. 查看日志
docker-compose logs -f
```

### 方法二: 手动启动

#### 1. 启动数据库

```bash
# 启动MySQL
systemctl start mysqld

# 启动Redis
systemctl start redis
```

#### 2. 初始化数据库

```bash
mysql -u root -p
# 输入root密码，然后执行:
source /path/to/bbs/sql/init.sql
source /path/to/bbs/sql/sample_data.sql
```

#### 3. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/bbs-1.0.0.jar
```

#### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

## 🌐 访问地址

- **前端地址**: http://localhost:3000
- **后端API**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/api/health

## 📝 初次登录

1. 访问前端地址: http://localhost:3000
2. 跳转到登录页面
3. 使用企业微信工作台点击应用进行授权登录
4. 首次登录会自动创建用户信息

## 🔧 配置说明

### 后端配置文件

修改 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bbs
    username: root
    password: your_password

wechat:
  corp-id: your_corp_id
  agent-id: 1000001
  secret: your_wechat_secret

network:
  enable-office-ip-check: true
  exterior-access-paths: /home,/public
```

### 企业微信配置

1. 登录企业微信管理后台
2. 创建的自建应用
3. 获取配置信息:
   - `corp-id`: 企业ID (在"我的企业"页面)
   - `agent-id`: 应用ID (在应用详情页)
   - `secret`: 应用Secret (在应用详情页)
4. 设置可信域名
5. 配置回调地址（如需要）

### 网络访问配置

```yaml
network:
  office-networks:
    - 192.168.1.0/24    # 公司内网
    - 10.0.0.0/8        # 公司办公网络
  enable-office-ip-check: true
  exterior-access-paths: /home,/public
```

## 📊 使用说明

### 帐号说明

系统已初始化几个测试帐号:

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 超级管理员 | 内部测试用户，白名单用户 |
| test_user | admin123 | 普通用户 | 普通用户，需要审核 |

### 功能导航

#### 管理员功能
- 板块管理
- 用户管理
- 角色权限管理
- IP白名单管理
- 敏感词管理
- 内容审核
- 数据统计

#### 普通用户功能
- 浏览帖子
- 发布帖子（需审核）
- 点赞收藏
- 查看公告

### 审核流程

```
用户发布 → 敏感词检测 → 白名单检查 → 审核提交 → 人工审核 → 发布/驳回
```

- **白名单用户**: 直接发布，无需审核
- **普通用户**: 触发敏感词需要审核
- **审核员**: 在后台审核待审核内容

## 🔍 故障排查

### 后端启动失败

1. 检查MySQL和Redis是否运行
2. 检查数据库连接配置
3. 查看后端日志: `tail -f logs/bbs.log`

### 前端启动失败

1. 清除node_modules重新安装:
```bash
rm -rf node_modules package-lock.json
npm install
```

2. 检查端口3000是否被占用

### 无法登录

1. 检查企业微信配置是否正确
2. 检查网络访问控制配置
3. 查看后端日志中的错误信息

## 📚 相关文档

- [API接口文档](docs/API.md)
- [部署运维文档](docs/DEPLOY.md)
- [项目README](README.md)

## 💡 开发提示

### 后端开发

```bash
# 开发模式启动
cd backend
mvn spring-boot:run

# 热重载(debug模式)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 前端开发

```bash
# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

## 🎯 下一步

1. 完成企业微信应用配置
2. 创建管理员账号
3. 配置办公网IP白名单
4. 添加敏感词库
5. 开始使用论坛系统

---

**技术支持**: support@company.com
**项目维护**: IT部门