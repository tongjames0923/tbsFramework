package tbs.framework.auth.interfaces.debounce;

import tbs.framework.auth.exceptions.DebounceException;
import tbs.framework.auth.interfaces.ITokenGenerator;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;

/**
 * 抽象的令牌去抖接口，用于生成和消费令牌
 *
 * @param <T> 令牌因子类型
 * @author abstergo
 */
public abstract class AbstractTokenDebounce<T> implements IDebounce, ITokenGenerator<T> {

    /**
     * 生成令牌
     *
     * @param fac 令牌因子
     * @return 生成的令牌
     */
    protected abstract String genToken(T fac);

    /**
     * 应用令牌
     *
     * @param fac   令牌因子
     * @param token 令牌
     */
    protected abstract void tokenApply(String token, T fac);

    /**
     * 消费令牌
     *
     * @param token 令牌因子
     * @throws DebounceException 消费令牌失败时抛出异常
     */
    protected abstract void consumeToken(T token) throws DebounceException;

    /**
     * 移除令牌
     *
     * @param token 令牌因子
     */
    protected abstract void tokenRemove(T token);

    /**
     * 将用户模型、方法、参数转换为令牌因子
     *
     * @param user   用户模型
     * @param method 方法
     * @param args   参数
     * @return 令牌因子
     */
    protected abstract T toFactor(UserModel user, Method method, Object[] args);

    @Override
    public String generateToken(T tokenFactor) {
        String token = genToken(tokenFactor);
        tokenApply(token, tokenFactor);
        return token;
    }

    @Override
    public void debounce(String url, UserModel user, Method method, Object target, Object[] args)
        throws DebounceException {
        T tokenFactor = toFactor(user, method, args);
        if (tokenFactor == null) {
            throw new DebounceException("不存在有效的令牌");
        }
        try {
            consumeToken(tokenFactor);
        } catch (DebounceException de) {
            throw de;
        } catch (Exception e) {
            throw new DebounceException(String.format("token consume failed. msg:%s", e.getMessage()));
        } finally {
            tokenRemove(tokenFactor);
        }
    }
}

