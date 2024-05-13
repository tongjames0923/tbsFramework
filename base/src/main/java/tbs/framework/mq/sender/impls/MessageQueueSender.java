package tbs.framework.mq.sender.impls;

import tbs.framework.mq.center.impls.MessageQueueCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;

import java.util.regex.Pattern;

/**
 * @author Abstergo
 */
public class MessageQueueSender implements IMessagePublisher {

    private MessageQueueCenter center;

    public MessageQueueSender(MessageQueueCenter center) {
        this.center = center;
    }

    @Override
    public void publishAll(IMessage... message) {
        center.getReceivers().forEach(r -> {
            for (IMessage m : message) {
                for (String t : r.acceptTopics()) {
                    Pattern pattern = Pattern.compile(t);
                    if (pattern.matcher(m.getTopic()).matches()) {
                        r.pull(m);
                        break;
                    }

                }
            }
        });
    }
}
