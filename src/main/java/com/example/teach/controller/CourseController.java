package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.dto.CourseQuery;
import com.example.teach.dto.CourseRequest;
import com.example.teach.entity.Course;
import com.example.teach.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 查询课程：有参数按条件过滤，无参数查询全部
     */
    @GetMapping("/list")
    public ApiResult<List<Course>> list(CourseQuery query) {
        List<Course> list = courseService.selectList(query);
        return ApiResult.ok(list);
    }

    /**
     * 根据id查询课程 GET /api/course/1
     */
    @GetMapping("/{id}")
    public ApiResult<Course> getById(@PathVariable Long id) {
        Course course = courseService.selectById(id);
        if (course == null) {
            return ApiResult.fail("课程不存在");
        }
        return ApiResult.ok(course);
    }

    /**
     * 新增课程 POST /api/course
     */
    @PostMapping
    public ApiResult<Void> add(@Valid @RequestBody CourseRequest request) {
        courseService.insert(request);
        return ApiResult.ok();
    }

    /**
     * 修改课程 PUT /api/course
     */
    @PutMapping
    public ApiResult<Void> update(@RequestBody CourseRequest request, @RequestParam Long id) {
        int rows = courseService.update(id, request);
        if (rows <= 0) {
            return ApiResult.fail("更新失败，课程不存在");
        }
        return ApiResult.ok();
    }

    /**
     * 删除课程 DELETE /api/course/1
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        int rows = courseService.deleteById(id);
        if (rows <= 0) {
            return ApiResult.fail("删除失败，课程不存在");
        }
        return ApiResult.ok();
    }

    //==================== 导入导出接口（只转发调用service） ====================
    /**
     * Excel导出全部课程
     * GET /api/course/export
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        courseService.export(response);
    }

    /**
     * Excel导入课程（自动去重：课程名称+授课老师相同则跳过）
     * POST /api/course/import
     */
    @PostMapping("/import")
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
        return courseService.importExcel(file);
    }
}
