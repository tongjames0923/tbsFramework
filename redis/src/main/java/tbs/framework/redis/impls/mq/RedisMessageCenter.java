package tbs.framework.redis.impls.mq;

import tbs.framework.mq.center.AbstractListImplMessageCenter;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.redis.impls.mq.receiver.RedisMessageConnector;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class RedisMessageCenter extends AbstractListImplMessageCenter {


    @Resource
    RedisMessageConnector connector;

    @Override
    public IMessageConnector getConnector() {
        return connector;
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
