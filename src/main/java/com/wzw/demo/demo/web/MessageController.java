package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.MessageRepository;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.MessageItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/filter/msg")
/**
 * 映射在/filter/msg下的所有子路径
 */
public class MessageController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @RequestMapping(method = RequestMethod.GET)
    public String msg(HttpSession session){
        //当到达该页面就把unread清零
        userRepository.readAll((Integer)session.getAttribute("uid"));
        session.setAttribute("news",0);
        return "message.html";
    }
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(){
        return "message.html";
    }
    @RequestMapping(value = "/getItems", method = RequestMethod.GET)
    @ResponseBody
    public String getMsgItems(HttpServletRequest request){
        int start = Parser.toInteger(request.getParameter("start"));
        int size = Parser.toInteger(request.getParameter("size"));
        List<MessageItem> messageItems = messageRepository.getNormalMsgItems((Integer) request.getSession().getAttribute("uid")
        , size,start);
        HashMap<String ,String> map = new HashMap<>();
        map.put("list", JSON.toJSONString(messageItems));
        return JSON.toJSONString(map);
    }
    @ResponseBody
    @RequestMapping(value = "/stu/getItems",method = RequestMethod.GET)
    public String getTeacherAnswers(HttpServletRequest request){
        int start = Parser.toInteger(request.getParameter("start"));
        int size = Parser.toInteger(request.getParameter("size"));
        List<MessageItem> messageItems = messageRepository.getTeacherMsgItems((Integer) request.getSession().getAttribute("uid")
                , size,start);
        HashMap<String ,String> map = new HashMap<>();
        map.put("list", JSON.toJSONString(messageItems));
        return JSON.toJSONString(map);
    }
    @ResponseBody
    @RequestMapping(value = "/teacher/getItems",method = RequestMethod.GET)
    public String getStudentQuestions(HttpServletRequest request){
        int start = Parser.toInteger(request.getParameter("start"));
        int size = Parser.toInteger(request.getParameter("size"));
        List<MessageItem> messageItems = messageRepository.getStudentQuestions((Integer) request.getSession().getAttribute("uid")
                , size,start);
        HashMap<String ,String> map = new HashMap<>();
        map.put("list", JSON.toJSONString(messageItems));
        return JSON.toJSONString(map);
    }
}
