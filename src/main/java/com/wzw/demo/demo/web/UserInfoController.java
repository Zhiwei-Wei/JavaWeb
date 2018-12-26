package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.User;
import com.wzw.demo.demo.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping(value = "/filter/profile")
public class UserInfoController {
    @Autowired
    UserRepository userRepository;
    @RequestMapping( method = RequestMethod.GET)
    public String profile(Model model, HttpServletRequest request){
        Integer uid = (Integer) request.getSession().getAttribute("uid");
        model.addAttribute("info",userRepository.getUserInfoById(uid));
        model.addAttribute("institution",userRepository.getInsti());
        return "profile.html";
    }
    @RequestMapping(value = "/checkNickName",method = RequestMethod.GET)
    @ResponseBody
    public String checkNickName(HttpServletRequest request){
        HashMap<String,String> map = new HashMap<>();
        Integer user = userRepository.getNumberByNickName(request.getParameter("nickName"));
        if(user==0){
            map.put("sts","1");
        }else
            map.put("sts","444");
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/submitInfo", method = RequestMethod.POST)
    public String submitInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUid((Integer) request.getSession().getAttribute("uid"));
        userInfo.setNickName(request.getParameter("nickName"));
        userInfo.setRealName(request.getParameter("realName"));
        userInfo.setSchool(request.getParameter("school"));
        userInfo.setInstId(Parser.toInteger(request.getParameter("instId")));
        userRepository.updateUserInfo(userInfo,(Integer)request.getSession().getAttribute("role"));
        request.getSession().setAttribute("nickname",userInfo.getNickName());
        response.sendRedirect("/filter/profile");
        return "index.html";
    }

    @RequestMapping(value = "/ckPs",method = RequestMethod.POST)
    @ResponseBody
    public String ckps(HttpServletRequest request, @RequestBody String password){
        Integer uid = (Integer) request.getSession().getAttribute("uid");
        HashMap<String,String> map = new HashMap<>();
        if(userRepository.checkPass(password,uid))
            map.put("sts","1");
        else
            map.put("sts","0");
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/submitPass",method = RequestMethod.POST)
    public String changePass(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String newPassword = request.getParameter("newPassword");
        System.out.println("新密码为："+newPassword);
        userRepository.updatePassword(newPassword,(Integer)request.getSession().getAttribute("uid"));
        response.sendRedirect("/logout");
        return "index.html";
    }
}
