package com.wzw.demo.demo.filter;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        if(uid==null){
            System.out.println("用户没有登录就访问页面！"+request.getServletPath());
            if (!(request.getHeader("x-requested-with") != null
                    && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))){
                //若不是正常访问
                response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
            }
            return false;
        }
        return true;
    }

}
