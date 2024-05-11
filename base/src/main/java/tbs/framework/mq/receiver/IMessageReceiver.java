package tbs.framework.mq.receiver;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import tbs.framework.mq.message.IMessage;

/**
 * @author abstergo
 */
public interface IMessageReceiver extends InitializingBean, DisposableBean {
    /**
     * 提供消息
     *
     * @return 接收到的消息¬
     */
    IMessage receive();

    /**
     * 获取到消息的调用 可能消息来自外部
     *
     * @param message 信息
     */
    void pull(IMessage message);

    /**
     * 查看以下方法是否可用
     *
     * @return
     * @see #pull(IMessage)
     */
    boolean avaliablePull();

    /**
     * 查看以下方法是否可用
     *
     * @return
     * @see #receive()
     */
    boolean avaliableReceive();

}
