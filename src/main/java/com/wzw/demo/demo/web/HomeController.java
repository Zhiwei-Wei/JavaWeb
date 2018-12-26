package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.QuestionRepository;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.CourseItem;
import com.wzw.demo.demo.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value = "/index")
    public String home(){
        return "index.html";
    }
    @RequestMapping(value = "/upload")
    public String ho2me(){
        return "/fragment/upload.html";
    }
    @RequestMapping(value = "/student/hello")
    public String test(){
        return "index.html";
    }
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    public String deleteAnswer(HttpServletRequest request){
        Integer qId = Parser.toInteger(request.getParameter("qId"));
        Integer aId = Parser.toInteger(request.getParameter("aId"));
        if(aId==0){
            questionRepository.deleteQuestion(qId);
        }else{
            questionRepository.deleteAnswer(aId);
        }
        return "success";
    }
    @RequestMapping(value = "/deleteSb", method = RequestMethod.GET)
    @ResponseBody
    public String deletSubAns(HttpServletRequest request){
        Integer sbid = Parser.toInteger(request.getParameter("sbId"));
        questionRepository.deleteSubAnswer(sbid);
        return "success";
    }
    @RequestMapping(value = "/user/postChange", method = RequestMethod.GET)
    @ResponseBody
    public String changeAnswer(HttpServletRequest request){
        if(userRepository.isBannedPost((Integer) request.getSession().getAttribute("uid"),
                Integer.parseInt(request.getParameter("takesId"))))
            return "";
        Integer answerId = Parser.toInteger(request.getParameter("answerId"));
        String content = request.getParameter("content");
        questionRepository.updateAnserContent(answerId,content);
        return "success";
    }
    @RequestMapping(value = "/teacher/course",method = RequestMethod.GET)
    public String course(Model model, HttpServletRequest request){
        List<CourseItem> courseItems = userRepository.getTakesByTid(
                (Integer)request.getSession().getAttribute("uid"));
        model.addAttribute("courses",courseItems);
        return "myCourse.html";
    }
    @RequestMapping(value = "/teacher/getStudents",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(HttpServletRequest request){
        int limit = Parser.toInteger(request.getParameter("limit"));
        int offset = Parser.toInteger(request.getParameter("offset"));
        Integer takesId = Parser.toInteger(request.getParameter("takesId"));
        List<UserInfo> userInfos = userRepository.getAllStudentInfos(offset,limit,takesId);
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",userRepository.getStudentCount());
        map.put("rows",userInfos);
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/teacher/authority/post")
    @ResponseBody
    public String setStudents(@RequestParam String ids,@RequestParam String oper,
                              @RequestParam Integer takesId){
        String[] id = ids.split(" ");
        Integer[] ID = new Integer[id.length];
        for(int i = 0; i < id.length; i++){
            ID[i] = Integer.parseInt(id[i]);
        }
        if(oper.equals("ban")){
            userRepository.banStudentsToPostTakes(ID,takesId);
        }else if(oper.equals("allow")){
            userRepository.allowStudentsToPostTakes(ID,takesId);
        }
        return "1";
    }
    @RequestMapping(value = "/teacher/authority/view")
    @ResponseBody
    public String setStudentsView(@RequestParam String ids,@RequestParam String oper,
                              @RequestParam Integer takesId){
        String[] id = ids.split(" ");
        Integer[] ID = new Integer[id.length];
        for(int i = 0; i < id.length; i++){
            ID[i] = Integer.parseInt(id[i]);
        }
        if(oper.equals("ban")){
            userRepository.banStudentsToViewTakes(ID,takesId);
        }else if(oper.equals("allow")){
            userRepository.allowStudentsToViewTakes(ID,takesId);
        }
        return "1";
    }

}
