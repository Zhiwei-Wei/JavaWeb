package com.wzw.demo.demo;

import com.wzw.demo.demo.repo.NewsRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Aspect
@Component
public class NewsCount {

    @Bean
    public NewsRepository newsRepository1(){
        return new NewsRepository();
    }
    @Autowired
    NewsRepository newsRepository1;
    @Pointcut("execution(public * com.wzw.demo.demo.web.*Controller.*(..))")
    private void controller(){}
    @Before(value = "controller()")
    public void countBefore(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession();
        Integer uid = (Integer) session.getAttribute("uid");
        if(uid!=null){//首先要登录
            Long l = (Long)session.getAttribute("last");//上次判断的时间
            if(l==null){
                session.setAttribute("last",new Date().getTime());
                session.setAttribute("news",newsRepository1.getNewsNumber(uid));//第一次
            }else {//之后几次
                Long ll = new Date().getTime();
                if(ll-l>300){//5分钟判断一次
                    session.setAttribute("last",ll);
                    session.setAttribute("news",newsRepository1.getNewsNumber(uid));
                }
            }
        }
    }
}
