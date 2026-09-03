package com.example.teach.service.impl;

import com.example.teach.common.ApiResult;
import com.example.teach.dto.CourseQuery;
import com.example.teach.dto.CourseRequest;
import com.example.teach.entity.Course;
import com.example.teach.mapper.CourseMapper;
import com.example.teach.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Override
    public List<Course> selectList(CourseQuery query) {
        // 从query对象取出查询条件传给mapper
        return courseMapper.selectList(query.getCourseName(), query.getTeacher());
    }

    @Override
    public Course selectById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    public void insert(CourseRequest request) {
        // DTO 转 Entity
        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setTeacher(request.getTeacher());
        course.setCredit(request.getCredit());
        course.setHours(request.getHours());
        courseMapper.insert(course);
    }

    @Override
    public int update(Long id, CourseRequest request) {
        // DTO + url中的id 组装实体
        Course course = new Course();
        course.setId(id);
        course.setCourseName(request.getCourseName());
        course.setTeacher(request.getTeacher());
        course.setCredit(request.getCredit());
        course.setHours(request.getHours());
        return courseMapper.update(course);
    }

    @Override
    public int deleteById(Long id) {
        return courseMapper.deleteById(id);
    }

    @Override
    public void export(HttpServletResponse response) throws Exception {
        List<Course> courseList = courseMapper.selectList(null, null);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("课程数据.xlsx", "UTF-8");
        response.setHeader("Content‑Disposition", "attachment;filename=" + fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("课程列表");

            //表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("课程名称");
            headerRow.createCell(1).setCellValue("授课老师");
            headerRow.createCell(2).setCellValue("学分");
            headerRow.createCell(3).setCellValue("课时");

            //写入数据行
            for (int i = 0; i < courseList.size(); i++) {
                Course c = courseList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(c.getCourseName() == null ? "" : c.getCourseName());
                row.createCell(1).setCellValue(c.getTeacher() == null ? "" : c.getTeacher());
                row.createCell(2).setCellValue(c.getCredit());
                row.createCell(3).setCellValue(c.getHours());
            }
            workbook.write(response.getOutputStream());
        }
    }

    @Override
    public ApiResult<String> importExcel(MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.fail("请选择Excel文件");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ApiResult.fail("仅支持 xlsx / xls 文件");
        }

        List<Course> saveList = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>(); // 文件内去重

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Course course = new Course();
                course.setCourseName(getCellStr(row.getCell(0)));
                course.setTeacher(getCellStr(row.getCell(1)));

                Cell creditCell = row.getCell(2);
                if (creditCell != null && creditCell.getCellType() == CellType.NUMERIC) {
                    course.setCredit((int) creditCell.getNumericCellValue());
                }
                Cell hoursCell = row.getCell(3);
                if (hoursCell != null && hoursCell.getCellType() == CellType.NUMERIC) {
                    course.setHours((int) hoursCell.getNumericCellValue());
                }

                //课程名称为空跳过
                if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
                    continue;
                }

                // 文件内去重：同样的课程名称+老师只保留第一条
                String key = course.getCourseName() + "|" + course.getTeacher();
                if (seenKeys.contains(key)) {
                    continue;
                }
                seenKeys.add(key);

                Course exist = courseMapper.selectByNameAndTeacher(
                        course.getCourseName(), course.getTeacher());
                if (exist != null) {
                    // 已存在则更新学分和课时
                    exist.setCredit(course.getCredit());
                    exist.setHours(course.getHours());
                    courseMapper.update(exist);
                    continue;
                }
                saveList.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail("解析Excel失败：" + e.getMessage());
        }

        //批量插入
        for (Course c : saveList) {
            courseMapper.insert(c);
        }
        return ApiResult.ok("成功导入 " + saveList.size() + " 条课程数据，重复数据已自动跳过");
    }

    /**
     * 工具方法：读取单元格字符串，处理空单元格
     */
    private String getCellStr(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }
}
