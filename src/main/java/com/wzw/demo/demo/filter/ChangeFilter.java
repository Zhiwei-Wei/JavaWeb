package com.wzw.demo.demo.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


public class ChangeFilter extends HandlerInterceptorAdapter {
    @Bean
    public JdbcTemplate jdbcTemplate3(){
        return new JdbcTemplate();
    }
    @Autowired
    JdbcTemplate jdbcTemplate3;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        Integer answerId = Integer.parseInt(request.getParameter("answerId"));
        if( uid==null || !isAuthorOf(uid,answerId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }

    public boolean isAuthorOf(Integer uid, Integer answerId) {
        String sql = "select `from_id` from answer where answer_id="+answerId;
        List<Integer> integer = jdbcTemplate3.query(
                sql,
                preparedStatement -> preparedStatement.setInt(1,answerId),
                (resultSet, i) -> resultSet.getInt(1));
        return uid.equals(integer.get(0));
    }
}
