package com.airxiechao.j20.auth.api.rest.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户修改密码请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordParam {
    /**
     * 旧密码
     */
    @NotBlank
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank
    private String newPassword;
}