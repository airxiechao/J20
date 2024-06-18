package com.airxiechao.j20.auth.api.rest.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterParam {
    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;
}