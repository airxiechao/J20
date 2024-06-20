package com.airxiechao.j20.common.security;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.jwt.JwtUtil;
import com.airxiechao.j20.common.util.RestUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * 用户身份提取过滤器
 */
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    private static final String authHeader = "Authorization";
    private static final String authPrefix = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String header = request.getHeader(authHeader);

        // 不验证 public 路径
        if(!uri.startsWith("/api/public/") && StringUtils.isNotBlank(header) && header.startsWith(authPrefix)){
            // 从头部中取出 Token
            String token = header.substring(authPrefix.length()).strip();

            try {
                // 解析 Token
                Claims claims = JwtUtil.parseToken(token);
                String username = claims.get("username", String.class);

                if (StringUtils.isNotBlank(username)) {
                    // 创建用户身份
                    User user = new User(username, "", Collections.emptyList());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, null);

                    // 添加用户身份附加信息
                    authenticationToken.setDetails(new AuthenticationTokenDetail());

                    // 设置用户身份
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }catch (ExpiredJwtException e){
                // Token 过期
                log.error("Token过期");
                Resp resp = new Resp(ConstRespCode.ERROR_TOKEN_EXPIRED,  "Token过期", null);
                RestUtil.sendResp(response, HttpServletResponse.SC_OK, resp);
                return;
            }catch (Exception e){
                // Token 无效
                log.error("Token无效");
                Resp resp = new Resp(ConstRespCode.ERROR_TOKEN_INVALID,  "Token无效", null);
                RestUtil.sendResp(response, HttpServletResponse.SC_OK, resp);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
