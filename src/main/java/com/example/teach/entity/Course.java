package com.example.teach.entity;

import lombok.Data;

@Data
public class Course {
    private Long id;
    private String courseName;
    private String teacher;
    private Integer credit;
    private Integer hours;
}
