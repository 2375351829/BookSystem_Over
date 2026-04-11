# MyBatis-Plus 集成指南

## 1. 简介

MyBatis-Plus（简称 MP）是一个 MyBatis 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。本系统使用 **mybatis-plus-spring-boot3-starter** 版本 **3.5.7**，专为 Spring Boot 3.x 设计。

### 主要特性：
- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响
- **强大的 CRUD 操作**：内置通用 Mapper、通用 Service，仅需少量配置即可实现单表大部分 CRUD 操作
- **支持 Lambda 表达式**：通过 Lambda 表达式，方便地编写各类查询条件
- **支持主键自动生成**：支持多达 4 种主键策略
- **内置分页插件**：基于 MyBatis 物理分页，配置好插件之后，分页操作非常简单
- **代码生成器**：支持数据库表结构快速生成代码

## 2. 版本信息和依赖配置

### 2.1 Maven依赖

在 [pom.xml](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\pom.xml) 中配置：

```xml
<!-- Database -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 2.2 技术栈版本信息

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 基础框架 |
| Java | 17 | JDK版本 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL Connector | 8.0.33 | 数据库驱动 |
| Lombok | 1.18.30 | 简化代码 |

## 3. 核心配置说明

### 3.1 MyBatisPlusConfig配置类详解

[MyBatisPlusConfig.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\config\MyBatisPlusConfig.java) 是MyBatis-Plus的核心配置类：

```java
package com.example.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

#### 配置说明：

1. **@Configuration 注解**：标记这是一个Spring配置类
2. **MybatisPlusInterceptor**：MyBatis-Plus的核心拦截器容器
3. **PaginationInnerInterceptor**：分页插件内部拦截器
   - 参数 `DbType.MYSQL`：指定数据库类型为MySQL，用于生成分页SQL

#### 分页插件工作原理：
- 拦截所有查询请求
- 当检测到Page对象作为参数时，自动进行物理分页
- 自动计算总记录数和总页数
- 在SQL末尾追加 LIMIT 子句

### 3.2 application.properties中的MyBatis配置

在 [application.properties](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\resources\application.properties) 中配置：

```properties
# MyBatis-Plus Configuration
mybatis-plus.mapper-locations=classpath:mapper/**/*.xml
mybatis-plus.type-aliases-package=com.example.demo.model
mybatis-plus.configuration.map-underscore-to-camel-case=true
```

#### 配置项说明：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| mapper-locations | classpath:mapper/**/*.xml | Mapper XML文件位置，支持通配符 |
| type-aliases-package | com.example.demo.model | 实体类别名包路径，XML中可直接使用类名 |
| map-underscore-to-camel-case | true | 开启下划线转驼峰命名映射 |

#### 数据源配置：

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/CityLibrary?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&connectTimeout=30000&socketTimeout=60000
spring.datasource.username=root
spring.datasource.password=admin123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.connection-timeout=60000
```

## 4. Entity实体类规范

### 4.1 注解说明

#### @TableName - 表名映射

指定实体类对应的数据库表名：

```java
@TableName("user_account")
public class UserAccount {
    // ...
}
```

**使用场景**：
- 当Java类名与数据库表名不一致时必须使用
- 例如：`UserAccount` 类对应 `user_account` 表

#### @TableId - 主键策略

指定主键字段及生成策略：

```java
@TableId(type = IdType.AUTO)
private Long id;
```

**主键策略类型**：

| 策略 | 说明 | 使用场景 |
|------|------|----------|
| IdType.AUTO | 数据库自增 | MySQL自增主键 |
| IdType.NONE | 无状态 | 需要手动设置ID |
| IdType.INPUT | 手动输入 | 前端传入ID |
| IdType.ID_WORKER | 全局唯一ID（数值型） | 分布式系统 |
| IdType.UUID | 全局唯一ID（字符串） | 需要字符串主键 |

本系统统一使用 **IdType.AUTO**（数据库自增）。

#### @TableField - 字段映射

当Java字段名与数据库列名不一致时使用：

```java
@TableField("user_code")
private String userId;

@TableField("class_name")
private String className;

@TableField("create_time")
private Date createTime;

