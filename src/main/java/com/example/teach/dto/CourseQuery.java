package com.example.teach.dto;

import lombok.Data;

/**
 * 课程分页查询条件
 */
@Data
public class CourseQuery {

    /** 课程名称 模糊查询 */
    private String courseName;

    /** 授课老师 */
    private String teacher;

    /** 页码，从1开始 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 计算mysql offset，给mybatis limit offset用 */
    public int getOffset() {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (num - 1) * size;
    }

    public int getLimit() {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}
