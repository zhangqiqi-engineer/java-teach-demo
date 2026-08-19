package com.example.teach.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生实体 —— 与数据库表 t_student 一一对应
 *
 * <p>教学提示：
 * <ul>
 *   <li>Entity 层只描述「数据长什么样」，不要写业务逻辑</li>
 *   <li>字段名建议用驼峰，MyBatis map-underscore-to-camel-case 会自动映射下划线列名</li>
 * </ul>
 */
@Data
public class Student {

    /** 主键 ID */
    private Long id;

    /** 学号（业务唯一） */
    private String studentNo;

    /** 姓名 */
    private String name;

    /** 性别：男 / 女 */
    private String gender;

    /** 年龄 */
    private Integer age;

    /** 班级名称 */
    private String className;

    /** 手机号 */
    private String phone;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    private String courseNames;
}
