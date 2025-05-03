package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserLoginMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserLoginService;
import com.sky.utils.HttpClientUtil;
import io.swagger.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    public static final String WX_URL = "https://api.weixin.qq.com/sns/jscode2session";
    public static final String GRANT_TYPE = "authorization_code";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserLoginMapper userLoginMapper;

    /**
     * 调用微信服务接口获取openId
     * @param code
     * @return
     */
    private String getOpenId(String code){
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type",GRANT_TYPE);

        String getResult = HttpClientUtil.doGet(WX_URL, map);

        //转换为JsonObject
        JSONObject json = JSON.parseObject(getResult);

        return json.getString("openid");
    }

    /**
     * 用户微信登陆
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //获取openId
        String openId = getOpenId(userLoginDTO.getCode());

        //判断openId是否获取成功，否则抛出异常
        if (openId == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //查询User表中是否存在该openId
        User user = userLoginMapper.getByOpenId(openId);

        //如果不存在，则自动注册
        if (user == null){
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userLoginMapper.insert(user);
        }

        return user;
    }
}
