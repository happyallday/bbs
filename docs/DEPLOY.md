# 员工论坛系统 - 部署文档

## 环境要求

### 最低配置
- **操作系统**: Linux (CentOS 7+, Ubuntu 18.04+)
- **CPU**: 2核
- **内存**: 4GB
- **硬盘**: 20GB

### 推荐配置
- **CPU**: 4核
- **内存**: 8GB
- **硬盘**: 50GB SSD

### 软件依赖
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Nginx**: 1.18+ (如不使用Docker)
- **MySQL**: 5.7
- **Redis**: 7.0+

## 部署方式

### 方式一: Docker Compose一键部署 (推荐)

#### 1. 克隆项目
```bash
git clone git@github.com:happyallday/bbs.git
cd bbs
```

#### 2. 配置环境变量

创建 `.env` 文件:
```env
# 数据库配置
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=bbs
MYSQL_USER=bbs_user
MYSQL_PASSWORD=bbs_password

# Redis配置
REDIS_PASSWORD=

# JWT密钥
JWT_SECRET=BBS2024JWTSECRETKEYFORAUTHENTICATIONANDAUTHORIZATION

# 企业微信配置
WECHAT_CORP_ID=your_corp_id
WECHAT_AGENT_ID=1000001
WECHAT_SECRET=your_wechat_secret

# 应用配置
SERVER_PORT=8080
```

#### 3. 启动服务
```bash
cd docker
docker-compose up -d
```

#### 4. 初始化数据库
```bash
docker exec -it bbs-mysql mysql -u root -p
# 输入root密码
source /docker-entrypoint-initdb.d/init.sql
source /docker-entrypoint-initdb.d/sample_data.sql
exit
```

#### 5. 检查服务状态
```bash
docker-compose ps
```

#### 6. 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f nginx
```

#### 7. 停止服务
```bash
docker-compose down
```

---

### 方式二: 手动部署

#### 1. 安装MySQL 5.7

CentOS:
```bash
yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
yum-config-manager --disable mysql80-community
yum-config-manager --enable mysql57-community
yum install -y mysql-community-server
systemctl start mysqld
systemctl enable mysqld
```

Ubuntu:
```bash
wget https://dev.mysql.com/get/mysql-apt-config_0.8.24-1_all.deb
dpkg -i mysql-apt-config_0.8.24-1_all.deb
apt update
apt install -y mysql-server-5.7
systemctl start mysql
systemctl enable mysql
```

#### 2. 安装Redis

CentOS:
```bash
yum install -y http://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
yum install -y redis
systemctl start redis
systemctl enable redis
```

Ubuntu:
```bash
apt update
apt install -y redis-server
systemctl start redis-server
systemctl enable redis-server
```

#### 3. 安装Nginx

CentOS:
```bash
yum install -y nginx
systemctl start nginx
systemctl enable nginx
```

Ubuntu:
```bash
apt install -y nginx
systemctl start nginx
systemctl enable nginx
```

#### 4. 配置Nginx

创建 `/etc/nginx/conf.d/bbs.conf`:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 日志
    access_log /var/log/nginx/bbs_access.log;
    error_log /var/log/nginx/bbs_error.log;
}
```

重新加载Nginx:
```bash
nginx -t
nginx -s reload
```

#### 5. 部署后端

```bash
cd backend
mvn clean package -DskipTests
java -jar target/bbs-1.0.0.jar --spring.profiles.active=prod
```

#### 6. 部署前端

```bash
cd frontend
npm install
npm run build

# 将dist目录复制到Nginx配置的路径
cp -r dist/* /path/to/nginx/html/
```

---

## 配置说明

### 后端配置文件

修改 `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bbs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: bbs_user
    password: bbs_password
    
  redis:
    host: localhost
    port: 6379
    password:
wechat:
  corp-id: your_corp_id
  agent-id: 1000001
  secret: your_wechat_secret
```

### 企业微信配置

1. 登录企业微信管理后台
2. 创建自建应用
3. 获取以下信息:
   - CorpID: 企业ID
   - AgentID: 应用ID
   - Secret: 应用Secret
4. 配置可信域名和回调地址
5. 设置应用可见范围

### 网络访问配置

修改 `application.yml` 中的网络配置:

```yaml
network:
  office-networks:
    - 192.168.1.0/24    # 公司内网
    - 10.0.0.0/8        # 公司办公网络
    - 172.16.0.0/12     # 公司内网
  enable-office-ip-check: true
  exterior-access-paths: /home,/public
```

---

## 监控与维护

### 查看应用日志

```bash
# 后端日志
tail -f logs/bbs.log

# Nginx日志
tail -f /var/log/nginx/bbs_access.log
tail -f /var/log/nginx/bbs_error.log
```

### 性能监控

```bash
# 查看应用进程
ps aux | grep java

# 查看内存使用
free -h

# 查看磁盘使用
df -h

# 查看CPU使用
top
```

### 数据库备份

```bash
# 备份数据库
mysqldump -u root -p bbs > bbs_backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p bbs < bbs_backup_20240412.sql
```

### 定时任务备份

添加到crontab:
```bash
# 每天凌晨2点备份数据库
0 2 * * * /usr/bin/mysqldump -u root -pyour_password bbs > /backup/bbs_$(date +\%Y\%m\%d).sql
```

---

## 故障排查

### 后端服务启动失败

1. 检查端口是否被占用:
```bash
lsof -i:8080
```

2. 检查数据库连接:
```bash
mysql -h localhost -u bbs_user -p
```

3. 查看详细日志:
```bash
java -jar target/bbs-1.0.0.jar --logging.level.root=DEBUG
```

### 数据库连接失败

1. 检查MySQL是否运行:
```bash
systemctl status mysqld
```

2. 检查用户权限:
```sql
GRANT ALL PRIVILEGES ON bbs.* TO 'bbs_user'@'%' IDENTIFIED BY 'bbs_password';
FLUSH PRIVILEGES;
```

3. 检查防火墙:
```bash
firewall-cmd --list-ports
firewall-cmd --permanent --add-port=3306/tcp
firewall-cmd --reload
```

### Redis连接失败

1. 检查Redis是否运行:
```bash
systemctl status redis
```

2. 测试Redis连接:
```bash
redis-cli ping
```

### 企业微信配置错误

1. 检查CorpID、AgentID、Secret是否正确
2. 确认企业微信应用已启用
3. 检查可信域名配置
4. 查看企业微信管理后台的错误日志

---

## 更新升级

### 后端更新

```bash
# 1. 备份数据库
mysqldump -u root -p bbs > bbs_backup_before_update.sql

# 2. 停止服务
docker-compose stop backend

# 3. 拉取最新代码
git pull origin main

# 4. 重新构建镜像
cd docker
docker-compose build backend

# 5. 启动服务
docker-compose up -d backend
```

### 前端更新

```bash
# 1. 备份当前版本
cp -r frontend/dist frontend/dist_backup

# 2. 拉取最新代码
git pull origin main

# 3. 重新构建
cd frontend
npm run build

# 4. 更新Nginx
cp -r dist/* /path/to/nginx/html/
```

---

## 安全建议

1. **修改默认密码**: 所有默认密码必须在首次部署时修改
2. **启用HTTPS**: 使用SSL证书保护数据传输
3. **防火墙配置**: 只开放必要的端口
4. **定期更新**: 及时更新系统和依赖包
5. **日志监控**: 定期检查异常日志
6. **数据备份**: 每日自动备份数据库
7. **权限管理**: 遵循最小权限原则

---

## 联系支持

如有问题，请联系:
- 技术支持邮箱: support@company.com
- 项目维护: IT部门