package com.example.teach.service.impl;

import com.example.teach.common.BizException;
import com.example.teach.common.PageResult;
import com.example.teach.dto.StudentQuery;
import com.example.teach.dto.StudentRequest;
import com.example.teach.entity.Student;
import com.example.teach.mapper.StudentMapper;
import com.example.teach.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学生业务实现
 *
 * <p>@Service：交给 Spring 容器管理
 * <p>@RequiredArgsConstructor（Lombok）：为 final 字段生成构造器，实现构造器注入（推荐写法）
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    @Override
    public PageResult<Student> page(StudentQuery query) {
        List<Student> list = studentMapper.selectPage(query);
        long total = studentMapper.countPage(query);
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public Student getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BizException("学生不存在，id=" + id);
        }
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentRequest request) {
        // 学号唯一性校验
        Student exists = studentMapper.selectByStudentNo(request.getStudentNo());
        if (exists != null) {
            throw new BizException("学号已存在：" + request.getStudentNo());
        }

        Student student = new Student();
        // BeanUtils 把同名属性从 request 拷到 entity（教学可用，生产可考虑 MapStruct）
        BeanUtils.copyProperties(request, student);
        studentMapper.insert(student);
        return student.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentRequest request) {
        // 先确认记录存在
        getById(id);

        Student byNo = studentMapper.selectByStudentNo(request.getStudentNo());
        if (byNo != null && !byNo.getId().equals(id)) {
            throw new BizException("学号已被其他学生占用：" + request.getStudentNo());
        }

        Student student = new Student();
        BeanUtils.copyProperties(request, student);
        student.setId(id);
        studentMapper.updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id);
        studentMapper.deleteById(id);
    }
}
