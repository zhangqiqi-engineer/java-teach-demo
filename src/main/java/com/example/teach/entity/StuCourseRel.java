package com.example.teach.entity;
import lombok.Data;

@Data
public class StuCourseRel {
    private Long id;
    private Long studentId;
    private Long courseId;

}
