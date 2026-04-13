-- 员工论坛系统数据库脚本 (MySQL 5.7)
-- Database: bbs

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bbs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE bbs;

-- 1. 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    wechat_userid VARCHAR(100) UNIQUE COMMENT '企业微信UserID',
    wechat_openid VARCHAR(100) COMMENT '企业微信OpenID',
    email VARCHAR(100) COMMENT '邮箱',
    mobile VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    department VARCHAR(100) COMMENT '部门',
    position VARCHAR(100) COMMENT '职位',
    status TINYINT DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
    is_white_list TINYINT DEFAULT 0 COMMENT '是否白名单用户 1:是 0:否',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    INDEX idx_wechat_userid (wechat_userid),
    INDEX idx_status (status),
    INDEX idx_is_white_list (is_white_list)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 白名单用户表
CREATE TABLE sys_white_list (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    white_type TINYINT DEFAULT 1 COMMENT '白名单类型 1:内容发布豁免 2:敏感词豁免',
    reason VARCHAR(200) COMMENT '加入原因',
    operator_id BIGINT COMMENT '操作人ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status TINYINT DEFAULT 1 COMMENT '状态 1:有效 0:失效',
    UNIQUE KEY uk_user_type (user_id, white_type),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='白名单用户表';

-- 5. 办公网IP白名单表
CREATE TABLE sys_office_network (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_range VARCHAR(50) NOT NULL COMMENT 'IP地址段 CIDR格式',
    description VARCHAR(200) COMMENT '描述',
    network_type TINYINT DEFAULT 1 COMMENT '网络类型 1:内部办公网 2:其他允许IP',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    operator_id BIGINT COMMENT '操作人ID',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='办公网IP白名单表';

-- 6. 论坛板块表
CREATE TABLE bbs_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_name VARCHAR(100) NOT NULL COMMENT '板块名称',
    board_code VARCHAR(50) NOT NULL COMMENT '板块编码',
    description TEXT COMMENT '板块描述',
    icon_url VARCHAR(255) COMMENT '板块图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    post_count INT DEFAULT 0 COMMENT '帖子数量',
    status TINYINT DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
    require_auth TINYINT DEFAULT 0 COMMENT '是否需要权限',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_board_code (board_code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛板块表';

-- 7. 帖子表
CREATE TABLE bbs_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_number VARCHAR(50) NOT NULL COMMENT '帖子编号',
    user_id BIGINT NOT NULL COMMENT '发帖人ID',
    board_id BIGINT NOT NULL COMMENT '板块ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    summary VARCHAR(500) COMMENT '摘要',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    status TINYINT DEFAULT 1 COMMENT '状态 1:已发布 2:审核中 3:已驳回 0:草稿',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态 0:无需审核 1:待审核 2:审核通过 3:审核驳回',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶 1:置顶',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华 1:精华',
    sensitive_words TEXT COMMENT '匹配的敏感词(用于审核)',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    published_time DATETIME COMMENT '发布时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    UNIQUE KEY uk_post_number (post_number),
    INDEX idx_user_id (user_id),
    INDEX idx_board_id (board_id),
    INDEX idx_status (status),
    INDEX idx_audit_status (audit_status),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (board_id) REFERENCES bbs_board(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 8. 回复表
CREATE TABLE bbs_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '回复人ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父回复ID(0表示直接回复帖子)',
    reply_to_user_id BIGINT COMMENT '被回复的用户ID',
    content TEXT NOT NULL COMMENT '回复内容',
    floor_number INT NOT NULL COMMENT '楼层号',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态 1:已发布 2:审核中 3:已驳回',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态 0:无需审核 1:待审核 2:审核通过 3:审核驳回',
    sensitive_words TEXT COMMENT '匹配的敏感词',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_created_time (created_time),
    FOREIGN KEY (post_id) REFERENCES bbs_post(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';

-- 9. 敏感词库表
CREATE TABLE bbs_sensitive_word (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL COMMENT '敏感词',
    word_type TINYINT DEFAULT 1 COMMENT '敏感词类型 1:词汇 2:正则表达式',
    category VARCHAR(50) DEFAULT 'default' COMMENT '分类',
    severity TINYINT DEFAULT 1 COMMENT '严重程度 1:一般 2:严重 3:禁止',
    is_regex TINYINT DEFAULT 0 COMMENT '是否正则表达式 1:是 0:否',
    hit_count INT DEFAULT 0 COMMENT '命中次数',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_word (word),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词库表';

-- 10. 审核日志表
CREATE TABLE bbs_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型 post/reply',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    user_id BIGINT NOT NULL COMMENT '提交者ID',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_status TINYINT NOT NULL COMMENT '审核状态 1:待审核 2:审核通过 3:审核驳回',
    sensitive_words TEXT COMMENT '敏感词',
    content_snippet VARCHAR(500) COMMENT '内容摘要',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    audited_time DATETIME COMMENT '审核时间',
    INDEX idx_content (content_type, content_id),
    INDEX idx_user_id (user_id),
    INDEX idx_audit_status (audit_status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核日志表';

-- 11. 点赞记录表
CREATE TABLE bbs_like_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型 post/reply',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- 12. 收藏记录表
CREATE TABLE bbs_collect_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_post (user_id, post_id),
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录表';

-- 13. 通知消息表
CREATE TABLE bbs_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    notification_type VARCHAR(20) NOT NULL COMMENT '通知类型 reply/like/collect/audit/system',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    related_post_id BIGINT COMMENT '相关帖子ID',
    related_user_id BIGINT COMMENT '相关用户ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读 1:已读',
    push_status TINYINT DEFAULT 0 COMMENT '推送状态 0:未推送 1:已推送',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

-- 14. 系统配置表
CREATE TABLE sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型 string/json/number/boolean',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';