@TableField("update_time")
private Date updateTime;
```

**常见场景**：
- Java使用驼峰命名，数据库使用下划线命名
- 字段名为Java关键字（如`class` -> `className`）
- 避免与MyBatis-Plus保留字冲突

#### @TableLogic - 逻辑删除（可选）

标记逻辑删除字段（本系统未启用此注解，而是手动管理deleted字段）：

```java
// 本系统的做法
private Integer deleted; // 0-正常, 1-已删除
```

#### @Version - 乐观锁（可选）

用于并发控制（本系统未使用）：

```java
@Version
private Integer version;
```

### 4.2 命名规范

#### Java实体类命名：
- 使用大驼峰命名法（PascalCase）
- 单数形式：`UserAccount` 而非 `UserAccounts`
- 示例：`Book`, `BorrowRecord`, `Category`, `Notification`

#### 数据库表命名：
- 使用小写字母+下划线（snake_case）
- 复数形式或业务含义明确即可
- 示例：`user_account`, `borrow_record`, `book_review`

#### 字段命名映射规则：
- 开启了 `map-underscore-to-camel-case: true`
- 自动转换：`create_time` -> `createTime`, `user_id` -> `userId`
- 特殊情况需要用 `@TableField` 手动指定

### 4.3 示例Entity

#### 示例1：用户账户实体 [UserAccount.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\model\UserAccount.java)

```java
package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_account")
public class UserAccount {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String userType;
    private String realName;
    private String phone;
    private String email;
    private String idCard;
    private String institution;
    private String role;
    private String language;

    private String studentId;
    private String facultyId;

    @TableField("user_code")
    private String userId;

    private String campus;
    private String college;
    private String grade;

    @TableField("class_name")
    private String className;

    private String counselor;

    private Integer status;
    private Integer deleted;
    private Integer violationCount;
    private Date lastViolationTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
```

**特点说明**：
- 使用Lombok的 `@Data` 注解自动生成getter/setter/toString/equals/hashCode
- 主键使用自增策略
- 多处使用 `@TableField` 处理特殊字段映射
- 包含逻辑删除字段 `deleted`

#### 示例2：图书实体 [Book.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\model\Book.java)

```java
@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String isbn;
    private String title;
    private String subtitle;
    private String titleEn;
    private String author;
    private String authorEn;
    private String translator;
    private String publisher;
    private String publishYear;
    private String edition;
    private String category;
    private String categoryName;
    private String language;
    private Integer pages;
    private BigDecimal price;
    private String tags;
    private String coverUrl;
    private String summary;
    private String summaryEn;
    private String status;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private String shelfNo;
    private Integer deleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
```

**特点说明**：
- 包含BigDecimal类型的price字段
- 包含图书状态和库存数字段
- 支持中英文字段

#### 示例3：借阅记录实体 [BorrowRecord.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\model\BorrowRecord.java)

```java
@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long bookId;
    private String bookBarcode;

    @TableField("borrow_date")
    private Date borrowDate;

    @TableField("due_date")
    private Date dueDate;

    @TableField("return_date")
    private Date returnDate;

    @TableField("renew_count")
    private Integer renewCount;

    private Integer status;
    private Long operatorId;
    private String remarks;
    private Integer deleted;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
```

**特点说明**：
- 外键关联字段：userId, bookId, operatorId
- 日期字段全部使用 `@TableField` 映射
- 包含续借次数字段

## 5. Mapper接口使用

### 5.1 基础Mapper继承BaseMapper

所有Mapper接口都需要继承 `BaseMapper<T>` 泛型接口：

```java
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    // 自定义方法...
}
```

**BaseMapper提供的泛型参数T**：对应的Entity实体类类型

**@Mapper注解作用**：告诉MyBatis这是一个Mapper接口，Spring会自动扫描并注册

### 5.2 内置CRUD方法说明

BaseMapper提供了丰富的CRUD方法，以下是本系统常用的方法：

#### Insert操作

```java
// 插入一条记录
int insert(T entity);
```

**示例**：
```java
Book book = new Book();
book.setTitle("Java编程思想");
book.setAuthor("Bruce Eckel");
int result = bookMapper.insert(book); // 返回影响行数
Long id = book.getId(); // 自增主键会自动回填
```

#### Delete操作

```java
// 根据ID删除
int deleteById(Serializable id);

// 根据ID批量删除
int deleteBatchIds(@Param("coll") Collection<? extends Serializable> idList);

// 根据条件删除
int delete(@Param("ew") Wrapper<T> wrapper);
```

**示例**：
```java
// 删除单条
bookMapper.deleteById(1L);

