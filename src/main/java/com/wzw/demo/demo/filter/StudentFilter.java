package com.wzw.demo.demo.filter;

import com.wzw.demo.demo.util.Parameter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StudentFilter extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HttpSession session = request.getSession();
        Integer role = (Integer) session.getAttribute("role");
        System.out.println("该用户想要访问学生权限，他的权限是:"+role);
        if(!role.equals(Parameter.STUDENT)&&!role.equals(Parameter.ADMIN)) {
            System.out.println("拦截！");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }

}