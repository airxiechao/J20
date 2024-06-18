package com.airxiechao.j20.auth.rest;

import com.airxiechao.j20.auth.api.pojo.User;
import com.airxiechao.j20.auth.api.pojo.vo.UserInfoVo;
import com.airxiechao.j20.auth.api.rest.IUserController;
import com.airxiechao.j20.auth.api.rest.param.UserLoginParam;
import com.airxiechao.j20.auth.api.rest.param.UserRefreshTokenParam;
import com.airxiechao.j20.auth.api.rest.param.UserRegisterParam;
import com.airxiechao.j20.auth.api.rest.param.UserUpdatePasswordParam;
import com.airxiechao.j20.auth.api.rest.resp.UserLoginResp;
import com.airxiechao.j20.auth.api.service.IUserService;
import com.airxiechao.j20.auth.service.UserService;
import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.jwt.JwtUtil;
import com.airxiechao.j20.common.util.CodecUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.security.Principal;

@Slf4j
@RestController
@RequestScope
public class UserController implements IUserController {
    private static final String authPrefix = "Bearer ";

    private IUserService userService = new UserService();

    @Override
    public Resp register(UserRegisterParam param) {
        log.info("创建用户[{}]", param.getUsername());

        try {
            if(userService.exists(param.getUsername())){
                throw new Exception("用户名已存在");
            }

            User user = new User();
            user.setUsername(param.getUsername());
            user.setPassword(param.getPassword());

            userService.add(user);
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("创建用户[{}]发生错误：{}", param.getUsername(), e.getMessage());
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<UserLoginResp> login(UserLoginParam param) {
        log.info("用户[{}]登录", param.getUsername());

        try {
            if(!userService.validatePassword(param.getUsername(), param.getPassword())){
                throw new Exception("用户名或密码错误");
            }

            User user;
            try {
                user = userService.getByUsername(param.getUsername());
            }catch (NotFoundException e){
                throw new Exception("用户名或密码错误");
            }

            String token = JwtUtil.createToken(user.getUsername());
            String tokenHash = CodecUtil.md5(token);

            String password = userService.getByUsername(user.getUsername()).getPassword();
            String passwordHash = CodecUtil.md5(password);

            String refreshToken = JwtUtil.createRefreshToken(tokenHash, param.getUsername(), passwordHash);
            UserLoginResp loginResp = new UserLoginResp(token, refreshToken);

            return new Resp<>(ConstRespCode.OK, null, loginResp);
        } catch (Exception e) {
            log.error("用户[{}]登录发生错误：{}", param.getUsername(), e.getMessage());
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<UserLoginResp> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, UserRefreshTokenParam param) {
        log.info("用户刷新Token");

        try {
            if(StringUtils.isBlank(authorization) || !authorization.startsWith(authPrefix)){
                throw new Exception("原Token不存在");
            }

            String token = authorization.substring(authPrefix.length()).strip();

            String tokenHash, username, passwordHash;
            try {
                Claims claims = JwtUtil.parseToken(param.getRefreshToken());
                tokenHash = claims.get("tokenHash", String.class);
                username = claims.get("username", String.class);
                passwordHash = claims.get("passwordHash", String.class);
            }catch (ExpiredJwtException e){
                throw new Exception("刷新Token过期");
            }catch (Exception e){
                throw new Exception("刷新Token无效");
            }

            // 检查 tokenHash
            if(!CodecUtil.md5(token).equals(tokenHash)){
                throw new Exception("刷新Token无效");
            }

            // 检查 passwordHash
            String currentPassword = userService.getByUsername(username).getPassword();
            String currentPasswordHash = CodecUtil.md5(currentPassword);
            if(!currentPasswordHash.equals(passwordHash)){
                throw new Exception("刷新Token无效");
            }

            String currentToken = JwtUtil.createToken(username);
            String currentTokenHash = CodecUtil.md5(currentToken);
            String refreshToken = JwtUtil.createRefreshToken(currentTokenHash, username, currentPasswordHash);
            UserLoginResp loginResp = new UserLoginResp(currentToken, refreshToken);

            log.info("用户[{}]刷新Token完成", username);

            return new Resp<>(ConstRespCode.OK, null, loginResp);
        }catch (Exception e){
            log.error("用户刷新Token发生错误：{}", e.getMessage());
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<UserInfoVo> getUserInfo(Principal principal) {
        log.info("用户[{}]获取用户信息", principal.getName());

        try {

            User user = userService.getByUsername(principal.getName());
            UserInfoVo userInfoVo = new UserInfoVo(user.getId(), user.getUsername());

            return new Resp<>(ConstRespCode.OK, null, userInfoVo);
        } catch (Exception e) {
            log.error("用户[{}]获取用户信息发生错误：{}", principal.getName(), e.getMessage());
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp changePassword(Principal principal, UserUpdatePasswordParam param) {
        log.info("用户[{}]修改密码", principal.getName());

        try {
            if(!userService.validatePassword(principal.getName(), param.getOldPassword())){
                throw new Exception("旧密码不正确");
            }

            userService.updatePassword(principal.getName(), param.getNewPassword());
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("用户[{}]修改密码发生错误：{}", principal.getName(), e.getMessage());
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

}
