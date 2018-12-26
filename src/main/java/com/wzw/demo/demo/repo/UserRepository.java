package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.vo.CourseItem;
import com.wzw.demo.demo.vo.Institution;
import com.wzw.demo.demo.util.Parameter;
import com.wzw.demo.demo.vo.User;
import com.wzw.demo.demo.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public User getUserByUserId(Integer uid){
        List<User> users = jdbcTemplate.query("select * from user where uid=?",
                preparedStatement -> preparedStatement.setInt(1, uid),
                (resultSet, i) -> getUser(resultSet));
        return users.size()>0?users.get(0):null;
    }
    public UserInfo getUserInfoById(Integer uid){
        Integer role = getUserByUserId(uid).getRole();
        if(role.equals(Parameter.TEACHER)){
            return getTeacherInfo(uid);
        }
        if( role.equals(Parameter.STUDENT))
            return getStudentInfo(uid);
        return null;
    }
    public Integer getNumberByNickName(String nickName){
        List<Integer> users = jdbcTemplate.query("select count(*) from `user` where nickname=?",
                preparedStatement -> {
                    preparedStatement.setString(1, nickName);
                },(resultSet, i) -> resultSet.getInt(1));
        return users.size()>0?users.get(0):null;
    }
    public UserInfo getTeacherInfo(Integer id){
        List<UserInfo> userInfos = jdbcTemplate.query("select u.uid, u.password, u.nickname," +
                        " t.name, t.title,t.abstract, i.name,t.school,t.institution_id from `user` u inner " +
                        "join teacher t on u.uid=t.tid left join institution i on " +
                        "t.institution_id=i.institution_id where u.uid=?",
                preparedStatement -> preparedStatement.setInt(1, id), (resultSet, i) -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUid(resultSet.getInt(1));
                    userInfo.setPassword(resultSet.getString(2));
                    userInfo.setNickName(resultSet.getString(3));
                    userInfo.setRealName(resultSet.getString(4));
                    userInfo.setTitle(resultSet.getString(5));
                    userInfo.setIntro(resultSet.getString(6));
                    userInfo.setInstitution(resultSet.getString(7));
                    userInfo.setSchool(resultSet.getString(8));
                    userInfo.setInstId(resultSet.getInt(9));
                    return userInfo;
                });
        return userInfos.get(0);
    }
    public UserInfo getStudentInfo(Integer id){
        List<UserInfo> userInfos = jdbcTemplate.query("select " +
                        " u.nickname,s.name,s.school,i.name,s.inst_id from `user` u inner join " +
                        "student s on u.uid = s.uid left join institution i on s.inst_id" +
                        " = i.institution_id where u.uid=?",
                preparedStatement -> preparedStatement.setInt(1, id), (resultSet, i) -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setNickName(resultSet.getString(1));
                    userInfo.setRealName(resultSet.getString(2));
                    userInfo.setSchool(resultSet.getString(3));
                    userInfo.setInstitution(resultSet.getString(4));
                    userInfo.setInstId(resultSet.getInt(5));
                    return userInfo;
                });
        return userInfos.get(0);
    }
    public List<UserInfo> getAllStudentInfos(int offset, int size, int takesId){
        List<UserInfo> userInfos = jdbcTemplate.query("select " +
                        " u.nickname,s.name,s.school,i.name,s.inst_id,u.uid from `user` u inner join " +
                        "student s on u.uid = s.uid left join institution i on s.inst_id" +
                        " = i.institution_id where u.role=1 limit ?,?",
                preparedStatement -> {
                    preparedStatement.setInt(1,offset);
                    preparedStatement.setInt(2,size);
                }, (resultSet, i) -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setNickName(resultSet.getString(1));
                    userInfo.setRealName(resultSet.getString(2));
                    userInfo.setSchool(resultSet.getString(3));
                    userInfo.setInstitution(resultSet.getString(4));
                    userInfo.setInstId(resultSet.getInt(5));
                    userInfo.setStuId(resultSet.getInt(6));
                    if(isBannedPost(userInfo.getStuId(),takesId))
                        userInfo.setPost(0);
                    else
                        userInfo.setPost(1);
                    if(isBannedView(userInfo.getStuId(),takesId))
                        userInfo.setView(0);
                    else
                        userInfo.setView(1);
                    return userInfo;
                });
        return userInfos;

    }

    public boolean isBannedView(Integer uid, int takesId) {
        List<Integer> integers = jdbcTemplate.query("select * from bannedRead where (?=0 or takes_id=?" +
                ") and stu_id=?", preparedStatement -> {
            preparedStatement.setInt(1, takesId);
            preparedStatement.setInt(2, takesId);
            preparedStatement.setInt(3, uid);
        }, (resultSet, i) -> resultSet.getInt(1));
        return integers.size()!=0;
    }

    public boolean isBannedPost(Integer uid, int takesId) {
        List<Integer> integers = jdbcTemplate.query("select * from bannedPost where (takes_id=? " +
                "or ?=0)" +
                " and stu_id=?", preparedStatement -> {
            preparedStatement.setInt(1, takesId);
            preparedStatement.setInt(2, takesId);
                    preparedStatement.setInt(3, uid);
                }, (resultSet, i) -> resultSet.getInt(1));
        return integers.size()!=0;
    }

    public User getUserByUserName(String userName){
        List<User> users = jdbcTemplate.query("select * from user where username=?",
                preparedStatement -> preparedStatement.setString(1, userName),
                (resultSet, i) -> getUser(resultSet));
        return users.size()>0?users.get(0):null;
    }
    public List<User> getAllUsers(){
        return jdbcTemplate.query("select * from user;", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                return getUser(resultSet);
            }
        });
    }
    public boolean checkPass(String password,Integer uid){
        String ps = getUserByUserId(uid).getPassword();
        return ps.equals(password);
    }
    public List<Institution> getInsti(){
        return jdbcTemplate.query("select institution_id,`name` from institution;", (resultSet, i) -> {
            Institution institution = new Institution();
            institution.setInstitutionId(resultSet.getInt(1));
            institution.setName(resultSet.getString(2));
            return institution;
        });
    }
    public void updateUserInfo(UserInfo userInfo, Integer role){
        jdbcTemplate.update("update `user` set nickname=? where uid = ?", preparedStatement -> {
            preparedStatement.setString(1,userInfo.getNickName());
            preparedStatement.setInt(2,userInfo.getUid());
        });
        if(role.equals(Parameter.STUDENT)){
            jdbcTemplate.update("update student set `name` = ?,school=?,inst_id=? where uid=?",
                    preparedStatement -> {
                        preparedStatement.setString(1,userInfo.getSchool());
                        preparedStatement.setString(2,userInfo.getSchool());
                        preparedStatement.setInt(3,userInfo.getInstId());
                        preparedStatement.setInt(4,userInfo.getUid());
                    });
        }
        if(role.equals(Parameter.TEACHER)){
            jdbcTemplate.update("update teacher set `name`=?,title=?,institution_id=?,school=? where " +
                    "tid=?;", preparedStatement -> {
                        preparedStatement.setString(1,userInfo.getRealName());
                        preparedStatement.setString(2,userInfo.getTitle());
                        preparedStatement.setInt(3,userInfo.getInstId());
                        preparedStatement.setString(4,userInfo.getSchool());
                        preparedStatement.setInt(5,userInfo.getUid());
                    });
        }
    }
    public void register(User user, Integer role){
        jdbcTemplate.update("insert into `user`(role,username,nickname,password) values(?,?,?,?)",
                preparedStatement -> {
                    preparedStatement.setInt(1,role);
                    preparedStatement.setString(2,user.getUserName());
                    preparedStatement.setString(3,user.getNickName());
                    preparedStatement.setString(4,user.getPassword());
                });
        if(role.equals(Parameter.STUDENT)){
            jdbcTemplate.update("insert into student(uid) values ("+
                    getUserByUserName(user.getUserName()).getUid()+")");
        }
    }
    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUid(resultSet.getInt(1));
        user.setRole(resultSet.getInt(2));
        user.setUserName(resultSet.getString(3));
        user.setNickName(resultSet.getString(4));
        user.setPassword(resultSet.getString(5));
        return user;
    }

    public void addUnread(Integer uid) {
        jdbcTemplate.update("update `user` set unread = unread+1 where uid=?",
                preparedStatement -> preparedStatement.setInt(1,uid));
    }

    public void readAll(Integer uid) {
        jdbcTemplate.update("update `user` set unread = 0 where uid=?",
                preparedStatement -> preparedStatement.setInt(1,uid));
    }

    public Integer getStudentCount() {
        List<Integer> integers = jdbcTemplate.query("select count(*) from student;", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        });
        return integers.get(0);
    }

    public void allowStudentsToPostTakes(Integer[] id, Integer takesId) {
        jdbcTemplate.batchUpdate("delete from bannedPost where takes_id=? and stu_id=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1,takesId);
                preparedStatement.setInt(2,id[i]);
            }

            @Override
            public int getBatchSize() {
                return id.length;
            }
        });
    }

    public void banStudentsToPostTakes(Integer[] id, Integer takesId) {
        jdbcTemplate.batchUpdate("replace into bannedPost values(?,?);", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1,takesId);
                preparedStatement.setInt(2,id[i]);
            }

            @Override
            public int getBatchSize() {
                return id.length;
            }
        });
    }

    public void allowStudentsToViewTakes(Integer[] id, Integer takesId) {
        jdbcTemplate.batchUpdate("delete from bannedRead where takes_id=? and stu_id=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1,takesId);
                preparedStatement.setInt(2,id[i]);
            }

            @Override
            public int getBatchSize() {
                return id.length;
            }
        });
    }

    public void banStudentsToViewTakes(Integer[] id, Integer takesId) {
        jdbcTemplate.batchUpdate("replace into bannedRead values(?,?);", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1,takesId);
                preparedStatement.setInt(2,id[i]);
            }

            @Override
            public int getBatchSize() {
                return id.length;
            }
        });
    }

    public List<CourseItem> getTakesByTid(Integer tid) {
        return jdbcTemplate.query("select c.name,ct.taken_id,ct.teacher_id" +
                        " from course_teacher_takes ct "+
                " inner join course c on  c.course_id=ct.course_id " +
                        " inner join teacher t on t.tid=ct.teacher_id where ct.teacher_id=" + tid,
                new RowMapper<CourseItem>() {
                    @Override
                    public CourseItem mapRow(ResultSet resultSet, int i) throws SQLException {
                        CourseItem courseItem = new CourseItem();
                        courseItem.setCourseName(resultSet.getString(1));
                        courseItem.setTakenId(resultSet.getInt(2));
                        courseItem.setTeacherId(resultSet.getInt(3));
                        return courseItem;
                    }
                });
    }

    public Integer getRoleByUserId(Integer userId) {
        User user = getUserByUserId(userId);
        return user.getRole();
    }

    public void updatePassword(String newPassword, Integer uid) {
        jdbcTemplate.update("update `user` set password='"+newPassword+"' where uid="+uid);
    }
}
