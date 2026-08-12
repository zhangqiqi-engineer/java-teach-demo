package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.Course;
import com.example.teach.mapper.CourseMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 查询课程：有参数按条件过滤，无参数查询全部
     */
    @GetMapping("/list")
    public ApiResult<List<Course>> list(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String teacher
    ) {
        List<Course> list = courseMapper.selectList(courseName, teacher);
        return ApiResult.ok(list);
    }

    /**
     * 根据id查询课程 GET /api/course/1
     */
    @GetMapping("/{id}")
    public ApiResult<Course> getById(@PathVariable Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return ApiResult.fail("课程不存在");
        }
        return ApiResult.ok(course);
    }

    /**
     * 新增课程 POST /api/course
     */
    @PostMapping
    public ApiResult<Void> add(@RequestBody Course course) {
        courseMapper.insert(course);
        return ApiResult.ok();
    }

    /**
     * 修改课程 PUT /api/course
     */
    @PutMapping
    public ApiResult<Void> update(@RequestBody Course course) {
        int rows = courseMapper.update(course);
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
        int rows = courseMapper.deleteById(id);
        if (rows <= 0) {
            return ApiResult.fail("删除失败，课程不存在");
        }
        return ApiResult.ok();
    }
}
