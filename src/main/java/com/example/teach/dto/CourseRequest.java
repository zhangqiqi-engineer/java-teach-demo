package com.example.teach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改课程 请求参数DTO
 * <p>不直接使用Course实体接收前端，避免前端传递id等内部字段</p>
 */
@Data
public class CourseRequest {

    /** 课程名称 */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100")
    private String courseName;

    /** 授课老师 */
    @NotBlank(message = "授课老师不能为空")
    @Size(max = 50, message = "老师名称长度不能超过50")
    private String teacher;

    /** 学分 */
    @NotNull(message = "学分不能为空")
    private Integer credit;

    /** 课时 */
    @NotNull(message = "课时不能为空")
    private Integer hours;
}
