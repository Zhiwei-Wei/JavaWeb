package com.wzw.demo.demo.vo;

public class SubAnswer {
    private Integer answerId;//隶属于哪个回复
    private Integer fromId,toId;//回复人的id和要回复的对象的id
    private String date;//回复时间
    private Integer subAnswerOrder,subAnswerId,fromRole;
    private String fromName,toName,content;

    public Integer getFromRole() {
        return fromRole;
    }

    public void setFromRole(Integer fromRole) {
        this.fromRole = fromRole;
    }

    public Integer getSubAnswerId() {
        return subAnswerId;
    }

    public void setSubAnswerId(Integer subAnswerId) {
        this.subAnswerId = subAnswerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public Integer getSubAnswerOrder() {
        return subAnswerOrder;
    }

    public void setSubAnswerOrder(Integer subAnswerOrder) {
        this.subAnswerOrder = subAnswerOrder;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
