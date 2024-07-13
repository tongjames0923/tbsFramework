package tbs.framework.rabbitmq.properties;

import lombok.Data;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
@ConfigurationProperties("tbs.framework.mq.rabbit")
public class RabbitMqProperty {
    private List<String> queues = new LinkedList<>(Arrays.asList("DEAFULT_QUEUE"));

    private String exchangeName = "DEFAULT";

    private Class<? extends Exchange> exchangeType = TopicExchange.class;

    private boolean rebuildExchangeAndQueue = false;

    private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;
    private long receiveTimeout = 3000;

}
