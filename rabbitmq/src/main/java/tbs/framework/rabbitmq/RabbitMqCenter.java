package tbs.framework.rabbitmq;

import tbs.framework.mq.center.AbstractListImplMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.rabbitmq.connectors.AbstractRabbitMqConnector;
import tbs.framework.rabbitmq.properties.RabbitMqProperty;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;

/**
 * @author Abstergo
 */
public class RabbitMqCenter extends AbstractListImplMessageCenter {

    @Resource
    RabbitMqProperty mqProperty;
    @Resource
    private AbstractRabbitMqConnector abstractRabbitMqConnector;

    @Override
    public IMessageConnector getConnector() {
        return abstractRabbitMqConnector;
    }

    @Override
    public void startUp() throws RuntimeException {
        super.startUp();
        if (!mqProperty.isPassiveReception()) {
            ThreadUtil.getInstance().runCollectionInBackground(() -> {
                listen();
            });
        }

    }

    @Override
    protected void centerStopToWork() {
    }
}
