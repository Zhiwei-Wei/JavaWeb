package com.wzw.demo.demo.web;

import com.alibaba.fastjson.JSON;
import com.wzw.demo.demo.repo.AnswerRepository;
import com.wzw.demo.demo.repo.CourseRepository;
import com.wzw.demo.demo.repo.QuestionRepository;
import com.wzw.demo.demo.repo.UserRepository;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.util.Parser;
import com.wzw.demo.demo.vo.Answer;
import com.wzw.demo.demo.vo.Question;
import com.wzw.demo.demo.vo.User;
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
public class QuestionController {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @ResponseBody
    @RequestMapping(value = "/course/{takenId}/question/{questionId}/getAnswers", method = RequestMethod.GET)
    public String getQuestions(@PathVariable Integer takenId, @PathVariable Integer questionId,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(!courseRepository.containTaken(takenId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(!courseRepository.containQuestion(questionId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        String pp = request.getParameter("page");
        int page = 1;
        if(pp!=null)
            page = Integer.parseInt(pp);
        List<Answer> answers = questionRepository.getAnswersByQuestionId(questionId, page);
        for(Answer answer:answers){
            answer.setJsonSubAnswers(JSON.toJSONString(answer.getSubAnswers()));
        }//读取的时候注意直接读取其中的JSONString
        HashMap<String,String> map = new HashMap<>();
        map.put("answers",JSON.toJSONString(answers));
        Question question = questionRepository.getQuerstionByQuestionId(questionId);
        map.put("question",JSON.toJSONString(question));
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/course/{takenId}/question/{questionId}", method = RequestMethod.GET)
    public String getQuestion(@PathVariable Integer takenId,
                              Model model,
                              @PathVariable Integer questionId, HttpServletResponse response,HttpServletRequest request) throws IOException {
        if(!courseRepository.containTaken(takenId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(!courseRepository.containQuestion(questionId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(userRepository.isBannedView((Integer)request.getSession().getAttribute("uid"),takenId)){
            return "";
        }
        model.addAttribute("qId",questionId);
        model.addAttribute("tkId",takenId);
        return "question.html";
    }

    @RequestMapping(value = "/course/{courseId}/question/{questionId}/post", method = RequestMethod.POST)
    @ResponseBody
    public String postAnswer(@PathVariable Integer courseId,
                             @PathVariable Integer questionId, HttpServletRequest request
    , @RequestBody Answer answer,HttpServletResponse response) throws IOException {
        if(!courseRepository.containTaken(courseId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(!courseRepository.containQuestion(questionId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(userRepository.isBannedPost((Integer)request.getSession().getAttribute("uid"),courseId)){
            return "";
        }
        StringBuilder content = new StringBuilder(answer.getContent());
        answer.setContent(content.toString());
        answer.setFromId((Integer) request.getSession().getAttribute("uid"));
        answer.setQuestionId(questionId);
        Integer answerId = answerRepository.insertAnswer(answer);
        request.getSession().setAttribute("answerId",answerId);
        request.getSession().setAttribute("order",1);
        HashMap<String,String> map = new HashMap<>();
        map.put("sts","1");
        Integer uid = (Integer)request.getSession().getAttribute("uid");
        User u =  userRepository.getUserByUserId(uid);
        if(u.getRole().equals(Parameter.TEACHER)){
            questionRepository.setRead(questionId);
        }
        return JSON.toJSONString(map);
    }
    @RequestMapping(value = "/course/{takenId}/question/{questionId}/postSubAnswer", method = RequestMethod.GET)
    @ResponseBody
    public String postSubAnswer(@PathVariable Integer takenId,
                                @PathVariable Integer questionId, HttpServletRequest request,HttpServletResponse response) throws IOException {
        if(!courseRepository.containTaken(takenId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(!courseRepository.containQuestion(questionId))
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        if(userRepository.isBannedPost((Integer)request.getSession().getAttribute("uid"),takenId)){
            return "";
        }
        Integer answerId = Integer.parseInt(request.getParameter("contentOrder"));
        String content = request.getParameter("content");
        Integer subAnsId = Parser.toInteger(request.getParameter("subAnsId"));
        Integer uid = (Integer)request.getSession().getAttribute("uid");
        answerRepository.insertSubAnswerByAnswerIdAndToId(answerId,
                questionRepository.getAuthorIdByQuestionId(questionId),uid,content,questionId,subAnsId);
        return "1";
    }
}
