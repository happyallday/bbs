# 员工论坛系统

企业内部论坛系统，支持企业微信工作台访问，具备敏感词过滤、人工审核、网络访问控制等功能。

## 技术栈

- **前端**: Vue3 + Vite + Element Plus + Pinia
- **后端**: SpringBoot 2.7.18 + MyBatis Plus 3.5.5
- **数据库**: MySQL 5.7
- **缓存**: Redis 7
- **部署**: Docker + Docker Compose + Nginx

## 系统特性

- ✅ 企业微信OAuth2统一认证
- ✅ 办公网IP白名单控制
- ✅ 敏感词自动检测(AC自动机算法)
- ✅ 人工审核机制
- ✅ 白名单用户豁免机制
- ✅ 论坛板块管理
- ✅ 帖子发布与回复
- ✅ 企业微信消息推送

---

## 开发路径与里程碑

### 📋 Phase 1: 项目基础搭建 (Week 1-2)

#### ✅ 里程碑 1.1 - 项目结构初始化

**已完成内容:**
- [x] 创建前后端项目骨架
- [x] 配置Maven/NPM依赖管理
- [x] 建立Git仓库并配置远程仓库
- [x] 创建Docker部署配置

**文件结构:**
```
code5/
├── frontend/           # Vue3前端 (Vite + Element Plus)
│   ├── src/
│   │   ├── api/       # API接口服务
│   │   ├── views/     # 页面组件
│   │   ├── router/    # 路由配置
│   │   └── stores/    # Pinia状态管理
│   ├── package.json
│   └── vite.config.js
│
├── backend/            # SpringBoot后端
│   ├── src/main/java/com/company/bbs/
│   │   ├── controller/  # RESTful控制器
│   │   ├── service/     # 业务逻辑层
│   │   ├── mapper/      # 数据访问层
│   │   ├── entity/      # JPA实体类
│   │   ├── dto/         # 数据传输对象
│   │   ├── config/      # 配置类
│   │   ├── filter/      # 访问过滤器
│   │   └── wechat/      # 企业微信集成
│   ├── src/main/resources/
│   ├── pom.xml
│   └── application.yml
│
├── sql/                # 数据库脚本
│   ├── init.sql       # 建表脚本 (14张核心表)
│   └── sample_data.sql # 示例数据
│
└── docker/             # Docker部署配置
    └── docker-compose.yml
```

**技术选型理由:**
- SpringBoot 2.7.x: 成熟稳定，完全兼容MySQL 5.7
- Vue3 3.x: 现代化前端框架，组合式API
- Element Plus: 企业级UI组件库，中文文档完善
- MySQL 5.7: 稳定版本，企业广泛使用

---

#### 📊 里程碑 1.2 - 核心数据库设计

**已完成内容:**
- [x] 设计14张核心数据表
- [x] 编写DDL脚本 (基于MySQL 5.7语法)
- [x] 初始化基础数据(敏感词库、管理员账户)
- [x] 建立表与表之间的外键约束

**数据表清单:**
1. `sys_user` - 用户表 (支持企业微信信息同步)
2. `sys_role` - 角色表
3. `sys_user_role` - 用户角色关联表
4. `sys_white_list` - 白名单用户表
5. `sys_office_network` - 办公网IP白名单表
6. `bbs_board` - 论坛板块表
7. `bbs_post` - 帖子表
8. `bbs_reply` - 回复表
9. `bbs_sensitive_word` - 敏感词库表
10. `bbs_audit_log` - 审核日志表
11. `bbs_like_record` - 点赞记录表
12. `bbs_collect_record` - 收藏记录表
13. `bbs_notification` - 通知消息表
14. `sys_config` - 系统配置表

**核心字段设计:**
- **用户表**: 包含企业微信UserID、部门、职位字段
- **帖子表**: 支持审核状态、置顶、精华标记
- **敏感词表**: 支持正则表达式、分类、严重程度
- **审核日志表**: 记录完整的审核流程

**数据库脚本位置:**
- 建表脚本: `/sql/init.sql`
- 示例数据: `/sql/sample_data.sql`

---

### 📋 Phase 2: 认证授权系统 (Week 3)

#### ✅ 里程碑 2.1 - 基础认证框架

**已完成内容:**
- [x] JWT工具类 (Token生成与验证)
- [x] Spring Security配置
- [x] JWT认证过滤器
- [x] Redis缓存配置
- [x] MyBatis Plus配置
- [x] 跨域配置

