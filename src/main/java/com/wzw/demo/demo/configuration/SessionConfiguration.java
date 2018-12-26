package com.wzw.demo.demo.configuration;

import com.wzw.demo.demo.filter.ChangeFilter;
import com.wzw.demo.demo.filter.LoginFilter;
import com.wzw.demo.demo.filter.StudentFilter;
import com.wzw.demo.demo.filter.TeacherFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginFilter()).addPathPatterns("/filter/**",
                "/course/**","/question/**");
        registry.addInterceptor(new TeacherFilter()).addPathPatterns("/**/teacher/**");
        registry.addInterceptor(new StudentFilter()).addPathPatterns("/**/student/**");
//        registry.addInterceptor(new ChangeFilter()).addPathPatterns("/user/postChange");
    }
}
