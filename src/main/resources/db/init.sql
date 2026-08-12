-- ============================================================
-- 教学演示库初始化脚本
-- 使用方式：在 MySQL 中执行本文件
--   mysql -uroot -p < src/main/resources/db/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS teach_demo
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE teach_demo;

-- 学生表
DROP TABLE IF EXISTS t_student;
CREATE TABLE t_student (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_no   VARCHAR(32)  NOT NULL                COMMENT '学号',
    name         VARCHAR(50)  NOT NULL                COMMENT '姓名',
    gender       VARCHAR(10)  NOT NULL                COMMENT '性别',
    age          INT          DEFAULT NULL            COMMENT '年龄',
    class_name   VARCHAR(50)  DEFAULT NULL            COMMENT '班级',
    phone        VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    remark       VARCHAR(200) DEFAULT NULL            COMMENT '备注',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 演示数据
INSERT INTO t_student (student_no, name, gender, age, class_name, phone, remark) VALUES
('2026001', '张三', '男', 20, '计科2401', '13800000001', '班长'),
('2026002', '李四', '女', 19, '计科2401', '13800000002', NULL),
('2026003', '王五', '男', 21, '软工2402', '13800000003', '学习委员'),
('2026004', '赵六', '女', 20, '软工2402', '13800000004', NULL),
('2026005', '钱七', '男', 22, '计科2401', '13800000005', '爱好编程');
