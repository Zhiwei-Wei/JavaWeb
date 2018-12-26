package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.util.DateFormatter;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.vo.Course;
import com.wzw.demo.demo.vo.CourseItem;
import com.wzw.demo.demo.vo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class CourseRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    @Lazy
    QuestionRepository questionRepository;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public List<Question> getQuestionsByTakenId(Integer takenId, int page){
        int a = (page-1)* Parameter.PAGESIZE;
        List<Question> questions = jdbcTemplate.query("select * from question where taken_id=? order by last_update desc limit" +
                        " ?,?;",
                preparedStatement -> {
                    preparedStatement.setInt(1, takenId);
                    preparedStatement.setInt(2,a);
                    preparedStatement.setInt(3,Parameter.PAGESIZE);
                }, (resultSet, i) -> getQuestion(resultSet));
        questions.sort((o1, o2) -> o2.getLastUpdate().compareTo(o1.getLastUpdate()));
        return questions;
    }

    public Question getQuestion(ResultSet resultSet) throws SQLException {
        int a = 0;
        Question question = new Question();
        question.setQuestionId(resultSet.getInt(1));
        question.setTitle(resultSet.getString(2));
        question.setContent(resultSet.getString(3));
        question.setUserId(resultSet.getInt(4));
        question.setLastUpdate(DateFormatter.doFormat(resultSet.getString(5)));
        question.setTakenId(resultSet.getInt(6));
        question.setAuthorName(userRepository.getUserByUserId(question.getUserId()).getNickName());
        question.setDate(DateFormatter.doFormat(resultSet.getString(7)));
        question.setRead(resultSet.getInt(8));
        return question;
    }
    public boolean containTaken(Integer takenId){
        List<Integer> integers = jdbcTemplate.query("select taken_id from course_teacher_takes where taken_id=?",
                preparedStatement -> preparedStatement.setInt(1, takenId), (resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0;
    }
    public boolean containQuestion(Integer questionId){
        List<Integer> integers = jdbcTemplate.query("select question_id from question where question_id=?",
                preparedStatement -> preparedStatement.setInt(1,questionId),(resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0;
    }
    public Integer getMaxPageByTakenId(Integer takenId){
        return jdbcTemplate.query("select count(*) from question where taken_id=?",
                preparedStatement -> preparedStatement.setInt(1,takenId), (resultSet, i) -> resultSet.getInt(1)
        ).get(0)/Parameter.PAGESIZE+1;
    }

    public Integer insertQuestion(Question question){
        jdbcTemplate.update("insert into question(title,content,uid,last_update,taken_id,`date`,question_id) values " +
                        "(?,?,?,?,?,?,?);",
                preparedStatement -> {
                    preparedStatement.setString(1,question.getTitle());
                    preparedStatement.setString(2,question.getContent());
                    preparedStatement.setInt(3,question.getUserId());
                    preparedStatement.setString(4,simpleDateFormat.format(new java.util.Date()));
                    preparedStatement.setInt(5,question.getTakenId());
                    preparedStatement.setString(6,simpleDateFormat.format(new java.util.Date()));
                    preparedStatement.setInt(7,getQuestionId()+1);
                });
        Integer tid = getTeacherIdByTakenId(question.getTakenId());
        userRepository.addUnread(tid);
        return questionRepository.getLatestQuestionByUserId(question.getUserId());
    }
    public Integer getTeacherIdByTakenId(Integer takenId){
        List<Integer> integers = jdbcTemplate.query("select teacher_id from course_teacher_takes" +
                " where taken_id=?", preparedStatement -> preparedStatement.setInt(1,takenId),
                (resultSet, i) -> resultSet.getInt(1));
        return integers.get(0);
    }
    private int getQuestionId() {
        List<Integer> integers = jdbcTemplate.query("(select max(b.question_id) from question b)", (resultSet, i) -> resultSet.getInt(1));
        return integers.get(0);
    }

}
