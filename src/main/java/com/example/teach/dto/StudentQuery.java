package com.example.teach.dto;

import lombok.Data;

/**
 * 学生分页查询条件
 */
@Data
public class StudentQuery {

    /** 姓名（模糊） */
    private String name;

    /** 学号（精确或模糊均可，这里用模糊） */
    private String studentNo;

    /** 班级 */
    private String className;

    /** 页码，从 1 开始 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** MyBatis 分页用的 offset，由 Service 计算后传入 */
    public int getOffset() {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (num - 1) * size;
    }

    public int getLimit() {
        return pageSize == null || pageSize < 1 ? 10 : pageSize;
    }
}
