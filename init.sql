-- 创建数据库
CREATE DATABASE IF NOT EXISTS martyrs_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE martyrs_db;

-- 烈士基本身份信息表
CREATE TABLE IF NOT EXISTS martyr_basic_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    death_date DATE COMMENT '牺牲日期',
    birthplace VARCHAR(255) COMMENT '籍贯',
    ethnicity VARCHAR(50) COMMENT '民族',
    political_status VARCHAR(50) COMMENT '政治面貌',
    military_unit VARCHAR(255) COMMENT '所属部队',
    `rank` VARCHAR(100) COMMENT '职务/军衔',
    sacrifice_location VARCHAR(255) COMMENT '牺牲地点',
    sacrifice_reason TEXT COMMENT '牺牲原因',
    burial_location VARCHAR(255) COMMENT '安葬地点',
    photo VARCHAR(500) COMMENT '照片路径',
    description TEXT COMMENT '生平简介',
    status INT DEFAULT 1 COMMENT '状态 1-正常 0-删除',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='烈士基本身份信息表';

-- 事迹史料表
CREATE TABLE IF NOT EXISTS martyr_deed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    martyr_id BIGINT NOT NULL COMMENT '烈士ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    source VARCHAR(255) COMMENT '来源',
    deed_type VARCHAR(50) COMMENT '类型：事迹/史料',
    date DATE COMMENT '日期',
    author VARCHAR(100) COMMENT '作者',
    attachment VARCHAR(500) COMMENT '附件路径',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_martyr_id (martyr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事迹史料表';

-- 影像音视频表
CREATE TABLE IF NOT EXISTS media_library (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    martyr_id BIGINT NOT NULL COMMENT '烈士ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    type VARCHAR(50) COMMENT '类型：image/video/audio',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    format VARCHAR(50) COMMENT '文件格式',
    description TEXT COMMENT '描述',
    upload_date DATETIME COMMENT '上传日期',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_martyr_id (martyr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影像音视频表';

-- 文物实物表
CREATE TABLE IF NOT EXISTS cultural_relic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    martyr_id BIGINT NOT NULL COMMENT '烈士ID',
    name VARCHAR(255) NOT NULL COMMENT '文物名称',
    category VARCHAR(100) COMMENT '类别',
    material VARCHAR(100) COMMENT '材质',
    era VARCHAR(100) COMMENT '年代',
    origin VARCHAR(255) COMMENT '来源',
    description TEXT COMMENT '描述',
    preservation_state VARCHAR(50) COMMENT '保存状态',
    image VARCHAR(500) COMMENT '图片路径',
    location VARCHAR(255) COMMENT '存放位置',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_martyr_id (martyr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文物实物表';

-- 荣誉纪念表
CREATE TABLE IF NOT EXISTS honor_memorial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    martyr_id BIGINT NOT NULL COMMENT '烈士ID',
    honor_name VARCHAR(255) NOT NULL COMMENT '荣誉名称',
    honor_type VARCHAR(100) COMMENT '荣誉类型',
    issuing_authority VARCHAR(255) COMMENT '颁发单位',
    issue_date DATE COMMENT '颁发日期',
    description TEXT COMMENT '描述',
    image VARCHAR(500) COMMENT '图片路径',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_martyr_id (martyr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='荣誉纪念表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role VARCHAR(50) NOT NULL COMMENT '角色：ADMIN/VISITOR',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
