package com.example.teach.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 相关配置
 *
 * <p>@MapperScan 指定 Mapper 接口所在包，Spring 会自动为这些接口创建代理实现。
 * 也可以在每个 Mapper 接口上单独加 @Mapper，效果类似。
 */
@Configuration
@MapperScan("com.example.teach.mapper")
public class MybatisConfig {
}
