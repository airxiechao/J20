package com.airxiechao.j20.auth.api.rest.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户刷新 Token 请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRefreshTokenParam {
    /**
     * 刷新 Token
     */
    @NotBlank
    private String refreshToken;
}