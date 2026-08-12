package com.example.teach.mapper;

import com.example.teach.dto.StudentQuery;
import com.example.teach.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生表 Mapper 接口
 *
 * <p>MyBatis 会根据同名 XML（StudentMapper.xml）生成实现类。
 * 方法名要与 XML 中的 &lt;select id="xxx"&gt; 等 id 保持一致。
 */
public interface StudentMapper {

    /** 按主键查询 */
    Student selectById(@Param("id") Long id);

    /** 按学号查询（用于唯一性校验） */
    Student selectByStudentNo(@Param("studentNo") String studentNo);

    /** 分页条件查询 */
    List<Student> selectPage(StudentQuery query);

    /** 条件统计总数 */
    long countPage(StudentQuery query);

    /** 新增 */
    int insert(Student student);

    /** 按主键更新 */
    int updateById(Student student);

    /** 按主键删除 */
    int deleteById(@Param("id") Long id);
}