**关键组件:**
- `JwtUtils` - JWT Token工具类 (backend/src/main/java/com/company/bbs/utils/JwtUtils.java)
- `SecurityConfig` - Spring Security安全配置 (backend/src/main/java/com/company/bbs/config/SecurityConfig.java)
- `JwtAuthenticationFilter` - JWT认证过滤器 (backend/src/main/java/com/company/bbs/config/JwtAuthenticationFilter.java)
- `RedisConfig` - Redis缓存配置 (backend/src/main/java/com/company/bbs/config/RedisConfig.java)
- `MybatisPlusConfig` - MyBatis Plus分页配置 (backend/src/main/java/com/company/bbs/config/MybatisPlusConfig.java)

**技术实现:**
- 使用HS256算法签名JWT
- Token有效期24小时 (可配置)
- 支持Token过期验证
- 请求头携带Token: `Authorization: Bearer {token}`

---

#### ✅ 里程碑 2.2 - 企业微信集成

**已完成内容:**
- [x] 企业微信配置属性类
- [x] 企业微信服务类 (WeChatService)
- [x] Access Token获取与缓存
- [x] 用户信息获取API
- [x] 部门信息获取API
- [x] 用户信息同步机制

**关键组件:**
- `WeChatProperties` - 企业微信属性配置 (backend/src/main/java/com/company/bbs/wechat/WeChatProperties.java)
- `WeChatService` - 企业微信服务 (backend/src/main/java/com/company/bbs/wechat/WeChatService.java)
- `WeChatAccessTokenResponse` - Access Token响应DTO
- `WeChatUserInfoResponse` - 用户信息响应DTO
- `WeChatDepartmentResponse` - 部门信息响应DTO

**技术实现:**
- 企业微信Access Token自动获取并缓存在Redis (有效期7200秒)
- 通过授权码获取用户基本信息
- 支持获取用户详细信息 (姓名、部门、职位等)
- 支持获取部门列表信息

---

#### ✅ 里程碑 2.3 - 用户认证服务

**已完成内容:**
- [x] 认证服务类 (AuthService)
- [x] 企业微信登录流程
- [x] 用户自动创建与角色分配
- [x] 白名单用户检查
- [x] 登录/登出接口
- [x] 用户信息获取接口

**关键组件:**
- `AuthService` - 认证服务 (backend/src/main/java/com/company/bbs/service/AuthService.java)
- `AuthController` - 认证控制器 (backend/src/main/java/com/company/bbs/controller/AuthController.java)
- `SysUserMapper` - 用户数据访问层
- `SysWhiteListMapper` - 白名单数据访问层

**认证流程:**
```mermaid
前端发起登录请求 → 企业微信OAuth2授权 → 获取Access Token → 获取用户信息 → 查询/创建用户 → 生成JWT Token → 返回登录信息
```

**接口清单:**
- `POST /api/auth/login` - 用户登录 (企业微信授权)
- `GET /api/auth/info` - 获取当前用户信息
- `POST /api/auth/logout` - 用户登出

---

#### ✅ 里程碑 2.4 - 前端认证模块

**已完成内容:**
- [x] 前端HTTP请求封装 (Axios)
- [x] Token自动携带机制
- [x] 用户状态管理 (Pinia)
- [x] 登录页面 (企业微信授权入口)
- [x] 首页 (登录状态验证)

**关键组件:**
- `request.js` - Axios请求封装 (frontend/src/api/request.js)
- `auth.js` - 认证API (frontend/src/api/auth.js)
- `user.js` - 用户状态管理 (frontend/src/stores/user.js)
- `auth/index.vue` - 登录页面 (frontend/src/views/auth/index.vue)
- `home/index.vue` - 首页 (frontend/src/views/home/index.vue)

**前端功能:**
- 自动在请求头携带JWT Token
- Token过期自动跳转登录页
- 用户信息持久化存储
- 企业微信授权码自动识别与登录

---

### 📋 Phase 3: 网络访问控制 (Week 4)

**计划内容:**
- [ ] IP白名单管理接口
- [ ] 网络访问拦截器
- [ ] CIDR网段匹配算法
- [ ] 办公网自动识别
- [ ] 外部访问策略配置

**技术要点:**
- 拦截器链设计
- IP地址解析工具
- 网络访问日志记录

---

### 📋 Phase 4: 敏感词过滤系统 (Week 5)

**计划内容:**
- [ ] 敏感词CRUD接口
- [ ] AC自动机算法实现
- [ ] 文本内容匹配引擎
- [ ] 正则表达式支持
- [ ] 白名单用户豁免机制

