package com.wzw.demo.demo.vo;

public class MessageItem {
    private String content,date,relyer,myContent;
    private Integer read,answerId,replyId,questionId,takenId;

    public Integer getTakenId() {
        return takenId;
    }

    public void setTakenId(Integer takenId) {
        this.takenId = takenId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getMyContent() {
        return myContent;
    }

    public void setMyContent(String myContent) {
        this.myContent = myContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRelyer() {
        return relyer;
    }

    public void setRelyer(String relyer) {
        this.relyer = relyer;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }
}
