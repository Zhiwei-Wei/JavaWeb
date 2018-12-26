package com.wzw.demo.demo.repo;

import com.wzw.demo.demo.vo.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public void addFile(MultipartFile file, String type, Integer questionId, Integer answerId, int order) throws IOException {//图片保存到数据库
        final InputStream fis = file.getInputStream();
        jdbcTemplate.execute("insert into files values (?,?,?,?,?,?)",
                (PreparedStatementCallback<Object>) stmt -> {
                    stmt.setInt(1,answerId);
                    stmt.setBinaryStream(2, fis);//将图片保存
                    System.out.println("文件名为："+file.getOriginalFilename());
                    stmt.setString(3, file.getOriginalFilename());
                    stmt.setString(4,type);
                    stmt.setInt(5,questionId);
                    stmt.setInt(6,order);
                    stmt.execute();
                    return null;
                });
    }

    public InputStream getPictures(Integer qId,Integer aId,int order) {//读取数据库内保存图片
        String sql = "select * from files where question_id="+qId+" and ans_id="+aId+" and `order`="+order;
        return jdbcTemplate.execute(sql, new CallableStatementCallback<InputStream>() {
            public InputStream doInCallableStatement(CallableStatement stmt)
                    throws SQLException, DataAccessException {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    InputStream inputStream = rs.getBinaryStream("files");
                    return inputStream;
                }
                return null;
            }
        });
    }

    public List<Image> getPictureURLs(Integer questionId, Integer answerId) {
        List<Image> urls = jdbcTemplate.query("select question_id,ans_id,`order`,file_name from files where ans_id=? and question_id=?",
                preparedStatement -> {
                    preparedStatement.setInt(1, answerId);
                    preparedStatement.setInt(2, questionId);
                }, (resultSet, i) -> {
                    Image image = new Image();
                    image.setFileName(resultSet.getString(4));
                    image.setUrl("/course/download/"+resultSet.getInt(1)+"/"
                            +resultSet.getInt(2)+"/"+resultSet.getInt(3));
                    return image;
                });
        return urls.size()>0?urls:null;
    }
}