// 批量删除
bookMapper.deleteBatchIds(Arrays.asList(1L, 2L, 3L));

// 条件删除（本系统使用软删除）
QueryWrapper<Book> wrapper = new QueryWrapper<>();
wrapper.eq("id", 1);
bookMapper.delete(wrapper); // 不推荐，建议使用updateById实现软删除
```

#### Update操作

```java
// 根据ID更新
int updateById(@Param("et") T entity);

// 根据条件更新
int update(@Param("et") T entity, @Param("ew") Wrapper<T> wrapper);
```

**示例**：
```java
Book book = bookMapper.selectById(1L);
book.setTitle("新标题");
book.setUpdateTime(new Date());
int result = bookMapper.updateById(book);
```

#### Select操作

```java
// 根据ID查询
T selectById(Serializable id);

// 根据ID批量查询
List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList);

// 查询全部记录
List<T> selectList(@Param("ew") Wrapper<T> wrapper);

// 查询单条记录
T selectOne(@Param("ew") Wrapper<T> wrapper);

// 查询记录总数
Long selectCount(@Param("ew") Wrapper<T> wrapper);

// 查询结果为Map列表
List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<T> wrapper);

// 分页查询
IPage<T> selectPage(IPage<T> page, @Param("ew") Wrapper<T> wrapper);
```

**示例**：
```java
// 根据ID查询
Book book = bookMapper.selectById(1L);

// 批量查询
List<Book> books = bookMapper.selectBatchIds(Arrays.asList(1L, 2L, 3L));

// 条件查询
QueryWrapper<Book> wrapper = new QueryWrapper<>();
wrapper.eq("status", "1");
List<Book> list = bookMapper.selectList(wrapper);

// 计数
Long count = bookMapper.selectCount(wrapper);

// 分页查询
Page<Book> page = new Page<>(1, 10);
IPage<Book> result = bookMapper.selectPage(page, wrapper);
```

### 5.3 条件构造器使用

MyBatis-Plus提供了强大的条件构造器，用于构建复杂的查询条件。

#### QueryWrapper / LambdaQueryWrapper

**QueryWrapper**：使用字符串指定列名

```java
QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
wrapper.eq("username", "admin")
       .eq("deleted", 0)
       .like("realName", "张")
       .in("userType", Arrays.asList("STUDENT", "TEACHER"))
       .orderByDesc("createTime");
List<UserAccount> users = userAccountMapper.selectList(wrapper);
```

**LambdaQueryWrapper**：使用Lambda表达式指定列名（推荐，避免拼写错误）

```java
LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(BorrowRecord::getUserId, userId)
       .eq(BorrowRecord::getBookId, bookId)
       .eq(BorrowRecord::getStatus, 0)
       .eq(BorrowRecord::getDeleted, 0);
Long count = borrowRecordMapper.selectCount(wrapper);
```

#### UpdateWrapper / LambdaUpdateWrapper

用于构建更新条件：

```java
UpdateWrapper<Book> wrapper = new UpdateWrapper<>();
wrapper.eq("id", 1L)
       .set("status", "0")
       .set("updateTime", new Date());
bookMapper.update(null, wrapper);
```

#### 常用条件方法

| 方法 | 说明 | SQL示例 |
|------|------|---------|
| eq | 等于 | `WHERE column = value` |
| ne | 不等于 | `WHERE column != value` |
| gt | 大于 | `WHERE column > value` |
| ge | 大于等于 | `WHERE column >= value` |
| lt | 小于 | `WHERE column < value` |
| le | 小于等于 | `WHERE column <= value` |
| like | 模糊查询 | `WHERE column LIKE '%value%'` |
| notLike | 不包含 | `WHERE column NOT LIKE '%value%'` |
| likeLeft | 左模糊 | `WHERE column LIKE '%value'` |
| likeRight | 右模糊 | `WHERE column LIKE 'value%'` |
| in | IN查询 | `WHERE column IN (v1, v2, ...)` |
| notIn | NOT IN | `WHERE column NOT IN (v1, v2, ...)` |
| between | BETWEEN | `WHERE column BETWEEN v1 AND v2` |
| isNull | IS NULL | `WHERE column IS NULL` |
| isNotNull | IS NOT NULL | `WHERE column IS NOT NULL` |
| orderByAsc | 升序排序 | `ORDER BY column ASC` |
| orderByDesc | 降序排序 | `ORDER BY column DESC` |
| and | AND连接 | `AND (...)` |
| or | OR连接 | `OR (...)` |

**复杂条件示例**（来自UserService）：

```java
QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("deleted", 0);

