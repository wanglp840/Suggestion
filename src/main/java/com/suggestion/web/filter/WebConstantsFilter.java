package com.suggestion.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * web常量拦截器
 *
 * @Auther wanglp
 * @Time 15/12/2 下午11:11
 * @Email wanglp840@nenu.edu.cn
 */

public class WebConstantsFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        int port = httpRequest.getServerPort();
        String path = httpRequest.getContextPath();
        // 协议＋主机名（域名）＋ 端口 ＋ 应用名 ／
        String basePath = httpRequest.getScheme() + "://" + httpRequest.getServerName()
                + (port == 80 ? "" : (":" + port)) + path + "/";
        // 讲网址放入到request中
        request.setAttribute("website", basePath);


        chain.doFilter(request, response);
    }

    public void destroy() {

    }
}
