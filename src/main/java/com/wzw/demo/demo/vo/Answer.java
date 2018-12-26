package com.wzw.demo.demo.vo;

import java.util.List;

public class Answer {
    private Integer questionId,answerId;//在哪一个问题下的回答
    private String content;
    private Integer fromId,fromRole;//回复人的id
    private String date,fromName;//回复时间
    private List<SubAnswer> subAnswers;
    private Integer ansOrder;
    private String jsonSubAnswers;
    private List<Image> images;

    public Integer getFromRole() {
        return fromRole;
    }

    public void setFromRole(Integer fromRole) {
        this.fromRole = fromRole;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getJsonSubAnswers() {
        return jsonSubAnswers;
    }

    public void setJsonSubAnswers(String jsonSubAnswers) {
        this.jsonSubAnswers = jsonSubAnswers;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Integer getAnsOrder() {
        return ansOrder;
    }

    public void setAnsOrder(Integer ansOrder) {
        this.ansOrder = ansOrder;
    }

    public List<SubAnswer> getSubAnswers() {
        return subAnswers;
    }

    public void setSubAnswers(List<SubAnswer> subAnswers) {
        this.subAnswers = subAnswers;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
