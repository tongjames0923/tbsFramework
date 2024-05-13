package tbs.framework.mq.connector;

import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

/**
 * @author Abstergo
 */
public interface IMessageConnector {

    void factoryMessageReceivers(List<IMessageReceiver> receivers);


}
