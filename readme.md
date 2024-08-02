# TBS Framework

## 1. 基础引入

在项目的`pom.xml`文件中，添加以下依赖：

#### 1.基础框架依赖包含基础的插件和依赖，如lombok, spring-boot, spring-boot-starter-web等。

```xml

<parent>
    <groupId>tbs.framework</groupId>
    <artifactId>FrameFoundation</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
```

#### 2.基础属性（可选）

```xml

<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

#### 3.基础包的依赖包含接口和一些基础功能实现

```xml

<dependency>
    <groupId>tbs.framework</groupId>
    <artifactId>base</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 4.其他功能包

```xml

<dependencies>
    //权限验证包，包含权限验证功能、Controller拦截、控制器错误处理等
    <dependency>
        <groupId>tbs.framework</groupId>
        <artifactId>auth</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    //redis缓存包，主要包含对以redis为基础的缓存服务的功能实现和消息队列的功能实现
    <dependency>
        <groupId>tbs.framework</groupId>
        <artifactId>redis</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    //swagger包，主要包含对swagger文档生成和接口测试的功能实现
    <dependency>
        <groupId>tbs.framework</groupId>
        <artifactId>swagger</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    //sql包，主要包含对数据库操作的功能实现
    <dependency>
        <groupId>tbs.framework</groupId>
        <artifactId>sql</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    //xxlJob包，主要包含对分布式任务调度框架xxlJob的功能实现
    <dependency>
        <groupId>tbs.framework</groupId>
        <artifactId>xxlJob</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## 基础功能

### 1.多语言功能

#### 1.1 多语言功能介绍

多语言功能是指在系统中支持多种语言，用户可以根据自己的需求选择不同的语言进行操作。这个功能可以帮助系统更好地服务全球用户，提高用户体验。

#### 1.2 多语言功能快速使用

在项目中使用多语言功能非常简单，只需要在需要支持多语言的地方添加相应的语言文件即可。例如，在`src/main/resources/i18n`
目录下创建一个名为`i18n.properties`的文件等，如支持英文即创建`i18n_en.properties`文件。

#### 1.2.1 多语言功能配置

在`application.properties`文件中添加以下配置：

##### 1.2.1.1 设置文件夹和文件名前缀

```properties
spring.messages.basename={resource下的文件夹}/{文件名前缀}
```

##### 1.2.1.2 设置文件夹和文件名前缀

```properties
tbs.framework.local.type={获取语言环境的方式}
tbs.framework.local.value={获取语言环境的key值}
```

#### 1.2.2 在项目中使用多语言功能

标记全局导入多语言功能的注解

```java

@SpringBootApplication
@EnableMultilingual
public class DemoApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

实体中增加注解标记需要翻译的字段以及在调用处标记需要翻译的函数

```java
/**
 * 实体数据类
 */
@Data
public class TestModel {
    @TranslateField(args = CustomParameter.class)
    private String text = "TIME.NOW";

    public TestModel(String text) {
        this.text = text;
    }

}

/**
 * 被spring托管的类中的函数，需要翻译的函数，会对返回值进行翻译
 * @param text
 * @return
 */
@Translated
public TestModel testModel(String text) {
    return new TestModel(text);
}
```
