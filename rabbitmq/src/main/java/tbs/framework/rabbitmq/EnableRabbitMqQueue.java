package tbs.framework.rabbitmq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.rabbitmq.config.RabbitMqConfig;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@EnableRabbit
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RabbitMqConfig.class)
@EnableConfigurationProperties({RabbitMqProperty.class})
public @interface EnableRabbitMqQueue {
}
