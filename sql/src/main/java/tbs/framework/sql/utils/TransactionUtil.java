package tbs.framework.sql.utils;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

/**
 * 事务工具类
 *
 * @author abstergo
 */
public class TransactionUtil {

    private final PlatformTransactionManager transactionManager;

    @AutoLogger
    private  ILogger logger;

    private static TransactionUtil transactionUtil;

    public static TransactionUtil getInstance() {
        return transactionUtil;
    }

    /**
     * 通过构造器注入事务管理器
     */
    public TransactionUtil(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        if (null == transactionUtil) {
            synchronized (TransactionUtil.class) {
                if (transactionUtil == null) {
                    transactionUtil = this;
                }
            }
        }
    }

    /**
     * 执行事务的方法，允许用户传入自定义的事务参数
     *
     * @param businessLogic         业务逻辑
     * @param transactionAttributes 事务参数
     */
    public void executeInTransaction(TransactionDefinition transactionAttributes, Runnable businessLogic) {
        TransactionStatus status = null;
        try {
            // 创建事务定义对象，这里使用用户提供的参数
            DefaultTransactionDefinition def = new DefaultTransactionDefinition(transactionAttributes);

            // 开启事务
            status = transactionManager.getTransaction(def);

            // 执行业务逻辑
            businessLogic.run();

            // 如果业务逻辑执行成功，提交事务
            transactionManager.commit(status);
        } catch (Exception e) { // 捕获所有异常
            // 如果出现异常，回滚事务
            logger.error(e, "Transaction rollback due to exception: " + e.getMessage());
            if (status != null) {
                transactionManager.rollback(status);
            }
            throw e;
        } finally {
            // 确保事务状态被清理
            if (status != null && !status.isCompleted()) {
                transactionManager.rollback(status);
                logger.warn("Transaction was not completed properly, rolled back.");
            }
        }
    }

    /**
     * 执行事务的方法，允许用户传入Propagation的事务参数
     *
     * @param propagationBehavior
     * @param businessLogic
     */
    public void executeTransaction(int propagationBehavior, Runnable businessLogic) {
        executeInTransaction(new DefaultTransactionDefinition(propagationBehavior), businessLogic);
    }
}
