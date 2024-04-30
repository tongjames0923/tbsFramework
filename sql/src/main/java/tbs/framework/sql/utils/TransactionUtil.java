package tbs.framework.sql.utils;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

/**
 * 事务工具
 * @author abstergo
 */
public class TransactionUtil {

    private PlatformTransactionManager transactionManager;

    private ILogger logger;

    private static TransactionUtil transactionUtil;

    public static TransactionUtil getInstance() {
        return transactionUtil;
    }

    /**
     * 通过构造器注入事务管理器
     */
    public TransactionUtil(PlatformTransactionManager transactionManager, LogUtil l) {
        this.transactionManager = transactionManager;
        logger = l.getLogger(TransactionUtil.class.getName());
        if (null == transactionUtil) {
            transactionUtil = this;
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
        } catch (RuntimeException e) {
            // 如果出现异常，回滚事务
            logger.warn("transaction roll back");
            transactionManager.rollback(status);
            throw e;
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