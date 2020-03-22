package com.changgou.oauth.service;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service
 * @version 1.0
 * @date 2020/1/17
 */
public interface LoginService {

    /**
     * 模拟psotman发送请求 申请令牌
     * @param username  登录用户名
     * @param password  登录密码
     * @param grantType  授权的类型
     * @param client_id  申请令牌的客户端（硬编码）
     * @param client_secret  申请令牌的客户端秘钥(硬编码)
     * @return
     */
    Map<String,Object> login(String username, String password, String grantType, String client_id, String client_secret);
}
