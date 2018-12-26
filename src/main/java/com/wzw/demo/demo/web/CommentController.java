package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.QuestionRepository;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/filter/comment")
public class CommentController {
    @Autowired
    QuestionRepository questionRepository;
    @RequestMapping(method = RequestMethod.GET)
    public String comment(){
        return "comment.html";
    }
    @RequestMapping(value = "/getComment",method = RequestMethod.GET)
    @ResponseBody
    public String getComment(HttpServletRequest request){
        int limit = Parser.toInteger(request.getParameter("limit"));
        int offset = Parser.toInteger(request.getParameter("offset"));
        String keyWord = request.getParameter("keyWord");
        int searchWay = Parser.toInteger(request.getParameter("searchWay"));
        List<Comment> comments = questionRepository.getComment(limit,offset,keyWord,searchWay);
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",questionRepository.getCommentSize(keyWord,searchWay));
        map.put("rows",comments);
        return JSON.toJSONString(map);
    }
}
