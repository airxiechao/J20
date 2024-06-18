package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.security.AuthenticationTokenDetail;
import com.alibaba.fastjson2.JSON;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

/**
 * 请求辅助类
 */
public class RestUtil {
    /**
     * 发送响应
     * @param response HTTP 响应
     * @param status HTTP 状态码
     * @param resp 响应数据
     * @throws IOException 发送异常
     */
    public static void sendResp(HttpServletResponse response,int status, Resp resp) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSON.toJSONString(resp));
        }
    }

    /**
     * 获取身份验证附加数据
     * @param principal 身份
     * @return 身份验证附加数据
     */
    public static AuthenticationTokenDetail getAuthenticationTokenDetail(Principal principal){
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)principal;
        AuthenticationTokenDetail tokenDetail = (AuthenticationTokenDetail)authenticationToken.getDetails();
        return tokenDetail;
    }
}
