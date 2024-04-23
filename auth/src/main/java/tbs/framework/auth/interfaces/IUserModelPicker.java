package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.UserModel;

public interface IUserModelPicker {
    UserModel getUserModel(String token);
}
