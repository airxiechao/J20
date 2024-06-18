package com.airxiechao.j20.auth.api.rest;

import com.airxiechao.j20.auth.api.pojo.vo.UserInfoVo;
import com.airxiechao.j20.auth.api.rest.param.UserLoginParam;
import com.airxiechao.j20.auth.api.rest.param.UserRefreshTokenParam;
import com.airxiechao.j20.auth.api.rest.param.UserRegisterParam;
import com.airxiechao.j20.auth.api.rest.param.UserUpdatePasswordParam;
import com.airxiechao.j20.auth.api.rest.resp.UserLoginResp;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;
import java.security.Principal;

/**
 * 用户请求接口
 */
public interface IUserController {

    /**
     * 注册
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/public/user/register")
    Resp register(@RequestBody @Valid UserRegisterParam param);

    /**
     * 登录
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/public/user/login")
    Resp<UserLoginResp> login(@RequestBody @Valid UserLoginParam param);

    /**
     * 刷新 Token
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/public/user/token/refresh")
    Resp<UserLoginResp> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody @Valid UserRefreshTokenParam param);

    /**
     * 获取用户信息
     * @param principal 登录身份凭证
     * @return 响应
     */
    @PostMapping("/api/user/info")
    Resp<UserInfoVo> getUserInfo(Principal principal);

    /**
     * 修改密码
     * @param principal 登录身份凭证
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/user/password/change")
    Resp changePassword(Principal principal, @RequestBody @Valid UserUpdatePasswordParam param);

}
