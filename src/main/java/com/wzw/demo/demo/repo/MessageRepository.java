package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.util.DateFormatter;
import com.wzw.demo.demo.vo.MessageItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MessageRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;
    public List<MessageItem> getNormalMsgItems(Integer uid,Integer size, Integer start){
        List<MessageItem> list = jdbcTemplate.query(" select * from    " +
                "                 (select ct,dt,rd,asid,nk,reply,qid,mct from (select    " +
                "                 a.content ct,a.date dt,a.read rd,a.answer_id asid,u.nickname nk,   " +
                "                 a.from_id reply, q.question_id qid,q.content mct from answer a inner join `user` u on u.uid   " +
                "                  = a.from_id inner join question q on q.question_id = a.question_id where u.role!=2 and q.uid=? union select s.content ct,s.date dt,s.read rd,   " +
                "                 s.answer_id asid,us.nickname nk,s.from_id reply,asa.question_id qid,   " +
                "                  asa.content mct from sub_answer s inner join `user` us on us.uid=s.from_id    " +
                "                 inner join answer asa on asa.answer_id=s.answer_id where s.to_id=?    " +
                "                 and us.role!=2 and s.to_sub_ans_id=0) aa union select sss.content ct,sss.date dt,sss.read rd,   " +
                "                                 sss.answer_id asid,uss.nickname nk,sss.from_id reply,asas.question_id qid,   " +
                "                                  (select se.content from sub_answer se where se.sub_ans_id=sss.to_sub_ans_id) mct from sub_answer sss inner join `user` uss on uss.uid=sss.from_id    " +
                "                                 inner join answer asas on asas.answer_id=sss.answer_id where sss.to_id=?    " +
                "                                 and uss.role!=2 and sss.to_sub_ans_id!=0) bb order by dt desc limit ?,?; ", preparedStatement -> {
            setMsgSQL(uid, size, start, preparedStatement);
        }, (resultSet, i) -> {
            return setMsgItm(resultSet);
        });
        return list;
    }

    public List<MessageItem> getTeacherMsgItems(Integer uid,Integer size, Integer start){
        List<MessageItem> list = jdbcTemplate.query(" select * from    " +
                "                 (select ct,dt,rd,asid,nk,reply,qid,mct from (select    " +
                "                 a.content ct,a.date dt,a.read rd,a.answer_id asid,u.nickname nk,   " +
                "                 a.from_id reply, q.question_id qid,q.content mct from answer a inner join `user` u on u.uid   " +
                "                  = a.from_id inner join question q on q.question_id = a.question_id where u.role=2 and q.uid=? union select s.content ct,s.date dt,s.read rd,   " +
                "                 s.answer_id asid,us.nickname nk,s.from_id reply,asa.question_id qid,   " +
                "                  asa.content mct from sub_answer s inner join `user` us on us.uid=s.from_id    " +
                "                 inner join answer asa on asa.answer_id=s.answer_id where s.to_id=?    " +
                "                 and us.role=2 and s.to_sub_ans_id=0) aa union select sss.content ct,sss.date dt,sss.read rd,   " +
                "                                 sss.answer_id asid,uss.nickname nk,sss.from_id reply,asas.question_id qid,   " +
                "                                  (select se.content from sub_answer se where se.sub_ans_id=sss.to_sub_ans_id) mct from sub_answer sss inner join `user` uss on uss.uid=sss.from_id    " +
                "                                 inner join answer asas on asas.answer_id=sss.answer_id where sss.to_id=?    " +
                "                                 and uss.role=2 and sss.to_sub_ans_id!=0) bb order by dt desc limit ?,?; ", preparedStatement -> {
            setMsgSQL(uid, size, start, preparedStatement);
        }, (resultSet, i) -> setMsgItm(resultSet));
        return list;
    }

    public List<MessageItem> getStudentQuestions(Integer uid, Integer size,Integer start){
        List<MessageItem> messageItems = jdbcTemplate.query("select q.content,q.date,q.read,0," +
                        "u.nickname,q.uid,q.question_id,null from question q inner join course_teacher_takes ct" +
                        " on ct.taken_id = q.taken_id inner join `user` u on u.uid=q.uid where ct.teacher_id=? " +
                        "order by q.date desc limit ?,?;",
                preparedStatement -> {
                    preparedStatement.setInt(1,uid);
                    preparedStatement.setInt(2,start);
                    preparedStatement.setInt(3,size);
                }, (resultSet, i) -> setMsgItm(resultSet));
        return messageItems;
    }

    private MessageItem setMsgItm(ResultSet resultSet) throws SQLException {
        MessageItem messageItem = new MessageItem();
        messageItem.setContent(resultSet.getString(1));
        messageItem.setDate(DateFormatter.doFormat(resultSet.getString(2)));
        messageItem.setRead(resultSet.getInt(3));
        messageItem.setAnswerId(resultSet.getInt(4));
        messageItem.setRelyer(resultSet.getString(5));
        messageItem.setReplyId(resultSet.getInt(6));
        messageItem.setQuestionId(resultSet.getInt(7));
        messageItem.setTakenId(questionRepository.getTakenIdByQuestionId(messageItem.getQuestionId()));
        messageItem.setMyContent(resultSet.getString(8));
        return messageItem;
    }

    private void setMsgSQL(Integer uid, Integer size, Integer start, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, uid);
        preparedStatement.setInt(2, uid);
        preparedStatement.setInt(3, uid);
        preparedStatement.setInt(4, start);
        preparedStatement.setInt(5, size);
    }

}
