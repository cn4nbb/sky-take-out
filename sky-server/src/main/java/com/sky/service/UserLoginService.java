package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserLoginService {

    /**
     * 用户微信登陆
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);
}
