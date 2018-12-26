package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.util.DateFormatter;
import com.wzw.demo.demo.vo.Answer;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.vo.SubAnswer;
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
public class AnswerRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    @Lazy
    QuestionRepository questionRepository;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public List<SubAnswer> getSubAnswersByAnswerId(Integer answerId, int page){
        int a = (page-1)* Parameter.PAGESIZE;
        return jdbcTemplate.query("select * from sub_answer where answer_id=? " +
                "order by sub_answer_order limit ?,?;", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, answerId);
                preparedStatement.setInt(2, a);
                preparedStatement.setInt(3, Parameter.PAGESIZE);
            }
        }, (resultSet, i) -> {
            SubAnswer subAnswer = new SubAnswer();
            subAnswer.setAnswerId(resultSet.getInt(1));
            subAnswer.setToId(resultSet.getInt(2));
            subAnswer.setFromId(resultSet.getInt(3));
            subAnswer.setFromName(userRepository.getUserByUserId(subAnswer.getFromId()).getNickName());
            subAnswer.setToName(userRepository.getUserByUserId(subAnswer.getToId()).getNickName());
            subAnswer.setDate(DateFormatter.doFormat(resultSet.getString(4)));
            subAnswer.setSubAnswerOrder(resultSet.getInt(6));
            subAnswer.setContent(resultSet.getString(7));
            subAnswer.setSubAnswerId(resultSet.getInt(8));
            subAnswer.setFromRole(userRepository.getRoleByUserId(subAnswer.getFromId()));
            return subAnswer;
        });

    }

    private int getAnserId() {
        List<Integer> integers = jdbcTemplate.query("(select max(b.answer_id) from answer b)", (resultSet, i) -> resultSet.getInt(1));
        return integers.get(0);
    }
    public Integer getAnswerOrderByQuestionId(Integer questionId){
        List<Integer> integers = jdbcTemplate.query("select max(ans_order) from answer where question_id" +
                " = ?", preparedStatement -> preparedStatement.setInt(1, questionId), (resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0?integers.get(0)+1:1;
    }
    public Integer insertAnswer(Answer answer){
        final int ans_order=getAnswerOrderByQuestionId(answer.getQuestionId());
        jdbcTemplate.update("insert into answer(question_id,content,from_id,`date`,ans_order,answer_id) values " +
                "(?,?,?,?,?,?)", preparedStatement -> {
                    preparedStatement.setInt(1,answer.getQuestionId());
                    preparedStatement.setString(2,answer.getContent());
                    preparedStatement.setInt(3,answer.getFromId());
                    preparedStatement.setString(4,simpleDateFormat.format(new java.util.Date()));
                    preparedStatement.setInt(5,ans_order);
                    preparedStatement.setInt(6,getAnserId()+1);
                });
        updateQuestionByQuestionId(answer.getQuestionId());
        //通过问题id获取作者id,然后向作者添加未读问题
        userRepository.addUnread(questionRepository.getAuthorIdByQuestionId(answer.getQuestionId()));
        return getAnswerIdByQuestionIdAndAnswerOrder(answer.getQuestionId(),ans_order);
    }
    public Integer getAnswerIdByQuestionIdAndAnswerOrder(Integer questionId,Integer ansOr){
        List<Integer> integers = jdbcTemplate.query("select answer_id from answer where question_id=?" +
                " and ans_order=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setInt(1, questionId);
                preparedStatement.setInt(2, ansOr);
            }
        }, (resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0?integers.get(0):null;
    }
    public void insertSubAnswerByAnswerIdAndToId(Integer answerId, Integer toId, Integer fromId,String content,
                                                 Integer questionId, Integer toSubAnsId){
        final Integer subAnswerOrder = getSubAnswerOrderByAnswerId(answerId);
        jdbcTemplate.update("insert into sub_answer values(?,?,?,?,?,?,?,null,?);", preparedStatement -> {
            preparedStatement.setInt(1,answerId);
            preparedStatement.setInt(2,toId);
            preparedStatement.setInt(3,fromId);
            preparedStatement.setString(4,simpleDateFormat.format(new java.util.Date()));
            preparedStatement.setInt(5,0);
            preparedStatement.setInt(6,subAnswerOrder);
            preparedStatement.setString(7,content);
            preparedStatement.setInt(8,toSubAnsId);
        });
        updateQuestionByQuestionId(questionId);
        userRepository.addUnread(toId);
    }
    public Integer getSubAnswerOrderByAnswerId(Integer answerId){
        List<Integer> integers = jdbcTemplate.query("select max(sub_answer_order) from sub_answer where answer_id=?"
                , new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, answerId);
                    }
                }, new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getInt(1);
                    }
                });
        return integers.size()>0?integers.get(0)+1:1;
    }

    /**
     * 每次回复就把帖子顶高
     * @param id
     */
    public void updateQuestionByQuestionId(Integer id){
        jdbcTemplate.update("update question set last_update=? where question_id=?"
                , preparedStatement -> {
            preparedStatement.setString(1,simpleDateFormat.format(new java.util.Date()));
                    preparedStatement.setInt(2,id);
                });
    }

}