**核心技术:**
- AC自动机数据结构
- Trie字典树
- 文本匹配算法

---

### 📋 Phase 5: 审核系统 (Week 6)

**计划内容:**
- [ ] 异步审核队列
- [ ] 人工审核工作台
- [ ] 审核规则引擎
- [ ] 自动化审核决策
- [ ] 审核通知推送

**审核流程:**
```
用户发布 → 敏感词判断 → 触发审核 → 审核员处理 → 发布/驳回
```

---

### 📋 Phase 6: 论坛核心功能 (Week 7-8)

#### 里程碑 6.1 - 板块管理
- [ ] 板块CRUD操作
- [ ] 板块权限配置
- [ ] 板块图标上传

#### 里程碑 6.2 - 帖子发布系统
- [ ] 富文本编辑器集成
- [ ] 帖子草稿功能
- [ ] 图片上传管理

#### 里程碑 6.3 - 帖子浏览与互动
- [ ] 帖子列表(分页/搜索/筛选)
- [ ] 帖子详情页
- [ ] 点赞/收藏功能

#### 里程碑 6.4 - 回复系统
- [ ] 回复发布功能
- [ ] 楼层显示
- [ ] 回复举报

---

### 📋 Phase 7: 用户与权限管理 (Week 9)

**计划内容:**
- [ ] 用户管理后台
- [ ] 角色权限配置
- [ ] 菜单权限管理
- [ ] 操作权限细粒度控制

---

### 📋 Phase 8: 统计与报表 (Week 10)

**计划内容:**
- [ ] 帖子统计(总数/新增/活跃度)
- [ ] 用户活跃度统计
- [ ] 板块热度排行
- [ ] ECharts可视化报表

---

### 📋 Phase 9: 企业微信消息推送 (Week 11)

**计划内容:**
- [ ] 企业微信API集成
- [ ] 回复通知推送
- [ ] 审核结果通知
- [ ] 消息中心模块

---

### 📋 Phase 10: 测试与优化 (Week 12)

**计划内容:**
- [ ] Service层单元测试
- [ ] Controller集成测试
- [ ] 数据库查询优化
- [ ] Redis缓存策略
- [ ] 并发压力测试

**性能指标:**
- 接口响应时间 < 200ms
- 并发支持 1000+ 用户
- 测试覆盖率 > 80%

---

### 📋 Phase 11: 部署与文档 (Week 13)

**计划内容:**
- [ ] Docker Compose生产配置
- [ ] Nginx反向代理配置
- [ ] API接口文档
- [ ] 部署运维文档
- [ ] 用户使用手册

---

### 📋 Phase 12: 上线与验收 (Week 14)

**计划内容:**
- [ ] 生产环境配置
- [ ] 企业微信应用配置
- [ ] 功能完整性测试
- [ ] 安全验收测试

---

## 快速开始

### 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 5.7
- Redis 7+

### 本地开发

#### 1. 启动数据库和Redis
```bash
cd docker
docker-compose up -d mysql redis
```

#### 2. 初始化数据库
```bash
mysql -h localhost -u root -p
source sql/init.sql
source sql/sample_data.sql
```

#### 3. 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### Docker部署
```bash
docker-compose up -d
```

访问地址: `http://localhost:80`

---

## API文档

后端运行后访问Swagger文档:
`http://localhost:8080/api/swagger-ui.html`

---

## 开发规范

### Git分支策略
- `main` - 生产环境
- `develop` - 开发环境
- `feature/*` - 功能分支
- `hotfix/*` - 紧急修复分支

### 代码规范
- 后端: 遵循阿里巴巴Java开发规范
- 前端: 遵循Vue官方风格指南

---

## 配置说明

### 后端配置
配置文件: `backend/src/main/resources/application.yml`

关键配置项:
- 企业微信配置: `wechat.*`
- 数据库配置: `spring.datasource.*`
- 办公网配置: `network.office-networks`
- JWT密钥: `jwt.secret`

### 前端配置
配置文件: `frontend/vite.config.js`

代理配置: 所有 `/api` 请求代理到 `http://localhost:8080`

---

## 贡献指南

1. Fork 本仓库
2. 创建 feature 分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

本项目采用内部版权所有。

---

## 联系方式

- 项目维护: IT部门
- 技术支持: support@company.com

---

**当前版本**: v1.0.0
**最后更新**: 2024-04-12
**开发状态**: Phase 2完成 ✅
**完成进度**: 2/12 阶段完成 (16.7%)