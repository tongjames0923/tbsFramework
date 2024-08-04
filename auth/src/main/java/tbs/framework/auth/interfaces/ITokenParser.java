package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;

public interface ITokenParser {
    public void parseToken(TokenModel tokenModel, RuntimeData data) throws RuntimeException;

    public boolean support(String field);
}
