package com.example.teach.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改学生时的请求参数对象（DTO）
 *
 * <p>为什么不直接用 Entity？
 * <ul>
 *   <li>对外暴露的字段可以和控制层解耦</li>
 *   <li>方便加校验注解，且不会把 id、createTime 等字段混进来</li>
 * </ul>
 */
@Data
public class StudentRequest {

    /** 学号 */
    @NotBlank(message = "学号不能为空")
    @Size(max = 32, message = "学号长度不能超过32")
    private String studentNo;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String name;

    /** 性别 */
    @NotBlank(message = "性别不能为空")
    private String gender;

    /** 年龄 */
    @Min(value = 1, message = "年龄最小为1")
    @Max(value = 150, message = "年龄最大为150")
    private Integer age;

    /** 班级 */
    @Size(max = 50, message = "班级名称长度不能超过50")
    private String className;

    /** 手机号 */
    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    /** 备注 */
    @Size(max = 200, message = "备注长度不能超过200")
    private String remark;
}
