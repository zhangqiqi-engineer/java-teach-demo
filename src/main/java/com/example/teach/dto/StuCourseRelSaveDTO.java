package com.example.teach.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class StuCourseRelSaveDTO {
    @NotNull(message = "学生id不能为空")
    private Long studentId;
    private List<Long> courseIdList;
}
