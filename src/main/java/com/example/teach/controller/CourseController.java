package com.example.teach.controller;

import com.example.teach.common.ApiResult;
import com.example.teach.entity.Course;
import com.example.teach.mapper.CourseMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 查询课程：有参数按条件过滤，无参数查询全部
     */
    @GetMapping("/list")
    public ApiResult<List<Course>> list(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String teacher
    ) {
        List<Course> list = courseMapper.selectList(courseName, teacher);
        return ApiResult.ok(list);
    }

    /**
     * 根据id查询课程 GET /api/course/1
     */
    @GetMapping("/{id}")
    public ApiResult<Course> getById(@PathVariable Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return ApiResult.fail("课程不存在");
        }
        return ApiResult.ok(course);
    }

    /**
     * 新增课程 POST /api/course
     */
    @PostMapping
    public ApiResult<Void> add(@RequestBody Course course) {
        courseMapper.insert(course);
        return ApiResult.ok();
    }

    /**
     * 修改课程 PUT /api/course
     */
    @PutMapping
    public ApiResult<Void> update(@RequestBody Course course) {
        int rows = courseMapper.update(course);
        if (rows <= 0) {
            return ApiResult.fail("更新失败，课程不存在");
        }
        return ApiResult.ok();
    }

    /**
     * 删除课程 DELETE /api/course/1
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        int rows = courseMapper.deleteById(id);
        if (rows <= 0) {
            return ApiResult.fail("删除失败，课程不存在");
        }
        return ApiResult.ok();
    }

    //==================== 导入导出新增接口 ====================

    /**
     * Excel导出全部课程
     * GET /api/course/export
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
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

    /**
     * Excel导入课程（自动去重：课程名称+授课老师相同则跳过）
     * POST /api/course/import
     */
    @PostMapping("/import")
    public ApiResult<String> importExcel(@RequestParam("file") MultipartFile file) {
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
