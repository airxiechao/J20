package com.airxiechao.j20.common.jwt;

import com.airxiechao.j20.common.util.ApplicationContextUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 辅助类
 */
@Slf4j
public class JwtUtil {

    /**
     * 创建登录 Token
     * @param username 用户名
     * @return 登录 Token
     */
    public static String createToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts
                .builder()
                .claims(claims)
                .issuedAt(new Date(nowMillis))
                .signWith(getSecretKey());

        long expiration = getExpiration();
        if (expiration >= 0) {
            long expMillis = nowMillis + expiration;
            builder.expiration(new Date(expMillis));
        }

        return builder.compact();
    }

    /**
     * 创建刷新 Token
     * @param tokenHash 登录Token 的 Hash
     * @param username 用户名
     * @param passwordHash 用户密码的 Hash
     * @return 刷新 Token
     */
    public static String createRefreshToken(String tokenHash, String username, String passwordHash) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenHash", tokenHash);
        claims.put("username", username);
        claims.put("passwordHash", passwordHash);

        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts
                .builder()
                .claims(claims)
                .issuedAt(new Date(nowMillis))
                .signWith(getSecretKey());

        long expiration = getExpiration() * 2;
        if (expiration >= 0) {
            long expMillis = nowMillis + expiration;
            builder.expiration(new Date(expMillis));
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     * @param token Token
     * @return Token 内容
     */
    public static Claims parseToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    /**
     * 获取加密密钥
     * @return 密钥
     */
    private static SecretKey getSecretKey() {
        JwtConfig jwtConfig = ApplicationContextUtil.getContext().getBean(JwtConfig.class);
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取过期时间
     * @return 过期毫秒数
     */
    private static Long getExpiration() {
        JwtConfig jwtConfig = ApplicationContextUtil.getContext().getBean(JwtConfig.class);
        return jwtConfig.getExpiration();
    }

    /**
     * 随机生成一个加密密钥
     * @return 密钥
     */
    public static String randomKey(){
        SecretKey key = Jwts.SIG.HS256.key().build();
        return Encoders.BASE64.encode(key.getEncoded());
    }
}
