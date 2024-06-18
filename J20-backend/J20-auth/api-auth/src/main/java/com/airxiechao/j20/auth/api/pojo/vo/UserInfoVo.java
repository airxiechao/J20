package com.airxiechao.j20.auth.api.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户信息显示对象
 */
@Data
@AllArgsConstructor
public class UserInfoVo {
    private String userId;
    private String username;
}