if (keyword != null && !keyword.isEmpty()) {
    queryWrapper.and(qw -> qw.like("username", keyword)
            .or().like("real_name", keyword)
            .or().like("phone", keyword)
            .or().like("email", keyword)
            .or().like("student_id", keyword)
            .or().like("faculty_id", keyword)
            .or().like("user_code", keyword));
}

if (type != null && !type.isEmpty()) {
    queryWrapper.eq("user_type", type);
}

queryWrapper.orderByDesc("create_time");
```

生成的SQL类似：
```sql
SELECT * FROM user_account
WHERE deleted = 0
  AND (
    username LIKE '%keyword%'
    OR real_name LIKE '%keyword%'
    OR phone LIKE '%keyword%'
    OR email LIKE '%keyword%'
    OR student_id LIKE '%keyword%'
    OR faculty_id LIKE '%keyword%'
    OR user_code LIKE '%keyword%'
  )
  AND user_type = 'STUDENT'
ORDER BY create_time DESC
```

### 5.4 自定义SQL

#### 方式一：@Select注解（推荐简单查询）

```java
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    @Select("SELECT * FROM user_account WHERE username = #{username} LIMIT 1")
    UserAccount selectByUsername(String username);
}

@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Select("SELECT * FROM book WHERE isbn = #{isbn} AND deleted = 0 LIMIT 1")
    Book selectByisbn(String isbn);
}
```

**优点**：
- 简单直观，SQL直接写在方法上
- 适合简单查询
- 无需额外XML文件

#### 方式二：XML映射文件（推荐复杂查询）

1. 在resources/mapper目录下创建XML文件
2. Mapper接口中定义方法
3. XML中编写SQL

示例目录结构：
```
src/main/resources/
└── mapper/
    ├── UserAccountMapper.xml
    ├── BookMapper.xml
    └── ...
```

### 5.5 实际Mapper示例

#### 示例1：UserAccountMapper [UserAccountMapper.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\repository\UserAccountMapper.java)

```java
package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    @Select("SELECT * FROM user_account WHERE username = #{username} LIMIT 1")
    UserAccount selectByUsername(String username);
}
```

**特点**：
- 继承BaseMapper获得所有CRUD方法
- 自定义selectByUsername方法用于登录验证
- 使用@Select注解简化SQL定义

#### 示例2：BookMapper [BookMapper.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\repository\BookMapper.java)

```java
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    @Select("SELECT * FROM book WHERE isbn = #{isbn} AND deleted = 0 LIMIT 1")
    Book selectByisbn(String isbn);
}
```

**特点**：
- 自定义按ISBN查询方法
- 增加deleted条件过滤已删除数据

#### 示例3：BorrowRecordMapper [BorrowRecordMapper.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\repository\BorrowRecordMapper.java)

```java
package com.example.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.BorrowRecord;

public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}
```

**特点**：
- 最简单的Mapper，仅继承BaseMapper
- 所有操作都通过Service层调用基础方法完成
- 无需自定义SQL

## 6. Service层使用

### 6.1 IService接口说明

虽然MyBatis-Plus提供了IService接口和ServiceImpl实现类，但本系统选择**不使用IService**，而是在自定义Service类中直接注入Mapper进行操作。

**本系统的设计模式**：
```java
@Service
public class UserService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    // 直接调用Mapper方法
    public UserAccount getUserById(Long id) {
        return userAccountMapper.selectById(id);
    }
}
```

**原因**：
- 更灵活的业务逻辑控制
- 可以组合多个Mapper的操作
- 便于事务管理
- 更符合项目实际需求

如果需要使用IService模式，可以这样：

```java
// 接口
public interface IUserService extends IService<UserAccount> { }

// 实现
@Service
public class UserServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserService { }
```

### 6.2 内置Service方法（参考）

虽然本系统未使用IService，但了解其方法有助于理解MyBatis-Plus的能力：

#### Save操作
```java
// 插入一条记录
boolean save(T entity);

// 批量插入
boolean saveBatch(Collection<T> entityList);
boolean saveBatch(Collection<T> entityList, int batchSize);
```

#### Remove操作
```java
// 根据ID删除
boolean removeById(Serializable id);

