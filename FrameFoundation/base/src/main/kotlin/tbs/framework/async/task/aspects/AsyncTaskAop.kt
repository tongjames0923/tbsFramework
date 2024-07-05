package tbs.framework.async.task.aspects

import cn.hutool.extra.spring.SpringUtil
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import tbs.framework.async.task.annotations.AsyncTaskId
import tbs.framework.async.task.annotations.AsyncWithCallback
import tbs.framework.base.model.AsyncReceipt
import tbs.framework.utils.ThreadUtil
import tbs.framework.utils.ThreadUtil.IReceiptBroker
import java.time.LocalDateTime
import java.util.*

@Aspect
class AsyncTaskAop {

    @Pointcut("@annotation(tbs.framework.async.task.annotations.AsyncWithCallback)")
    public fun pt() {
    }

    @Around("pt()")
    @Throws(Throwable::class)
    public fun asyncTaskCall(joinPoint: ProceedingJoinPoint): Any? {
        val j = joinPoint.signature as MethodSignature;
        val anno: AsyncWithCallback = j.method.getDeclaredAnnotation(AsyncWithCallback::class.java);
        val callback: IReceiptBroker = SpringUtil.getBean(anno.callbackBean)
        var index = 0;
        var flag = false
        var uid = ""
        for (p in j.method.parameterAnnotations) {
            for (i in p) {
                if (i is AsyncTaskId) {
                    if (joinPoint.args[index] != null) {
                        uid = joinPoint.args[index].toString()
                    }
                    flag = true
                    break
                }
            }
            if (flag) {
                break
            }
            index++

        }
        ThreadUtil.getInstance().runCollectionInBackground(Runnable {
            val beg = LocalDateTime.now();
            val result: Any? = joinPoint.proceed();
            val ed = LocalDateTime.now();
            callback.submitReceipt(AsyncReceipt(uid, beg, ed, joinPoint.args.toList(), result, j.method));
        })
        return null
    }
}