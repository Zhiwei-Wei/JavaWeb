package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.util.DateFormatter;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Repository
public class QuestionRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    @Lazy
    AnswerRepository answerRepository;
    @Autowired
    FileRepository fileRepository;

    public boolean isAuthorOf(Integer uid, Integer answerId) {
        String sql = "select `from_id` from answer where answer_id="+answerId;
        List<Integer> integer = jdbcTemplate.query(
                sql,
                preparedStatement -> preparedStatement.setInt(1,answerId),
                (resultSet, i) -> resultSet.getInt(1));
        return uid.equals(integer.get(0));
    }

    public Integer getLatestQuestionByUserId(Integer uid){
        List<Integer> integers = jdbcTemplate.query("select max(question_id) from question where uid=?",
                preparedStatement -> preparedStatement.setInt(1, uid), (resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0?integers.get(0):null;
    }
    public List<Answer> getAnswersByQuestionId(Integer questionId, int page){
        int a = (page-1)* Parameter.PAGESIZE;
        return jdbcTemplate.query("select * from answer where question_id=? order by ans_order asc " +
                        "limit ?,?;",
                preparedStatement -> {
                    preparedStatement.setInt(1, questionId);
                    preparedStatement.setInt(2,a);
                    preparedStatement.setInt(3,Parameter.PAGESIZE);
                }, (resultSet, i) -> {
                    Answer answer = new Answer();
                    answer.setAnswerId(resultSet.getInt(1));
                    answer.setQuestionId(questionId);
                    answer.setContent(resultSet.getString(3));
                    answer.setFromId(resultSet.getInt(4));
                    answer.setDate(DateFormatter.doFormat(resultSet.getString(5)));
                    answer.setSubAnswers(answerRepository.getSubAnswersByAnswerId(answer.getAnswerId(),1));
                    User user = userRepository.getUserByUserId(answer.getFromId());
                    answer.setFromName(user.getNickName());
                    answer.setAnsOrder(resultSet.getInt(7));
                    answer.setImages(fileRepository.getPictureURLs(questionId,answer.getAnswerId()));
                    answer.setFromRole(userRepository.getRoleByUserId(answer.getFromId()));
                    return answer;
                });
    }

    public Question getQuerstionByQuestionId(Integer questionId){
        List<Question> questions = jdbcTemplate.query("select * from question where question_id=?",
                preparedStatement -> preparedStatement.setInt(1, questionId), (resultSet, i) -> getQuestion(resultSet));
        return questions.size()>0?questions.get(0):null;
    }
    public Question getQuestion(ResultSet resultSet) throws SQLException {
        Question question = new Question();
        question.setQuestionId(resultSet.getInt(1));
        question.setTitle(resultSet.getString(2));
        question.setContent(resultSet.getString(3));
        question.setUserId(resultSet.getInt(4));
        question.setLastUpdate(resultSet.getString(5));
        question.setTakenId(resultSet.getInt(6));
        question.setAuthorName(userRepository.getUserByUserId(question.getUserId()).getNickName());
        question.setDate(DateFormatter.doFormat(resultSet.getString(7)));
        question.setRead(resultSet.getInt(8));
        question.setImageUrls(fileRepository.getPictureURLs(question.getQuestionId(),0));
        question.setUserRole(userRepository.getRoleByUserId(question.getUserId()));
        return question;
    }
    public Integer getAuthorIdByQuestionId(Integer questionId){
        List<Integer> integers = jdbcTemplate.query("select uid from question where question_id=?",
                preparedStatement -> preparedStatement.setInt(1, questionId), (resultSet, i) -> resultSet.getInt(1));
        return integers.size()>0?integers.get(0):null;
    }
    public Integer getTakenIdByQuestionId(Integer qid){
        List<Integer> integers = jdbcTemplate.query("select taken_id from question where question_id=?",
                preparedStatement -> preparedStatement.setInt(1, qid), (resultSet, i) -> resultSet.getInt(1));
        return integers.get(0);
    }

    /**
     *
     * @param limit 返回的数量
     * @param offset 页码数
     * @param keyWord 关键字
     * @param searchWay 搜索选项
     * @return
     */
    public List<Comment> getComment(int limit, int offset, String keyWord, int searchWay) {
        String sql = "select qid,qtt,qct,tkid,dt,c.name,i.name inst,t.name,aid,act,t.tid from (select dt,aid,aa.qid,act,tid,qct,qtt,tkid from " +
                "(select a.answer_id aid, a.question_id qid,a.content act,a.from_id tid from answer a inner join teacher t on a.from_id=t.tid) aa " +
                "inner join (select q.date dt,q.question_id qid,q.taken_id tkid, q.content qct,q.title qtt " +
                "from question q) bb on aa.qid = bb.qid) ab inner join teacher t on ab.tid = t.tid inner join " +
                "institution i on t.institution_id=i.institution_id inner join course_teacher_takes ctk on " +
                "ctk.taken_id=tkid inner join course c on ctk.course_id=c.course_id where ";
        String order="";
        switch (searchWay){
            case Parameter.SEARCH_BY_CONTENT:order="qct like '%"+keyWord+"%' or act like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_COURSE:order=" c.name like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_INSTITUTION:order="i.name like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_TEACHER:order="t.name like '%"+keyWord+"%'";break;
            default:break;
        }
        String size = " order by dt desc limit " + offset + "," + limit + ";";
        List<Comment> comments = jdbcTemplate.query(sql+order+size, (resultSet, i) -> {
            Comment comment = new Comment();
            comment.setQuestionId(resultSet.getInt(1));
            comment.setTitle(resultSet.getString(2));
            comment.setQuestionContent(resultSet.getString(3));
            comment.setTakenId(resultSet.getInt(4));
            comment.setDate(DateFormatter.doFormat(resultSet.getString(5)));
            comment.setCourseName(resultSet.getString(6));
            comment.setInstitution(resultSet.getString(7));
            comment.setTeacherName(resultSet.getString(8));
            comment.setAnswerId(resultSet.getInt(9));
            comment.setAnswerContent(resultSet.getString(10));
            comment.setTeacherId(resultSet.getInt(11));
            comment.setHref("/course/"+comment.getTakenId()+"/question/"
                    +comment.getQuestionId()+"#qa"+(comment.getAnswerId()-1));
            return comment;
        });
        return comments;
    }

    public void deleteAnswer(Integer aId) {
        jdbcTemplate.execute("delete from sub_answer where answer_id="+aId);
        jdbcTemplate.execute("delete from answer where answer_id="+aId);
    }

    public void deleteQuestion(Integer qId) {
        jdbcTemplate.execute("delete from sub_answer where answer_id in (" +
                "select a.answer_id from answer a where a.question_id="+qId+");");
        jdbcTemplate.execute("delete from answer where question_id="+qId);
        jdbcTemplate.execute("delete from question where question_id="+qId);
    }

    public void deleteSubAnswer(Integer sbId) {
        jdbcTemplate.execute("delete from sub_answer where sub_ans_id="+sbId);
    }

    public void setRead(Integer questionId) {
        jdbcTemplate.update("update question set `read`=1 where question_id="+questionId);
    }

    public void updateAnserContent(Integer answerId, String content) {
        jdbcTemplate.update("update answer set content = '"+content+"' where answer_id="+answerId);
    }

    public Integer getCommentSize(String keyWord, int searchWay) {
        String sql = "select count(*) from (select dt,aid,aa.qid,act,tid,qct,qtt,tkid from " +
                "(select a.answer_id aid, a.question_id qid,a.content act,a.from_id tid from answer a inner join teacher t on a.from_id=t.tid) aa " +
                "inner join (select q.date dt,q.question_id qid,q.taken_id tkid, q.content qct,q.title qtt " +
                "from question q) bb on aa.qid = bb.qid) ab inner join teacher t on ab.tid = t.tid inner join " +
                "institution i on t.institution_id=i.institution_id inner join course_teacher_takes ctk on " +
                "ctk.taken_id=tkid inner join course c on ctk.course_id=c.course_id where ";
        String order="";
        switch (searchWay){
            case Parameter.SEARCH_BY_CONTENT:order="qct like '%"+keyWord+"%' or act like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_COURSE:order=" c.name like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_INSTITUTION:order="i.name like '%"+keyWord+"%'";break;
            case Parameter.SEARCH_BY_TEACHER:order="t.name like '%"+keyWord+"%'";break;
            default:break;
        }
        List<Integer> integers = jdbcTemplate.query(sql + order, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
        return integers.get(0);
    }
}
