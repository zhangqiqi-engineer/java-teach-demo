package com.example.teach.service;

import com.example.teach.common.PageResult;
import com.example.teach.dto.StudentQuery;
import com.example.teach.dto.StudentRequest;
import com.example.teach.entity.Student;

/**
 * 学生业务接口
 *
 * <p>教学分层约定：
 * Controller → Service → Mapper → DB
 * Service 负责业务规则（唯一性校验、字段组装等），不直接写 SQL。
 */
public interface StudentService {

    PageResult<Student> page(StudentQuery query);

    Student getById(Long id);

    Long create(StudentRequest request);

    void update(Long id, StudentRequest request);

    void delete(Long id);
}
