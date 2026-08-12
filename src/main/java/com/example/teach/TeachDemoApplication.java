package com.example.teach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 教学演示项目启动类
 *
 * <p>@SpringBootApplication 是一个组合注解，等价于：
 * <ul>
 *   <li>@Configuration —— 标识这是配置类</li>
 *   <li>@EnableAutoConfiguration —— 开启自动配置</li>
 *   <li>@ComponentScan —— 扫描当前包及子包中的组件</li>
 * </ul>
 *
 * <p>运行方式：
 * <pre>
 *   ./gradlew bootRun
 *   或在 IDE 中直接运行 main 方法
 * </pre>
 */
@SpringBootApplication
public class TeachDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeachDemoApplication.class, args);
        System.out.println("========================================");
        System.out.println("  教学系统已启动，管理界面：");
        System.out.println("  http://localhost:8080/");
        System.out.println("========================================");
    }
}
