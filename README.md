# Java 教学演示项目（Spring Boot + MyBatis）

面向刚接触 Java / Spring Boot 的同学，用一个完整可运行的「学生管理系统」演示：

- Gradle 构建
- Spring Boot 3.5.x
- JDK 17
- MySQL + MyBatis
- 分层架构下的增删改查（CRUD）
- 简单管理界面（原生 HTML/CSS/JS）

---

## 1. 技术栈

| 技术 | 版本 | 作用 |
|------|------|------|
| JDK | 17 | 运行与编译 |
| Spring Boot | 3.5.16 | Web / 依赖管理 / 内嵌 Tomcat |
| MyBatis | 3.0.5（starter） | ORM，手写 SQL |
| MySQL | 8.x 推荐 | 数据存储 |
| Gradle | 8.x | 构建工具 |
| Lombok | 随 Boot 管理 | 减少样板代码 |

---

## 2. 项目结构（先认路）

```text
java-teach-demo/
├── build.gradle                          # 依赖与插件
├── settings.gradle
├── src/main/java/com/example/teach/
│   ├── TeachDemoApplication.java         # 启动类
│   ├── common/                           # 统一响应、异常处理
│   ├── config/                           # MyBatis / Web 配置
│   ├── controller/                       # 接口层（接收 HTTP）
│   ├── dto/                              # 入参对象
│   ├── entity/                           # 与表对应的实体
│   ├── mapper/                           # MyBatis 接口
│   └── service/                          # 业务逻辑
├── src/main/resources/
│   ├── application.yml                   # 数据库等配置
│   ├── mapper/StudentMapper.xml          # SQL 映射
│   ├── db/init.sql                       # 建库建表脚本
│   └── static/                           # 管理界面
└── README.md
```

请求链路（务必理解）：

```text
浏览器 / Postman
    → Controller（参数校验、调用 Service）
        → Service（业务规则：唯一性、组装数据）
            → Mapper 接口 + XML（执行 SQL）
                → MySQL
```

---

## 3. 快速启动

### 3.1 准备环境

1. 安装 **JDK 17**
2. 安装 **MySQL 8**，并保证本机可连接
3. 建议安装 IDE：IntelliJ IDEA

### 3.2 初始化数据库

```bash
mysql -uroot -p < src/main/resources/db/init.sql
```

脚本会创建库 `teach_demo`、表 `t_student`，并插入几条演示数据。

### 3.3 修改数据库账号

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/teach_demo?...
    username: root
    password: Root@123456
```

### 3.4 启动项目

在项目根目录执行：

```bash
./gradlew bootRun
```

若没有 Gradle Wrapper，可用本机 Gradle：

```bash
gradle bootRun
```

或在 IDEA 中打开项目，运行 `TeachDemoApplication`。

### 3.5 打开管理界面

浏览器访问：http://localhost:8080/

---

## 4. 接口一览

基础路径：`/api/students`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/students` | 分页查询（支持 name / studentNo / className） |
| GET | `/api/students/{id}` | 详情 |
| POST | `/api/students` | 新增 |
| PUT | `/api/students/{id}` | 修改 |
| DELETE | `/api/students/{id}` | 删除 |

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

新增示例：

```bash
curl -X POST http://localhost:8080/api/students \
  -H 'Content-Type: application/json' \
  -d '{
    "studentNo": "2026099",
    "name": "小明",
    "gender": "男",
    "age": 20,
    "className": "计科2401",
    "phone": "13900000000"
  }'
```

---

## 5. 建议学习顺序

1. 先跑通：改配置 → 执行 SQL → 启动 → 打开页面点增删改查
2. 从 `StudentController` 看接口如何接收参数
3. 到 `StudentServiceImpl` 看业务校验（学号唯一）
4. 对照 `StudentMapper` + `StudentMapper.xml` 理解 SQL 如何绑定
5. 阅读 `ApiResult` / `GlobalExceptionHandler`，理解统一响应与异常处理
6. 打开浏览器 F12，看前端 `static/js/app.js` 如何调用接口

---

## 6. 常见问题

**Q: 启动报错 `Communications link failure`？**  
A: MySQL 没启动，或 `application.yml` 中的 host/端口/账号密码不对。

**Q: 报错 `Unknown database 'teach_demo'`？**  
A: 还没执行 `init.sql`。

**Q: 页面空白 / 接口 404？**  
A: 确认启动日志端口是否为 8080；静态资源在 `src/main/resources/static/`。

**Q: Lombok 在 IDE 里爆红？**  
A: 安装 Lombok 插件，并开启 Annotation Processing。

---

## 7. 可扩展练习（作业建议）

1. 给学生表增加「邮箱」字段，打通前后端
2. 增加按性别筛选
3. 把删除改成逻辑删除（加 `deleted` 字段）
4. 增加课程表，实现「学生-课程」一对多查询
5. 给接口加简单登录拦截（Session / JWT 入门）

---

## 8. 说明

本项目刻意保持简单：无权限体系、无复杂前端框架，目的是让新人看清 Spring Boot + MyBatis 的主干。  
生产环境请补齐安全、审计、连接池调优、统一日志与更完善的测试。
# java-teach-demo
# java-teach-demo
