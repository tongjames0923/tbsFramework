package tbs.framework.mq.connector;

import org.springframework.beans.factory.DisposableBean;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

/**
 * @author Abstergo
 */
public interface IMessageConnector extends DisposableBean {

    AbstractMessageCenter getMessageCenter();

    List<IMessageReceiver> getReceivers();

    public void afterPropertiesSet() throws Exception;

}
