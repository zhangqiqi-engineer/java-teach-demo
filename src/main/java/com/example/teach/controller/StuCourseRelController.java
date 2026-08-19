package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.Course;
import com.example.teach.entity.StuCourseRel;
import com.example.teach.mapper.StuCourseRelMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/stuCourseRel")
public class StuCourseRelController {

    @Resource
    private StuCourseRelMapper stuCourseRelMapper;


    /**
     * 保存学生课程绑定关系
     * POST /api/stuCourseRel/save
     * 参数 studentId：学生id，courseIdList：选中课程id数组
     */
    @PostMapping("/save")
    public ApiResult<Void> saveRel(@RequestBody RelSaveDTO dto) {
        Long studentId = dto.getStudentId();
        List<Long> courseIdList = dto.getCourseIdList();

        // 1.先删除该学生旧的全部绑定关系
        stuCourseRelMapper.deleteByStudentId(studentId);

        // 2.如果没有选择任何课程，直接返回
        if(courseIdList == null || courseIdList.isEmpty()){
            return ApiResult.ok();
        }

        // 3.组装中间表对象，批量插入
        List<StuCourseRel> relList = new ArrayList<>();
        for(Long cid : courseIdList){
            StuCourseRel rel = new StuCourseRel();
            rel.setStudentId(studentId);
            rel.setCourseId(cid);
            relList.add(rel);
        }
        stuCourseRelMapper.batchInsert(relList);
        return ApiResult.ok();
    }

    /**
     * 获取某个学生已绑定的课程ID集合，用于前端多选框回显
     * GET /api/stuCourseRel/courseIds?studentId=1
     */
    @GetMapping("/courseIds")
    public ApiResult<List<Long>> getSelectedCourseIds(@RequestParam Long studentId){
        List<Long> ids = stuCourseRelMapper.selectCourseIdsByStudentId(studentId);
        return ApiResult.ok(ids);
    }

    /**
     * 获取学生绑定的完整课程信息
     * GET /api/stuCourseRel/courses?studentId=1
     */
    @GetMapping("/courses")
    public ApiResult<List<Course>> getStudentCourse(@RequestParam Long studentId){
        List<Course> list = stuCourseRelMapper.selectCourseByStudentId(studentId);
        return ApiResult.ok(list);
    }

    // 内部DTO，接收前端提交JSON
    public static class RelSaveDTO{
        private Long studentId;
        private List<Long> courseIdList;

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public List<Long> getCourseIdList() { return courseIdList; }
        public void setCourseIdList(List<Long> courseIdList) { this.courseIdList = courseIdList; }
    }
}
