# 员工论坛系统 - API文档

## 基础信息

- **Base URL**: `http://your-domain.com/api`
- **认证方式**: Bearer Token (JWT)
- **数据格式**: JSON

## 认证接口

### 用户登录

**请求:** `POST /auth/login`

**请求体:**
```json
{
  "code": "企业微信授权码",
  "state": "state"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "user_wx123",
      "realName": "张三",
      "avatar": "https://...",
      "department": "技术部",
      "position": "开发工程师",
      "isWhiteList": 0
    }
  }
}
```

### 获取用户信息

**请求:** `GET /auth/info`

**请求头:**
```
Authorization: Bearer {token}
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "user_wx123",
    "realName": "张三",
    "department": "技术部",
    "position": "开发工程师"
  }
}
```

### 用户登出

**请求:** `POST /auth/logout`

**响应:**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

## 论坛板块接口

### 获取所有板块

**请求:** `GET /admin/boards/all`

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "boardName": "技术交流",
      "boardCode": "tech",
      "description": "技术问题讨论与经验分享",
      "postCount": 45
    }
  ]
}
```

## 帖子接口

### 创建帖子

**请求:** `POST /posts`

**请求体:**
```json
{
  "title": "帖子标题",
  "content": "帖子内容",
  "boardId": 1
}
```

**响应:**
```json
{
  "code": 200,
  "message": "帖子发布成功，等待审核",
  "data": {
    "id": 123,
    "postNumber": "P20240412123456789",
    "status": 2,
    "auditStatus": 1
  }
}
```

### 获取帖子列表

**请求:** `GET /posts`

**参数:**
- current: 页码，默认1
- size: 每页数量，默认10
- boardId: 板块ID (可选)
- status: 帖子状态 (可选)

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 123,
        "postNumber": "P20240412123456789",
        "title": "帖子标题",
        "summary": "帖子摘要...",
        "viewCount": 123,
        "likeCount": 45,
        "replyCount": 23,
        "publishedTime": "2024-04-12 10:30:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

### 获取帖子详情

**请求:** `GET /posts/{id}`

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "postNumber": "P20240412123456789",
    "title": "帖子标题",
    "content": "帖子完整内容",
    "userId": 1,
    "boardId": 1,
    "viewCount": 123,
    "likeCount": 45,
    "replyCount": 23
  }
}
```

### 点赞帖子

**请求:** `POST /posts/{id}/like`

**响应:**
```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

## 审核接口

### 获取待审核列表

**请求:** `GET /admin/audit/pending`

**参数:**
- current: 页码，默认1
- size: 每页数量，默认10

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "contentType": "post",
        "contentId": 123,
        "userId": 2,
        "sensitiveWords": "测试敏感词1,测试敏感词2",
        "contentSnippet": "帖子内容摘要...",
        "createdTime": "2024-04-12 10:00:00"
      }
    ],
    "total": 5
  }
}
```

### 审核通过

**请求:** `POST /admin/audit/approve/{auditLogId}`

**请求体:**
```json
{
  "remark": "审核通过意见"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "审核通过",
  "data": null
}
```

### 审核驳回

**请求:** `POST /admin/audit/reject/{auditLogId}`

**请求体:**
```json
{
  "remark": "驳回原因"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "审核驳回",
  "data": null
}
```

### 获取审核统计

**请求:** `GET /admin/audit/statistics`

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 100,
    "pendingCount": 5,
    "approvedCount": 90,
    "rejectedCount": 5
  }
}
```

## 敏感词接口

### 获取敏感词列表

**请求:** `GET /admin/sensitive-words/list`

**参数:**
- current: 页码
- size: 每页数量
- word: 敏感词 (可选)
- category: 分类 (可选)
- status: 状态 (可选)

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "word": "测试敏感词1",
        "wordType": 1,
        "category": "politics",
        "severity": 3,
        "isRegex": 0,
        "hitCount": 10,
        "status": 1
      }
    ],
    "total": 50
  }
}
```

### 添加敏感词

**请求:** `POST /admin/sensitive-words/add`

**请求体:**
```json
{
  "word": "敏感词内容",
  "wordType": 1,
  "category": "spam",
  "severity": 2,
  "isRegex": 0
}
```

**响应:**
```json
{
  "code": 200,
  "message": "添加成功",
  "data": null
}
```

### 测试敏感词

**请求:** `POST /admin/sensitive-words/test`

**请求体:**
```json
{
  "text": "测试文本内容",
  "category": "spam"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "originalText": "测试文本内容",
    "filteredText": "测试██内容",
    "matchCount": 1,
    "matchedWords": ["敏感词"],
    "needAudit": true,
    "highSeverity": false
  }
}
```

## 统计接口

### 获取总体统计

**请求:** `GET /admin/statistics/overall`

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalPosts": 1234,
    "totalUsers": 456,
    "totalBoards": 12,
    "todayPosts": 10,
    "weekPosts": 56,
    "pendingAudits": 5,
    "approvedAudits": 100,
    "rejectedAudits": 2
  }
}
```

### 获取帖子趋势

**请求:** `GET /admin/statistics/post-trend`

**参数:**
- days: 统计天数，默认7

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "trend": [
      {
        "date": "04-06",
        "count": 8
      },
      {
        "date": "04-07",
        "count": 12
      }
    ],
    "days": 7
  }
}
```

### 获取板块统计

**请求:** `GET /admin/statistics/boards`

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "boardId": 1,
      "boardName": "技术交流",
      "postCount": 456
    }
  ]
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 401 | 未登录或Token已过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有需要认证的接口必须在请求头中携带JWT Token
2. Token有效期为24小时
3. 敏感词过滤会在发布内容时自动触发
4. 白名单用户可以豁免审核
5. 企业微信授权需要企业微信应用配置