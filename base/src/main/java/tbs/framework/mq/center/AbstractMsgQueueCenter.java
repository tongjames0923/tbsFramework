//package tbs.framework.mq.center;
//
//import org.springframework.context.annotation.Lazy;
//import tbs.framework.base.log.ILogger;
//import tbs.framework.base.utils.LogUtil;
//import tbs.framework.mq.consumer.IMessageConsumer;
//import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
//import tbs.framework.mq.event.IMessageQueueEvents;
//import tbs.framework.mq.listener.impls.BaseQueueListener;
//import tbs.framework.mq.message.IMessage;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutorService;
//
///**
// * @author abstergo
// */
//public abstract class AbstractMsgQueueCenter extends AbstractMessageCenter {
//
//
//    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";
//
//    ILogger logger;
//
//    private ILogger getLogger() {
//        if (logger == null) {
//            logger = LogUtil.getInstance().getLogger(this.getClass().getName());
//        }
//        return logger;
//    }
//
//    @Resource
//    @Lazy
//    List<IMessageConsumer> consumerList;
//
//    IMessageConsumerManager messageConsumerManager;
//
//    IMessageQueueEvents messageQueueEvents;
//
//    ExecutorService service;
//
//    /**
//     * 获取队列监听器
//     *
//     * @return
//     */
//    protected abstract BaseQueueListener getQueueListener();
//
//    @Override
//    protected Optional<IMessageConsumerManager> getMessageConsumerManager() {
//        return Optional.ofNullable(messageConsumerManager);
//    }
//
//    /**
//     * 监听间隔
//     *
//     * @return
//     */
//    protected abstract long listenSpan();
//
//    @Override
//    protected Optional<IMessageQueueEvents> getMessageQueueEvents() {
//        return Optional.ofNullable(messageQueueEvents);
//    }
//
//    public AbstractMsgQueueCenter(IMessageConsumerManager messageConsumerManager,
//        IMessageQueueEvents messageQueueEvents, ExecutorService service) {
//        this.messageConsumerManager = messageConsumerManager;
//        this.messageQueueEvents = messageQueueEvents;
//        this.service = service;
//    }
//
////    @Override
////    protected void sendMessage(IMessage message) {
////        getQueueListener().getQueue().insert(message);
////    }
//
//    @Override
//    protected void centerStartToWork() {
//        for (IMessageConsumer consumer : consumerList) {
//            getMessageConsumerManager().ifPresent((m) -> {
//                m.setMessageConsumer(consumer);
//            });
//        }
//        service.execute(() -> {
//            long sp = 0;
//            while (isStart()) {
//                sp = listenSpan();
//                if (sp > 0) {
//                    try {
//                        Thread.currentThread().join(sp);
//                    } catch (Exception e) {
//                        getLogger().error(e, "listen span error:{}", e.getMessage());
//                    }
//                }
//                getQueueListener().listen(this);
//            }
//        });
//
//    }
//
//    @Override
//    protected void centerStopToWork() {
//        service.shutdown();
//    }
//}
