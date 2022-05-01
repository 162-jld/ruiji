package com.beim.ruiji.filter;

import com.alibaba.fastjson.JSON;
import com.beim.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对前台发送的请求作一个过滤
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("拦截的请求，{}",request.getRequestURI());

        // 白名单
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        // 获取被拦截的请求路径
        String requestURI = request.getRequestURI();

        // 检查是否需要放行
        if (check(urls,requestURI)){
            filterChain.doFilter(request, response);
            return;
        }

        // 判断用户是否完成登录
        if (request.getSession().getAttribute("employee") != null){
            filterChain.doFilter(request, response);
            return;
        }


        // 如果未登录则返回未登录结果,通过输出流，将结果用JSON写回到前端
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)){
                // 表明路径能匹配上，则放行
                return true;
            }
        }
        return false;
    }



}
