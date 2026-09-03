package com.example.teach.service;

import com.example.teach.common.ApiResult;
import com.example.teach.dto.CourseQuery;
import com.example.teach.dto.CourseRequest;
import com.example.teach.entity.Course;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {

    // 使用CourseQuery接收条件
    List<Course> selectList(CourseQuery query);

    Course selectById(Long id);

    // 新增接收CourseRequest
    void insert(CourseRequest request);

    // 修改接收id + CourseRequest
    int update(Long id, CourseRequest request);

    int deleteById(Long id);

    void export(HttpServletResponse response) throws Exception;

    ApiResult<String> importExcel(MultipartFile file);
}
