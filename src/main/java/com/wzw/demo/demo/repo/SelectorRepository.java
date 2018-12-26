package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.vo.CourseItem;
import com.wzw.demo.demo.vo.SelectorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * 有的需要有id
 */
@Repository
public class SelectorRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<SelectorItem> getYears(){
        List<SelectorItem> list = jdbcTemplate.query("select count(*),`year` from course_teacher_takes group by `year`;",
                (resultSet, i) -> {
                    SelectorItem selectorItem = new SelectorItem();
                    selectorItem.setNumber(resultSet.getInt(1));
                    selectorItem.setId(resultSet.getInt(2));
                    selectorItem.setName(resultSet.getInt(2)+"年");
                    return selectorItem;
                });
        list.sort(Comparator.comparing(SelectorItem::getName));
        return list;
    }
    public List<SelectorItem> getInstitutions(){
        List<SelectorItem> list = jdbcTemplate.query("select count(*),`institution_id`,`name` from course_teacher_takes c inner join " +
                        "course on course.course_id=c.course_id group by `institution_id`;",
                (resultSet, i) -> {
                    SelectorItem selectorItem = new SelectorItem();
                    selectorItem.setNumber(resultSet.getInt(1));
                    selectorItem.setId(resultSet.getInt(2));
                    selectorItem.setName(resultSet.getString(3));
                    return selectorItem;
                });
        list.sort(Comparator.comparing(SelectorItem::getName));
        return list;
    }
    public List<SelectorItem> getSections(){
        String[] names = new String[]{"","春季","夏季","秋季","冬季"};
        List<SelectorItem> list =  jdbcTemplate.query("select count(*),`section` from course_teacher_takes" +
                        " group by `section`;",
                (resultSet, i) -> {
                    return getSelectorItem(names, resultSet);
                });
        list.sort(Comparator.comparing(SelectorItem::getId));
        return list;
    }
    public List<SelectorItem> getTeachers(Integer institutionId){
        List<SelectorItem> list =   jdbcTemplate.query("select distinct t.tid,t.name from course_teacher_takes c" +
                        " inner join teacher t on c.teacher_id=t.tid where t.institution_id=? or ?=0;",
                preparedStatement -> {
                    preparedStatement.setInt(1,institutionId);
                    preparedStatement.setInt(2,institutionId);
                },
                (resultSet, i) -> {
                    SelectorItem selectorItem = new SelectorItem();
                    selectorItem.setId(resultSet.getInt(1));
                    selectorItem.setName(resultSet.getString(2));
                    return selectorItem;
                });
        list.sort(Comparator.comparing(SelectorItem::getName));
        return list;
    }
    public List<SelectorItem> getAcitivated(){
        String[] names = new String[]{"","已完成","进行中"};
        List<SelectorItem> list =  jdbcTemplate.query("select count(*),`activated` from course_teacher_takes" +
                        " group by `activated`;",
                (resultSet, i) -> getSelectorItem(names, resultSet));
        list.sort(Comparator.comparing(SelectorItem::getId));
        return list;
    }

    private SelectorItem getSelectorItem(String[] names, ResultSet resultSet) throws SQLException {
        SelectorItem selectorItem = new SelectorItem();
        selectorItem.setNumber(resultSet.getInt(1));
        selectorItem.setId(resultSet.getInt(2));
        selectorItem.setName(names[selectorItem.getId()]);
        return selectorItem;
    }

    static String[] sectionNames = new String[]{"","春季","夏季","秋季","冬季"};
    static String[] activatedName = new String[]{"","进行中","已完成"};
    public List<CourseItem> getSelectedItems(int institution,int teacher,int year,
                                             int section,int activated,String course){
        return jdbcTemplate.query("select c.taken_id,c.course_id,c.teacher_id,c.year,c.section,c.activated" +
                ",i.institution_id,i.name,t.name,cc.name from course_teacher_takes c inner join teacher t on c.teacher_id=t.tid " +
                " inner join course cc on c.course_id = cc.course_id inner join institution i on i.institution_id = " +
                " cc.institution_id where " +
                "(?=0 or ?=t.institution_id) and" +
                "(?=0 or ?=c.teacher_id) and" +
                        "(?=0 or ?=c.year) and" +
                        "(?=0 or ?=c.section) and" +
                        "(?=c.activated or 3=?) and (?='0' or cc.name like ?)", preparedStatement -> {
                            preparedStatement.setInt(1, institution);
                            preparedStatement.setInt(2, institution);
                            preparedStatement.setInt(3, teacher);
                            preparedStatement.setInt(4, teacher);
                            preparedStatement.setInt(5, year);
                            preparedStatement.setInt(6, year);
                            preparedStatement.setInt(7, section);
                            preparedStatement.setInt(8, section);
                            preparedStatement.setInt(9, activated);
                            preparedStatement.setInt(10, activated);
                            preparedStatement.setString(11,course);
                            preparedStatement.setString(12,"%"+course+"%");
                        }, (resultSet, i) -> getCourseItem(resultSet));
    }

    private CourseItem getCourseItem(ResultSet resultSet) throws SQLException {
        CourseItem courseItem = new CourseItem();
        courseItem.setTakenId(resultSet.getInt(1));
        courseItem.setCourseId(resultSet.getInt(2));
        courseItem.setTeacherId(resultSet.getInt(3));
        courseItem.setYear(resultSet.getInt(4));
        courseItem.setSection(resultSet.getInt(5));
        courseItem.setActivated(resultSet.getInt(6));
        courseItem.setInstitutionId(resultSet.getInt(7));
        courseItem.setInstitution(resultSet.getString(8));
        courseItem.setSectionName(sectionNames[courseItem.getSection()]);
        courseItem.setTeacherName(resultSet.getString(9));
        courseItem.setActivatedName(activatedName[courseItem.getActivated()]);
        courseItem.setCourseName(resultSet.getString(10));
        courseItem.setHref("/course/"+courseItem.getTakenId()+"/");
        return courseItem;
    }

    public CourseItem getCourseByTakenId(Integer courseId){
        List<CourseItem> courseItems = jdbcTemplate.query("select c.taken_id,c.course_id,c.teacher_id,c.year,c.section,c.activated" +
                        ",i.institution_id,i.name,t.name,cc.abstract,cc.name from course_teacher_takes c inner join teacher t on c.teacher_id=t.tid " +
                        " inner join course cc on c.course_id = cc.course_id inner join institution i on i.institution_id = " +
                        " cc.institution_id where c.taken_id=?",
                preparedStatement -> {
                    preparedStatement.setInt(1, courseId);
                }, (resultSet, i) -> {
                    CourseItem courseItem = new CourseItem();
                    courseItem.setTakenId(resultSet.getInt(1));
                    courseItem.setCourseId(resultSet.getInt(2));
                    courseItem.setTeacherId(resultSet.getInt(3));
                    courseItem.setYear(resultSet.getInt(4));
                    courseItem.setSection(resultSet.getInt(5));
                    courseItem.setActivated(resultSet.getInt(6));
                    courseItem.setInstitutionId(resultSet.getInt(7));
                    courseItem.setInstitution(resultSet.getString(8));
                    courseItem.setSectionName(sectionNames[courseItem.getSection()]);
                    courseItem.setTeacherName(resultSet.getString(9));
                    courseItem.setActivatedName(activatedName[courseItem.getActivated()]);
                    courseItem.setIntroduction(resultSet.getString(10));
                    courseItem.setCourseName(resultSet.getString(11));
                    return courseItem;
                });
        return courseItems.size()>0?courseItems.get(0):null;
    }
}
