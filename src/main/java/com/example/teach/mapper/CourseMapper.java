package com.example.teach.mapper;
import org.apache.ibatis.annotations.Param;
import com.example.teach.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseMapper {
    // 查询全部课程
    List<Course> selectAll();

    //条件查询
    List<Course> selectList(@Param("courseName") String courseName,
                            @Param("teacher") String teacher);

    // 根据id查询单条课程
    Course selectById(Long id);

    // 新增课程
    int insert(Course course);

    // 修改课程
    int update(Course course);

    // 删除课程
    int deleteById(Long id);

    // 根据课程名称+老师查询是否已存在
    Course selectByNameAndTeacher(@Param("courseName") String courseName,
                                  @Param("teacher") String teacher);

}
