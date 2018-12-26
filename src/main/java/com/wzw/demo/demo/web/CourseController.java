package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.CourseRepository;
import com.wzw.demo.demo.repo.SelectorRepository;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.CourseItem;
import com.wzw.demo.demo.vo.Question;
import com.wzw.demo.demo.vo.SelectorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
@Controller
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    SelectorRepository selectorRepository;
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value = "/filter/search", method = RequestMethod.GET)
    public String getSearch(HttpServletRequest request){
        String course = request.getParameter("course");
        if(course!=null)
            request.getSession().setAttribute("course",course);
        return "search.html";
    }
    @RequestMapping(value = "/filter/search/getSelectors", method = RequestMethod.GET)
    @ResponseBody
    public String getSelectors(HttpServletRequest request){
        String inst = request.getParameter("institution");
        int ins = 0;
        if(inst!=null)
            ins = Integer.parseInt(inst);
        HashMap<String,String> map = new HashMap<>();
        map.put("inst",JSON.toJSONString(selectorRepository.getInstitutions()));
        map.put("year",JSON.toJSONString(selectorRepository.getYears()));
        map.put("teac",JSON.toJSONString(selectorRepository.getTeachers(ins)));
        map.put("sect",JSON.toJSONString(selectorRepository.getSections()));
        map.put("acti",JSON.toJSONString(selectorRepository.getAcitivated()));
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/filter/search/getItems", method = RequestMethod.GET)
    @ResponseBody
    public String getSelectedItems(HttpServletRequest request){
        HashMap<String,String> map = new HashMap<>();
        int institution = Parser.toInteger(request.getParameter("institution"));
        int teacher = Parser.toInteger(request.getParameter("teacher"));
        int year = Parser.toInteger(request.getParameter("year"));
        int section = Parser.toInteger(request.getParameter("section"));
        int activated = Parser.toInteger(request.getParameter("activated"));
        String course = (String)request.getSession().getAttribute("course");
        if(course==null || course.equals(""))
            course = "0";
        course = course.trim();
        if(request.getParameter("activated")==null)
            activated = 3;//这个要另外判断的
        List<CourseItem> a = selectorRepository.getSelectedItems(
                institution,teacher,year,section,activated,course);
        map.put("items",JSON.toJSONString(a));
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = {"/course/{takenId}/","/course/{takenId}"},method = RequestMethod.GET)
    public String getCourse(@PathVariable Integer takenId, HttpServletRequest request,
                            Model model,HttpServletResponse response) throws IOException {
        //检察权限
        if(userRepository.isBannedView((Integer)request.getSession().getAttribute("uid"),takenId)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return "index.html";
        }
        //在一个课程板块下会有多个问题贴
        if(!request.getServletPath().endsWith("/")) {
            response.sendRedirect("/course/" + takenId + "/");
            return "index.html";
        }
        if(!courseRepository.containTaken(takenId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        String pp = request.getParameter("page");
        int page = 1;
        if(pp!=null)
            page = Integer.parseInt(pp);
        request.getSession().setAttribute("curTid",courseRepository.getTeacherIdByTakenId(takenId));
        int maxPage = courseRepository.getMaxPageByTakenId(takenId);
        List<Question> questions = courseRepository.getQuestionsByTakenId(takenId, page);
        CourseItem courseItem = selectorRepository.getCourseByTakenId(takenId);
        model.addAttribute("maxPage",maxPage);
        model.addAttribute("questions",questions);
        model.addAttribute("course",courseItem);
        return "course.html";
    }
    @RequestMapping(value = "/course/{takenId}/getItems", method = RequestMethod.GET)
    @ResponseBody
    public String getItems(@PathVariable Integer takenId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(!courseRepository.containTaken(takenId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        HashMap<String,String> map = new HashMap<>();
        Integer page = Integer.parseInt(request.getParameter("page"));
        if(page==null) page = 1;
        int maxPage = courseRepository.getMaxPageByTakenId(takenId);
        List<Question> questions = courseRepository.getQuestionsByTakenId(takenId, page);
        String questionlists = JSON.toJSONString(questions);
        map.put("questions",questionlists);
        map.put("maxPage",maxPage+"");
        if(questions==null){
            map.put("msg","数据异常，请刷新页面");
            map.put("sts","0");
        }else
            map.put("sts","1");
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/course/{takenId}/post", method = RequestMethod.POST)
    @ResponseBody
    public String postQuestion(@PathVariable Integer takenId, @RequestBody Question question,
                             HttpServletRequest request,HttpServletResponse response) throws IOException {
        if(!courseRepository.containTaken(takenId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return "";
        }
        if(userRepository.isBannedPost((Integer)request.getSession().getAttribute("uid"),takenId)){
            return "";
        }
        question.setTakenId(takenId);
        question.setUserId((Integer) request.getSession().getAttribute("uid"));
        Integer questionId = courseRepository.insertQuestion(question);
        request.getSession().setAttribute("questionId",questionId);
        request.getSession().setAttribute("order",1);
        return "1";
    }
}
