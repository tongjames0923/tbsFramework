package tbs.framework.rabbitmq;

import tbs.framework.mq.center.AbstractListImplMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.rabbitmq.connectors.RabbitMqManulReceiveConnector;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;

/**
 * @author Abstergo
 */
public class RabbitMqCenter extends AbstractListImplMessageCenter {

    @Resource
    private RabbitMqManulReceiveConnector rabbitMqManulReceiveConnector;

    @Override
    public IMessageConnector getConnector() {
        return rabbitMqManulReceiveConnector;
    }

    @Override
    public void startUp() throws RuntimeException {
        super.startUp();
        ThreadUtil.getInstance().runCollectionInBackground(() -> {
            listen();
        });
    }

    @Override
    protected void centerStopToWork() {
    }
}
