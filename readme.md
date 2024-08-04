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

### 2.权限验证功能

#### 2.1 权限验证功能介绍

权限验证功能是指在系统中对用户的操作进行权限验证，确保用户只能访问自己有权限访问的资源。这个功能可以帮助系统更好地保护用户的数据和隐私，提高系统的安全性。

#### 2.2 权限验证功能快速使用

##### 1. 添加依赖

在项目的`pom.xml`文件中添加以下依赖：

```xml

<dependency>
    <groupId>tbs.framework</groupId>
    <artifactId>auth</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

##### 2. 配置权限验证框架

在Spring Boot的主类上添加`@EnableTbsAuth`注解，以启用权限验证框架。

```java
import tbs.framework.auth.annotations.EnableTbsAuth;

@SpringBootApplication
@EnableTbsAuth
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

##### 3. 获取token

`IRequestTokenPicker`：用于从HTTP请求中获取token。
默认提供的`HeaderRequestTokenPicker`类，从HTTP请求头中获取token、`CookieRequestTokenPicker`
类，从HTTP请求的cookie中获取token、`ParameterRequestTokenPicker`类，从HTTP请求的参数中获取token。

请在配置文件中配置token获取方式：

```properties
tbs.framework.auth.tokenPicker={获取token的方式}
```

`tbs.framwork.auth.userModelTokenField`字段用于配置系统默认自带的`UserModelTokenParser`类中获取用户权限的字段。
配置文件中`unForcedTokenFields`和`tokenFields`
字段的区别在于是否需要强制检查token，如果需要强制检查token，则需要在配置文件中配置`tokenFields`字段，否则不需要配置。
即若token检查不通过，不会直接报错。

##### 4. 解析Token

通过实现`ITokenParser`接口来解析Token。例如，在`UserModelTokenParser`类中，通过`ITokenParser`接口的`parseToken`方法来解析Token。

```java
public class UserModelTokenParser implements ITokenParser {

    // ...

    @Override
    public void parseToken(TokenModel tokenModel, RuntimeData data) {
        // ...
    }

    @Override
    public boolean support(String field) {
        return true;
    }
}
```

默认自带的`UserModelTokenParser`类实现了`ITokenParser`接口，通过`IUserModelPicker`接口获取用户权限。

在使用自带的`UserModelTokenParser`，需要提供通过实现`IUserModelPicker`接口的Bean来获取用户权限。

如有更多需求和功能可以自定义实现`ITokenParser`接口，该接口用于处理接收到的Token。

##### 5. 权限验证

在需要进行权限验证的地方，使用`@PermissionValidated`注解来标记需要验证的方法。例如，在`MyController`
类中，通过`@PermissionValidated`注解来标记需要验证的方法。

```java
import tbs.framework.auth.annotations.PermissionValidated;

@RestController
public class MyController {

    @PermissionValidated
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
```

##### 6. 异常处理

在权限验证过程中，如果出现异常，可以通过实现`IErrorHandler`接口来处理异常。例如，在`SimpleLogErrorHandler`
类中，通过`IErrorHandler`接口的`handleError`方法来处理异常。

```java
public class SimpleLogErrorHandler implements IErrorHandler {

    @Override
    public void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 处理异常
    }
}
```

通过以上步骤，可以快速搭建一个基于Spring框架的权限验证框架。在需要进行权限验证的地方，使用`@PermissionValidated`
注解来标记需要验证的方法。在权限验证过程中，通过`IUserModelPicker`和`ITokenParser`
接口的实现类来获取用户权限和解析Token。最后，通过`IErrorHandler`接口的实现类来处理权限验证过程中出现的异常。


