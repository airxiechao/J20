package com.airxiechao.j20.auth.api.rest.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResp {
    /**
     * 登录 Token
     */
    private String token;

    /**
     * 刷新 Token
     */
    private String refreshToken;
}
