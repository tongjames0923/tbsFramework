package tbs.framework.swagger.annotations;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tbs.framework.swagger.config.SwaggerConfig;
import tbs.framework.swagger.properties.SwaggerProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SwaggerConfig.class)
@EnableConfigurationProperties({SwaggerProperty.class})
@EnableWebMvc
@EnableKnife4j //启动swagger
public @interface EnableTbsSwagger {
}
