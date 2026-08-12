package com.example.teach;

import com.example.teach.common.ApiResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 不依赖数据库的简单单元测试示例
 */
class ApiResultTest {

    @Test
    void okShouldReturnCode200() {
        ApiResult<String> result = ApiResult.ok("hello");
        assertEquals(200, result.getCode());
        assertEquals("hello", result.getData());
        assertEquals("success", result.getMessage());
    }

    @Test
    void failShouldReturnMessage() {
        ApiResult<Void> result = ApiResult.fail("出错了");
        assertEquals(500, result.getCode());
        assertEquals("出错了", result.getMessage());
        assertNull(result.getData());
    }
}
