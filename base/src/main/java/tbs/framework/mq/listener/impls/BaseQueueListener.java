//package tbs.framework.mq.listener.impls;
//
//import tbs.framework.mq.center.AbstractMessageCenter;
//import tbs.framework.mq.message.IMessage;
//import tbs.framework.mq.queue.IMessageQueue;
//import tbs.framework.mq.receiver.IMessageReceiver;
//
///**
// * @author abstergo
// */
//public abstract class BaseQueueListener {
//
//    private static final String LOCK_NAME = "SIMPLE_MSG_CENTER_LOCK";
//
//    /**
//     * 获取消息队列
//     *
//     * @return
//     */
//    public abstract IMessageQueue getQueue();
//
//    //    IMessageReceiver receiver = new IMessageReceiver() {
//    //        @Override
//    //        public IMessage receive() {
//    //            return getQueue().getNext();
//    //        }
//    //    };
//
//    public IMessageReceiver getMessageReceiver() {
//        return null;
//    }
//
//    public void listen(AbstractMessageCenter messageCenter) {
//        IMessage message = null;
//        int r = 0;
//        while (true) {
//            try {
//                message = getMessageReceiver().receive();
//                break;
//            } catch (Exception e) {
//                message = null;
//                if (!messageCenter.errorOnRecive(r++, e)) {
//                    break;
//                }
//            }
//        }
//        if (message == null) {
//            Thread.yield();
//            return;
//        }
//        messageCenter.messageArrived(message, , );
//        messageCenter.consumeMessage(message);
//
//    }
//}
