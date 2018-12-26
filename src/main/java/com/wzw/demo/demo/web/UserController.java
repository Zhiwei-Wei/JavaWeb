package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.NickNameGenerator;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/login/**", method = RequestMethod.GET)
    public String login(){
        return "login.html";
    }
    @ResponseBody
    @RequestMapping(value = "/login/**", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request,HttpServletResponse response, @RequestBody User user1) {
        String username = user1.getUserName();
        String password = user1.getPassword();
        System.out.println("用户名："+username+" 密码："+password);
        User user = userRepository.getUserByUserName(username);
        Map<String,String> map = new HashMap<>();
        if(user==null||!user.getPassword().equals(password)){
            map.put("sts","0");//表示失败
            map.put("msg","用户名或密码错误！");
        }else{
            map.put("sts","1");
            map.put("msg","登录成功！");
            request.getSession().setAttribute("uid",user.getUid());//放uid
            request.getSession().setAttribute("nickname",user.getNickName());
            request.getSession().setAttribute("role",user.getRole());
        }
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(){
        return "register.html";
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String doRegeisterStudent(@RequestBody User user) throws IOException {
        user.setNickName(NickNameGenerator.getNickName());
        userRepository.register(user, Parameter.STUDENT);
        HashMap<String,String> map = new HashMap<>();
        map.put("msg","1");
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/logout")
    public String doLogOut(HttpServletRequest request,HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect("/index");
        return "/index";
    }
    @RequestMapping(value = "/checkUserName")
    @ResponseBody
    public String checkUserName(@RequestBody User user){
        String username = user.getUserName();
        HashMap<String,String> map = new HashMap<>();
        if(userRepository.getUserByUserName(username)!=null){
            map.put("sts","0");
            map.put("msg","用户名重复");
        }else{
            map.put("sts","1");
            map.put("msg","该用户名可用");
        }
        return JSON.toJSONString(map);
    }

}
