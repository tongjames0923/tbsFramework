package tbs.framework.auth.interfaces.impls;

import tbs.framework.auth.exceptions.UserModelNotFoundException;
import tbs.framework.auth.interfaces.ITokenParser;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.TokenModel;
import tbs.framework.auth.model.UserModel;
import tbs.framework.auth.properties.AuthProperty;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 内置用户数据解析器，需要配置IUserModelPicker的Bean配合使用
 *
 * @author abstergo
 */
public class UserModelTokenParser implements ITokenParser {
    @Resource
    AuthProperty authProperty;

    @Resource
    IUserModelPicker userModelPicker;

    @Override
    public void parseToken(TokenModel tokenModel, RuntimeData data) throws RuntimeException {
        UserModel model = userModelPicker.getUserModel(tokenModel.getToken());
        if (Objects.isNull(model)) {
            throw new UserModelNotFoundException("用户数据不存在");
        }
        data.setUserModel(model);
    }

    @Override
    public boolean support(String field) {
        return Objects.equals(field, authProperty.getUserModelTokenField());
    }
}
