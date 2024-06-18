package com.airxiechao.j20.common.security;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.util.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户身份验证配置
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    /**
     * 配置用户身份提取过滤器
     * @return 用户身份提取过滤器
     */
    @Bean
    public SecurityFilter jwtAuthenticationFilter(){
        return new SecurityFilter();
    }

    /**
     * 配置过滤器链条
     * @param http 配置工厂
     * @return 配置结果
     * @throws Exception 配置发生错误
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated()
                ).exceptionHandling(auth -> auth.authenticationEntryPoint(
                        (request, response, ex) -> {
                            // 用户身份异常
                            log.error("请求[{}]认证发生错误: {}", request.getRequestURI(), ex.getMessage());

                            Resp resp = new Resp(ConstRespCode.ERROR, "认证发生错误", null);
                            RestUtil.sendResp(response, HttpServletResponse.SC_UNAUTHORIZED, resp);
                        }
                ));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置跨域访问
     * @return 跨域访问配置
     */
    private CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
