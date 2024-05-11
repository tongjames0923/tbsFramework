package tbs.framework.mq;

import org.springframework.beans.factory.DisposableBean;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

public interface IMessageConnector extends DisposableBean {
    AbstractMessageCenter getMessageCenter();

    List<IMessageReceiver> getReceivers();

    public void afterPropertiesSet() throws Exception;

}
