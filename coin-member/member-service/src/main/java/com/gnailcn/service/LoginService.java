package com.gnailcn.service;

import com.gnailcn.model.LoginForm;
import com.gnailcn.model.LoginUser;

public interface LoginService {
    /**
     * 会员的登录
     * @param loginForm
     * @return
     */

    LoginUser login(LoginForm loginForm);
}
