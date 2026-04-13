-- 员工论坛系统示例数据
USE bbs;

-- 插入角色数据
INSERT INTO sys_role (role_name, role_code, description) VALUES
('超级管理员', 'ROLE_ADMIN', '系统超级管理员'),
('普通用户', 'ROLE_USER', '普通论坛用户'),
('审核员', 'ROLE_AUDITOR', '内容审核员'),
('版主', 'ROLE_MODERATOR', '板块版主');

-- 插入管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, email, mobile, department, position, status, is_white_list) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@company.com', '13800138000', 'IT部', '系统架构师', 1, 1),
('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'test@company.com', '13800138001', '人事部', '专员', 1, 0);

-- 为管理员分配角色
INSERT INTO sys_user_role (user_id, role_id, created_time) VALUES
(1, 1, NOW()),
(1, 3, NOW()),
(2, 2, NOW());

-- 插入办公网IP白名单
INSERT INTO sys_office_network (ip_range, description, network_type, status, operator_id) VALUES
('192.168.1.0/24', '公司内网', 1, 1, 1),
('10.0.0.0/8', '公司办公网络', 1, 1, 1),
('172.16.0.0/12', '公司内网', 1, 1, 1);

-- 插入论坛板块
INSERT INTO bbs_board (board_name, board_code, description, sort_order, status, require_auth) VALUES
('技术交流', 'tech', '技术问题讨论与经验分享', 1, 1, 0),
('公告通知', 'announcement', '公司公告与通知', 2, 1, 1),
('生活吐槽', 'life', '生活分享与吐槽', 3, 1, 0),
('招聘求职', 'jobs', '内部招聘与求职', 4, 1, 0),
('产品反馈', 'feedback', '产品功能反馈与建议', 5, 1, 0);

-- 插入敏感词库
INSERT INTO bbs_sensitive_word (word, word_type, category, severity, is_regex, status) VALUES
('测试敏感词1', 1, 'politics', 3, 0, 1),
('测试敏感词2', 1, 'spam', 2, 0, 1),
('测试敏感词3', 1, 'violence', 3, 0, 1),
('垃圾广告', 1, 'spam', 2, 0, 1),
('.*(?:垃圾|广告).*', 2, 'spam', 2, 1, 1);

-- 插入示例帖子
INSERT INTO bbs_post (post_number, user_id, board_id, title, content, summary, status, audit_status, published_time) VALUES
('P20240412001', 1, 1, '欢迎来到员工论坛', '欢迎大家使用员工论坛系统，这是一个内部交流的平台。请遵守社区规范，文明交流。', '欢迎大家使用员工论坛系统', 1, 2, NOW()),
('P20240412002', 2, 1, 'Vue3组合式API使用心得', '最近在学习Vue3的组合式API，感觉非常好用。分享一下我的学习心得...', '分享Vue3学习心得', 1, 2, NOW()),
('P20240412003', 1, 2, '关于论坛系统的使用说明', '1. 请使用企业微信工作台访问论坛\n2. 外部用户只能浏览，不能发帖\n3. 内容经过审核后发布', '论坛使用说明', 1, 2, NOW());

-- 插入示例回复
INSERT INTO bbs_reply (post_id, user_id, parent_id, content, floor_number, status, audit_status) VALUES
(1, 2, 0, '感谢提供这么棒的论坛系统！', 1, 1, 2),
(1, 1, 0, '有好的建议随时提出！', 2, 1, 2),
(2, 1, 0, '组合式API确实很棒，推荐继续深入学习Composition API', 1, 1, 2);

-- 插入系统配置
INSERT INTO sys_config (config_key, config_value, description, config_type) VALUES
('system.title', '员工论坛系统', '系统标题', 'string'),
('audit.auto_approve', 'false', '是否自动通过审核', 'boolean'),
('audit.sensitive_threshold', '1', '敏感词触发审核阈值', 'number'),
('post.max_length', '10000', '帖子最大字数', 'number'),
('reply.max_length', '2000', '回复最大字数', 'number');

-- 更新板块帖子数
UPDATE bbs_board SET post_count = 1 WHERE id = 1;
UPDATE bbs_board SET post_count = 1 WHERE id = 2;