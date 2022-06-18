package com.beim.ruiji.filter;

import com.alibaba.fastjson.JSON;
import com.beim.ruiji.common.BaseContext;
import com.beim.ruiji.common.R;
import com.beim.ruiji.entity.User;
import lombok.extern.slf4j.Slf4j;
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
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 路径白名单
    private static final String[] URL_PATH = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg", // 移动端发送短信
            "/user/login"  // 移动端登录
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("拦截到请求：{}",request.getRequestURI());
        // 检查是否需要放行
        if (check(URL_PATH,request.getRequestURI())){
            filterChain.doFilter(request, response);
            return;
        }
        // 后管端，判断用户是否完成登录
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("登录用户的id为：{}",empId);
            // 将用户ID存储到ThreadLocal中
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        // 移动端，判断移动端是否完成登录
        if (request.getSession().getAttribute("user") != null){
            // 获取用户ID
            Long userId = ((User) request.getSession().getAttribute("user")).getId();
            log.info("登录用户的id为：{}",userId);
            // 将用户ID存储到ThreadLocal中
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        // 如果未登录则返回未登录结果,通过输出流，fastjson 将结果用JSON写回到前端
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
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
