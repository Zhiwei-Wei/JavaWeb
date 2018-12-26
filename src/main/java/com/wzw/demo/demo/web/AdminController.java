package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.CourseRepository;
import com.wzw.demo.demo.repo.SelectorRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.Course;
import com.wzw.demo.demo.vo.CourseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    SelectorRepository selectorRepository;
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String teacher(){
        return "teacher.html";
    }
    @RequestMapping(value = "/course",method = RequestMethod.GET)
    public String course(){
        return "adCourse.html";
    }
    @RequestMapping(value = "/getCourses",method = RequestMethod.GET)
    @ResponseBody
    public String getCourses(){
        List<CourseItem> courses = selectorRepository.
                getSelectedItems(0,0,0,0,3,"0");
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",courses.size());
        map.put("rows",courses);
        return JSON.toJSONString(map);
    }
}