// 根据ID批量删除
boolean removeByIds(Collection<? extends Serializable> idList);

// 根据条件删除
boolean remove(Wrapper<T> wrapper);
```

#### Update操作
```java
// 根据ID更新
boolean updateById(T entity);

// 批量更新
boolean updateBatchById(Collection<T> entityList);
boolean updateBatchById(Collection<T> entityList, int batchSize);

// 根据条件更新
boolean update(T entity, Wrapper<T> wrapper);
```

#### Get/List操作
```java
// 根据ID查询
T getById(Serializable id);

// 根据ID批量查询
Collection<T> listByIds(Collection<? extends Serializable> idList);

// 查询全部
List<T> list();
List<T> list(Wrapper<T> wrapper);

// 查询数量
int count();
int count(Wrapper<T> wrapper);

// 分页查询
IPage<T> page(IPage<T> page);
IPage<T> page(IPage<T> page, Wrapper<T> wrapper);
```

### 6.3 自定义Service方法

本系统的Service层展示了如何灵活使用MyBatis-Plus：

#### 用户注册示例（来自UserService）

```java
public boolean registerUser(UserAccount userAccount) {
    // 1. 检查用户名是否已存在
    QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", userAccount.getUsername());
    if (userAccountMapper.selectOne(queryWrapper) != null) {
        return false;
    }

    // 2. 密码强度验证
    PasswordValidator.ValidationResult validationResult =
        PasswordValidator.validate(userAccount.getPassword());
    if (!validationResult.isValid()) {
        throw new RuntimeException(validationResult.getMessage());
    }

    // 3. 密码加密
    userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));

    // 4. 设置默认值
    userAccount.setStatus(1);
    userAccount.setDeleted(0);
    userAccount.setCreateTime(new Date());
    userAccount.setUpdateTime(new Date());

    // 5. 插入数据库
    userAccountMapper.insert(userAccount);

    return true;
}
```

#### 创建用户示例（带身份标识）

```java
public String createUserWithNewFields(UserAccount userAccount) {
    // 1. 校验用户名唯一性
    if (userAccount.getUsername() != null && !userAccount.getUsername().isEmpty()) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount.getUsername());
        if (userAccountMapper.selectOne(queryWrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
    }

    // 2. 校验学号唯一性
    if (userAccount.getStudentId() != null && !userAccount.getStudentId().isEmpty()) {
        if (isStudentIdExists(userAccount.getStudentId())) {
            throw new RuntimeException("学号已存在");
        }
    }

    // 3. 生成初始密码
    String initialPassword = generateInitialPassword();
    userAccount.setPassword(passwordEncoder.encode(initialPassword));

    // 4. 设置默认值
    userAccount.setStatus(1);
    userAccount.setDeleted(0);
    userAccount.setCreateTime(new Date());
    userAccount.setUpdateTime(new Date());

    // 5. 插入数据库
    userAccountMapper.insert(userAccount);

    return initialPassword; // 返回初始密码供管理员查看
}
```

### 6.4 实际Service示例

#### 示例1：UserService [UserService.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\service\UserService.java)

**核心功能**：
- 用户注册与创建
- 用户查询（按ID、用户名、学号等）
- 用户更新与删除（软删除）
- 分页查询与搜索
- 密码管理

**关键方法**：

```java
// 分页查询（支持多条件筛选）
public IPage<UserAccount> getUsers(int page, int size, String keyword,
                                   String type, Integer status, String searchField) {
    Page<UserAccount> pageInfo = new Page<>(page, size);
    QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("deleted", 0);

    // 关键词搜索
    if (keyword != null && !keyword.isEmpty()) {
        if (searchField != null && SEARCH_FIELD_MAP.containsKey(searchField)) {
            // 指定字段搜索
            String dbColumn = SEARCH_FIELD_MAP.get(searchField);
            if ("identity_id".equals(dbColumn)) {
                queryWrapper.and(qw -> qw.like("student_id", keyword)
                        .or().like("faculty_id", keyword)
                        .or().like("user_code", keyword));
            } else {
                queryWrapper.like(dbColumn, keyword);
            }
        } else {
            // 全字段搜索
            queryWrapper.and(qw -> qw.like("username", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword)
                    // ... 更多字段
                    );
        }
    }

    // 类型筛选
    if (type != null && !type.isEmpty()) {
        queryWrapper.eq("user_type", type);
    }

    // 状态筛选
    if (status != null) {
        queryWrapper.eq("status", status);
    }

    queryWrapper.orderByDesc("create_time");
    return userAccountMapper.selectPage(pageInfo, queryWrapper);
}
```

#### 示例2：BookService [BookService.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\service\BookService.java)

**核心功能**：
- 图书增删改查
- 图书搜索（标题、作者、ISBN、出版社）
- 分类筛选
- 库存管理

**关键方法**：

```java
// 搜索图书
public IPage<Book> searchBooks(String keyword, int page, int size) {
    Page<Book> pageInfo = new Page<>(page, size);
    QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("deleted", 0)
            .and(qw -> qw.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("isbn", keyword)
                    .or().like("publisher", keyword));
    return bookMapper.selectPage(pageInfo, queryWrapper);
}

