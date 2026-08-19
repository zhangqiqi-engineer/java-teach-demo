package com.example.teach.mapper;

import com.example.teach.entity.StuCourseRel;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.example.teach.entity.Course;
public interface StuCourseRelMapper {

    // 删除某个学生全部旧的课程关联
    int deleteByStudentId(@Param("studentId") Long studentId);

    // 批量新增学生课程关联
    int batchInsert(@Param("list") List<StuCourseRel> list);

    // 根据学生id查询该学生绑定的所有课程id
    List<Long> selectCourseIdsByStudentId(@Param("studentId") Long studentId);

    // 根据学生id查询该学生绑定完整课程信息（用于回显）
    List<Course> selectCourseByStudentId(@Param("studentId") Long studentId);
}
