/**
 * ITokenParser 接口用于解析令牌（Token）。
 */
package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;

/**
 * ITokenParser 接口用于解析令牌（Token）。
 */
public interface ITokenParser {

    /**
     * 解析令牌。
     *
     * @param tokenModel 令牌模型
     * @param data       运行时数据
     * @throws RuntimeException 如果解析过程中发生错误
     */
    public void parseToken(TokenModel tokenModel, RuntimeData data) throws RuntimeException;

    /**
     * 判断是否支持解析指定字段。
     *
     * @param field 字段名
     * @return 如果支持解析该字段，则返回 true，否则返回 false
     */
    public boolean support(String field);
}