// 软删除图书
public boolean deleteBook(Long id) {
    Book book = bookMapper.selectById(id);
    if (book != null) {
        book.setDeleted(1);
        book.setUpdateTime(new Date());
        int result = bookMapper.updateById(book);
        return result > 0;
    }
    return false;
}
```

#### 示例3：BorrowService [BorrowService.java](file:///g:\CIFS_Java_web\Javaweb\BookSystem_Over\spring-boot-backend\src\main\java\com\example\demo\service\BorrowService.java)

**核心功能**：
- 图书借阅（含业务校验）
- 图书归还（含逾期罚款计算）
- 续借功能
- 借阅历史查询

**关键方法**（借阅流程）：

```java
@Transactional
public BorrowRecord borrowBook(Long userId, Long bookId, String bookBarcode) {
    // 1. 校验用户
    UserAccount user = userAccountMapper.selectById(userId);
    if (user == null || user.getDeleted() == 1) {
        throw new RuntimeException("用户不存在");
    }

    // 2. 校验借阅权限
    UserType userType = UserType.fromCode(user.getUserType());
    if (!canUserBorrow(userId, userType)) {
        int maxLimit = borrowRuleService.getMaxBorrowLimit(userType);
        throw new RuntimeException("用户借阅数量已达上限（最大" + maxLimit + "本）");
    }

    // 3. 校验图书
    Book book = bookMapper.selectById(bookId);
    if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
        throw new RuntimeException("图书无可用副本");
    }

    // 4. 检查是否重复借阅
    LambdaQueryWrapper<BorrowRecord> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(BorrowRecord::getUserId, userId)
            .eq(BorrowRecord::getBookId, bookId)
            .eq(BorrowRecord::getStatus, 0)
            .eq(BorrowRecord::getDeleted, 0);
    if (borrowRecordMapper.selectCount(queryWrapper) > 0) {
        throw new RuntimeException("您已借阅该图书，尚未归还");
    }

    // 5. 创建借阅记录
    int borrowDays = borrowRuleService.getBorrowDays(userType);
    BorrowRecord borrowRecord = new BorrowRecord();
    borrowRecord.setUserId(userId);
    borrowRecord.setBookId(bookId);
    borrowRecord.setBookBarcode(bookBarcode);
    borrowRecord.setBorrowDate(new Date());
    borrowRecord.setDueDate(new Date(System.currentTimeMillis()
        + (long) borrowDays * 24 * 60 * 60 * 1000));
    borrowRecord.setRenewCount(0);
    borrowRecord.setStatus(0);
    borrowRecord.setDeleted(0);
    borrowRecord.setCreateTime(new Date());
    borrowRecord.setUpdateTime(new Date());
    borrowRecordMapper.insert(borrowRecord);

    // 6. 更新库存
    book.setAvailableCopies(book.getAvailableCopies() - 1);
    if (book.getAvailableCopies() <= 0) {
        book.setStatus("borrowed");
    }
    book.setUpdateTime(new Date());
    bookMapper.updateById(book);

    // 7. 发送通知
    notificationService.sendNotification(userId, "BORROW",
        "借阅成功通知", content);

    return borrowRecord;
}
```

**特点**：
- 使用@Transactional保证事务一致性
- 使用LambdaQueryWrapper避免硬编码字符串
- 组合多个Service协作完成业务
- 完整的业务校验流程

## 7. 分页插件使用

### 7.1 Page对象创建

MyBatis-Plus的分页非常简单，只需创建Page对象：

```java
// 创建分页对象（当前页，每页大小）
Page<UserAccount> page = new Page<>(currentPage, pageSize);

