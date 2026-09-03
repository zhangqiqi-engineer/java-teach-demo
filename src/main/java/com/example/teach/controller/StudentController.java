package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.common.PageResult;
import com.example.teach.dto.StuCourseRelSaveDTO;
import com.example.teach.dto.StudentQuery;
import com.example.teach.dto.StudentRequest;
import com.example.teach.entity.Course;
import com.example.teach.entity.Student;
import com.example.teach.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理接口
 *
 * <p>REST 风格约定（教学常用）：
 * <ul>
 *   <li>GET    /api/students       —— 分页列表</li>
 *   <li>GET    /api/students/{id}  —— 详情</li>
 *   <li>POST   /api/students       —— 新增</li>
 *   <li>PUT    /api/students/{id}  —— 修改</li>
 *   <li>DELETE /api/students/{id}  —— 删除</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /** 分页查询：查询参数自动绑定到 StudentQuery */
    @GetMapping
    public ApiResult<PageResult<Student>> page(StudentQuery query) {
        return ApiResult.ok(studentService.page(query));
    }

    /** 按 ID 查询详情 */
    @GetMapping("/{id}")
    public ApiResult<Student> detail(@PathVariable Long id) {
        return ApiResult.ok(studentService.getById(id));
    }

    /**
     * 新增学生
     * <p>@RequestBody：把 JSON 转成 Java 对象
     * <p>@Valid：触发 DTO 上的校验注解
     */
    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody StudentRequest request) {
        return ApiResult.ok(studentService.create(request));
    }

    /** 修改学生 */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id,
                                  @Valid @RequestBody StudentRequest request) {
        studentService.update(id, request);
        return ApiResult.ok();
    }

    /** 删除学生 */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ApiResult.ok();
    }

    // ====================== 新增：学生‑课程绑定三个接口 ======================

    /**
     * 保存学生课程绑定关系
     * POST /api/students/saveCourseRel
     */
    @PostMapping("/saveCourseRel")
    public ApiResult<Void> saveCourseRel(@Valid @RequestBody StuCourseRelSaveDTO dto) {
        studentService.saveStudentCourseRel(dto);
        return ApiResult.ok();
    }

    /**
     * 获取学生已选课程ID集合，用于前端多选框回显
     * GET /api/students/courseIds?studentId=xxx
     */
    @GetMapping("/courseIds")
    public ApiResult<List<Long>> getSelectedCourseIds(@RequestParam Long studentId) {
        List<Long> idList = studentService.getSelectedCourseIds(studentId);
        return ApiResult.ok(idList);
    }

    /**
     * 获取学生绑定完整课程信息
     * GET /api/students/courses?studentId=xxx
     */
    @GetMapping("/courses")
    public ApiResult<List<Course>> getStudentCourses(@RequestParam Long studentId) {
        List<Course> list = studentService.getStudentBindCourse(studentId);
        return ApiResult.ok(list);
    }

}
