package com.wzw.demo.demo.vo;

import java.util.List;

public class Question {
    private Integer questionId,userId;//发布者id
    private String title;//问题的标题
    private String content;
    private String lastUpdate;//最后一次操作的时间,用于排序
    private Integer takenId;//所处在哪个课程板块下
    private String authorName;
    private String date;
    private Integer answerNum,read;
    private List<Image> imageUrls;
    private Integer userRole;

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    public List<Image> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<Image> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getTakenId() {
        return takenId;
    }

    public void setTakenId(Integer takenId) {
        this.takenId = takenId;
    }
}
