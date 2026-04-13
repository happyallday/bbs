# 员工论坛系统 - 常见问题和解决方案

## 🚨 常见编译问题及解决方案

### 问题1: Maven编译类型推论错误

**症状**: 
```
不兼容的类型: 推论变量 T 具有不兼容的上限
```

**解决方案**:

使用IDE进行编译，IDE能更好地处理复杂的泛型类型推论：

1. **使用IntelliJ IDEA (推荐)**:
```bash
# 在IDE中导入项目
File -> Open -> 选择backend目录
# 等待Maven依赖下载完成
# 在IDE中重新构建
Build -> Rebuild Project
```

2. **使用Eclipse**:
```bash
# 导入为Maven项目
File -> Import -> Existing Maven Projects
# 选择backend目录
# Project -> Clean
# Project -> Build All
```

### 问题2: Windows环境路径问题

**症状**: 
文件路径分隔符问题导致编译错误

**解决方案**:
```bash
# 在Windows上使用Git Bash或WSL
# 避免使用cmd和PowerShell执行Maven命令
```

### 问题3: Java版本问题

**症状**: 
```
Java版本不匹配，Java 8还是Java 11+
```

**解决方案**:
```bash
# 检查Java版本
java -version

# 项目支持Java 8+,建议使用Java 11
# 下载并安装Java 11: https://adoptium.net/
```

### 问题4: Maven依赖下载慢

**症状**: 
```
Maven下载依赖很慢或失败
```

**解决方案**:
```bash
# 配置阿里云Maven镜像
# 创建或修改 ~/.m2/settings.xml

<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

### 问题5: Docker运行问题

**症状**: 
```
Docker容器启动失败或无法访问
```

**解决方案**:
```bash
# 检查Docker状态
docker ps -a

# 查看容器日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 检查端口占用
netstat -ano | findstr "8080"
```

---

## 🛠️ 推荐的开发环境配置

### 开发工具推荐

#### 后端开发
- **IDE**: IntelliJ IDEA 2023+
- **JDK**: Oracle JDK 11 或 Adoptium JDK 11
- **Maven**: 3.6.3+
- **Git**: 2.30+

#### 前端开发
- **IDE**: VSCode 1.75+ 或 WebStorm 2023+
- **Node.js**: 16.x 或 18.x
- **包管理器**: npm 9.x 或 pnpm 8.x

---

## 🔨 快速修复命令

### 1. 清理并重新编译
```bash
# 后端
cd backend
mvn clean
mvn install -DskipTests
mvn package -DskipTests

# 前端
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### 2. 检查依赖完整性
```bash
# 检查后端依赖
cd backend
mvn dependency:tree

# 检查前端依赖
cd frontend  
npm ls
```

### 3. 本地测试
```bash
# 启动MySQL和Redis
docker-compose up -d mysql redis

# 启动后端 (IDE中运行)
# 配置运行配置: EmployeeForumApplication
# VM options: -Dfile.encoding=UTF-8

# 启动前端
npm run dev
```

---

## 📊 项目架构说明

### 后端模块结构

```
backend/src/main/java/com/company/bbs/
├── audit/           # 审核系统
│   ├── ACSensitiveWordFilter.java
│   ├── RegexSensitiveWordFilter.java
│   └── CompositeSensitiveWordFilter.java
├── config/          # 配置类
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   ├── MybatisPlusConfig.java
│   └── RestTemplateConfig.java
├── controller/      # 控制器
├── service/          # 业务逻辑
├── mapper/           # 数据访问
├── entity/           # 实体类
├── dto/              # 数据传输对象
├── filter/           # 过滤器
├── utils/            # 工具类
└── wechat/           # 企业微信集成
```

---

## 🎯 功能测试清单

### 基础功能测试

#### 1. 健康检查
```bash
curl http://localhost:8080/api/health
# 预期返回: {"code":200,"message":"操作成功","data":{...}}
```

#### 2. 企业微信登录
1. 访问: http://localhost:3000/auth
2. 点击"企业微信登录"
3. 授权后返回首页

#### 3. 板块管理 (需要管理员权限)
```bash
# 登录后测试
GET /api/admin/boards/all
```

#### 4. 帖子功能
```bash
# 创建帖子
POST /api/posts
Content-Type: application/json

{
  "title": "测试标题",
  "content": "测试内容",
  "boardId": 1
}
```

---

## 📝 开发调试技巧

### 后端调试

1. **IDEA断点调试**:
   - 在代码行号左侧点击设置断点
   - 右键类名 -> Debug
   - F8单步执行，F9继续

2. **日志调试**:
```java
// 在代码中添加日志
log.debug("调试信息: {}", object);
log.info("业务信息: {}", object);
log.error("错误信息", exception);
```

### 前端调试

1. **Vue DevTools**: 浏览器F12切换到Vue标签
2. **Console调试**: `console.log('调试')`
3. **Network调试**: 查看API请求和响应

---

## 🔧 性能优化建议

### 后端优化

1. **数据库索引**: 已在init.sql中配置
2. **Redis缓存**: 敏感词、Access Token等
3. **分页查询**: 所有列表接口已实现分页
4. **异步处理**: 审核、消息推送使用@Async

### 前端优化

1. **路由懒加载**: 已配置动态import
2. **按需加载**: Element Plus按需引入
3. **请求缓存**: 考虑添加本地缓存

---

## 🐛 已知问题和限制

### 当前已知问题
1. **编译时类型推论**: 部分Controller方法在命令行编译时类型推论失败，建议使用IDE编译
2. **前端管理页面**: 部分前端管理页面仅提供API接口，前端组件逐步完善

### 计划改进
1. 修复所有Controller的类型注解
2. 完善前端管理页面组件
3. 添加单元测试覆盖
4. 优化查询性能

---

## 💡 开发建议

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用IDE代码格式化
- 提交前进行语法检查

### Git提交规范
```bash
# 提交消息格式
fix: 问题描述
feat: 功能描述
docs: 文档更新
refactor: 重构描述

# 具体示例
fix: 修复用户登录时的JWT验证问题
feat: 添加帖子点赞功能
docs: 更新API接口文档
```

---

## 📞 技术支持

如遇到无法解决的问题，请提供以下信息：

1. **环境信息**:
   - 操作系统版本
   - Java版本
   - Maven版本
   - Node.js版本

2. **错误日志**:
   - 完整的错误堆栈
   - 控制台输出

3. **复现步骤**:
   - 如何触发错误
   - 预期行为
   - 实际行为

**技术支持**: support@company.com
**项目仓库**: git@github.com:happyallday/bbs.git