// 常用构造函数
Page<T> page = new Page<>(1, 10);          // 第1页，每页10条
Page<T> page = new Page<>(2, 20, false);   // 第2页，每页20条，不查询总数
```

**Page对象常用方法**：
```java
page.getCurrent();     // 当前页
page.getSize();        // 每页大小
page.getTotal();       // 总记录数
page.getPages();       // 总页数
page.getRecords();     // 当前页数据列表
page.hasNext();        // 是否有下一页
page.hasPrevious();    // 是否有上一页
```

### 7.2 分页查询示例

#### 基础分页

```java
public IPage<UserAccount> getAllUsers(int page, int size) {
    // 1. 创建分页对象
    Page<UserAccount> pageInfo = new Page<>(page, size);

    // 2. 创建查询条件
    QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("deleted", 0);

    // 3. 执行分页查询
    return userAccountMapper.selectPage(pageInfo, queryWrapper);
}
```

#### 带条件的分页查询

```java
public IPage<Book> getBooksByCategory(String category, int page, int size) {
    Page<Book> pageInfo = new Page<>(page, size);
    QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("deleted", 0).eq("category", category);
    return bookMapper.selectPage(pageInfo, queryWrapper);
}
```

#### 返回结果处理

```java
IPage<UserAccount> result = userService.getAllUsers(1, 10);

// 获取数据
List<UserAccount> users = result.getRecords();

// 获取分页信息
long total = result.getTotal();      // 总记录数
long pages = result.getPages();       // 总页数
long current = result.getCurrent();   // 当前页
long size = result.getSize();         // 每页大小

// 构建前端返回数据
Map<String, Object> response = new HashMap<>();
response.put("records", users);
response.put("total", total);
response.put("pages", pages);
response.put("current", current);
response.put("size", size);
```

### 7.3 前端分页参数传递

#### Controller层接收参数

```java
@GetMapping("/users")
public ResponseEntity<?> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String type) {

    IPage<UserAccount> result = userService.getUsers(page, size, keyword, type);

    Map<String, Object> response = new HashMap<>();
    response.put("code", 200);
    response.put("data", result.getRecords());
    response.put("total", result.getTotal());
    response.put("pages", result.getPages());

    return ResponseEntity.ok(response);
}
```

#### 前端请求示例

```javascript
// GET /api/users?page=1&size=10&keyword=张&type=STUDENT

async function fetchUsers() {
    const response = await fetch('/api/users?page=1&size=10&keyword=张');
    const data = await response.json();

    console.log('用户列表:', data.data);
    console.log('总数:', data.total);
    console.log('总页数:', data.pages);
}
```

## 8. 最佳实践和注意事项

### 8.1 性能优化建议

#### 1. 合理使用索引

确保查询条件字段上有适当的索引：

```sql
-- 为常用查询字段添加索引
CREATE INDEX idx_user_type ON user_account(user_type);
CREATE INDEX idx_book_status ON book(status, deleted);
CREATE INDEX idx_borrow_user_status ON borrow_record(user_id, status);
```

#### 2. 避免全表查询

```java
// ❌ 错误：查询所有数据
List<UserAccount> allUsers = userAccountMapper.selectList(null);

// ✅ 正确：添加必要条件
QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
wrapper.eq("deleted", 0);
List<UserAccount> users = userAccountMapper.selectList(wrapper);
```

#### 3. 分页查询必须加条件

```java
// ✅ 正确：分页查询配合条件
public IPage<Book> getAllBooks(int page, int size) {
    Page<Book> pageInfo = new Page<>(page, size);
    QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("deleted", 0);  // 重要！过滤已删除数据
    return bookMapper.selectPage(pageInfo, queryWrapper);
}
```

#### 4. 只查询需要的字段

```java
// ❌ 查询所有字段
Book book = bookMapper.selectById(id);

// ✅ 只查询需要的字段（使用QueryWrapper的select方法）
QueryWrapper<Book> wrapper = new QueryWrapper<>();
wrapper.select("id", "title", "author", "isbn").eq("id", id);
Book book = bookMapper.selectOne(wrapper);
```

#### 5. 批量操作优化

```java
// ✅ 使用批量插入
List<Book> books = Arrays.asList(book1, book2, book3);
for (Book book : books) {
    bookMapper.insert(book);
}

