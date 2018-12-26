package com.wzw.demo.demo.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public Integer getNewsNumber(Integer uid){
        List<Integer> unread = jdbcTemplate.query("select unread from `user` where uid=?",
                preparedStatement -> preparedStatement.setInt(1, uid),
                (resultSet, i) -> resultSet.getInt(1));
        return unread.get(0);
    }
}
