package com.airxiechao.j20.auth.api.service;

import com.airxiechao.j20.auth.api.pojo.User;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;

/**
 * 用户服务接口
 */
public interface IUserService {
    /**
     * 用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean exists(String username);

    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return 用户
     * @throws NotFoundException 找不到用户异常
     */
    User getByUsername(String username) throws NotFoundException;

    /**
     * 验证密码
     * @param username 用户名
     * @param password 密码
     * @return 是否一致
     */
    boolean validatePassword(String username, String password);

    /**
     * 添加用户
     * @param user 用户
     * @return 新增用户
     */
    User add(User user);

    /**
     * 修改密码
     * @param username 用户名
     * @param password 密码
     * @return 修改后的用户
     * @throws NotFoundException 找不到用户异常
     */
    User updatePassword(String username, String password) throws NotFoundException;
}