// 或者使用saveBatch（如果使用IService）
// this.saveBatch(books, 100); // 每100条一批
```

### 8.2 常见陷阱和解决方案

#### 陷阱1：忘记过滤已删除数据

**问题**：查询出已软删除的数据

**解决**：始终添加 `deleted = 0` 条件

```java
QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
wrapper.eq("deleted", 0);  // 必须添加！
```

#### 陷阱2：更新时间未设置

**问题**：更新操作后update_time字段不变

**解决**：每次更新前手动设置时间

```java
public boolean updateUser(UserAccount userAccount) {
    userAccount.setUpdateTime(new Date());  // 必须设置！
    int result = userAccountMapper.updateById(userAccount);
    return result > 0;
}
```

#### 陷阱3：QueryWrapper字符串拼写错误

**问题**：运行时才发现字段名错误

**解决**：使用LambdaQueryWrapper

```java
// ❌ 容易出错
wrapper.eq("userName", "admin");  // 应该是username

// ✅ 编译期检查
LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(UserAccount::getUsername, "admin");  // 编译时报错
```

#### 陷阱4：逻辑删除误用

**问题**：使用deleteById真正删除了数据

**解决**：手动实现软删除

```java
// ❌ 物理删除
userAccountMapper.deleteById(id);

// ✅ 软删除（本项目做法）
public boolean deleteUser(Long id) {
    UserAccount user = userAccountMapper.selectById(id);
    if (user != null) {
        user.setDeleted(1);
        user.setUpdateTime(new Date());
        return userAccountMapper.updateById(user) > 0;
    }
    return false;
}
```

#### 陷阱5：事务边界不当

**问题**：多个数据库操作不在同一事务中

**解决**：使用@Transactional注解

```java
@Transactional
public BorrowRecord borrowBook(Long userId, Long bookId, String barcode) {
    // 这些操作会在同一事务中
    borrowRecordMapper.insert(record);
    bookMapper.updateById(book);
    // 如果中间出错，所有操作回滚
}
```

### 8.3 代码规范建议

#### 1. 命名规范

```java
// Entity: 大驼峰
public class UserAccount { }

// Mapper: 实体名+Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> { }

// Service: 实体名+Service
@Service
public class UserService { }

// Controller: 实体名+Controller
@RestController
public class UserController { }
```

#### 2. 统一的返回格式

```java
Map<String, Object> response = new HashMap<>();
response.put("code", 200);
response.put("message", "操作成功");
response.put("data", data);
return ResponseEntity.ok(response);
```

#### 3. 异常处理

```java
try {
    // 业务操作
} catch (Exception e) {
    log.error("操作失败: {}", e.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put("code", 500);
    response.put("message", "操作失败: " + e.getMessage());
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

#### 4. 日志记录

```java
private static final Logger logger = LoggerFactory.getLogger(UserService.class);

logger.info("用户注册成功: username={}", username);
logger.warn("密码强度验证失败: {}", message);
logger.error("数据库操作失败: userId={}, error={}", userId, e.getMessage());
```

#### 5. 参数校验

```java
public UserAccount getUserByStudentId(String studentId) {
    if (studentId == null || studentId.isEmpty()) {
        return null;  // 或抛出异常
    }
    // 业务逻辑...
}
```

#### 6. 常量提取

```java
// 定义常量
private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
private static final int PASSWORD_LENGTH = 8;

// 字段映射表
private static final Map<String, String> SEARCH_FIELD_MAP = new HashMap<>();
static {
    SEARCH_FIELD_MAP.put("username", "username");
    SEARCH_FIELD_MAP.put("realName", "real_name");
    SEARCH_FIELD_MAP.put("userType", "user_type");
    // ...
}
```

## 总结

本系统完整地展示了如何在Spring Boot 3项目中集成和使用MyBatis-Plus 3.5.7：

1. **配置简洁**：仅需要一个Config类和几行application.properties配置
2. **开发高效**：继承BaseMapper即可获得完整的CRUD能力
3. **查询强大**：QueryWrapper/LambdaQueryWrapper支持复杂条件构建
4. **分页便捷**：配置一次分页插件，全局可用
5. **扩展灵活**：可结合@Select注解或XML自定义SQL
6. **架构清晰**：Controller -> Service -> Mapper 三层架构分明

遵循本文档的规范和最佳实践，可以保证代码的一致性、可维护性和性能。
