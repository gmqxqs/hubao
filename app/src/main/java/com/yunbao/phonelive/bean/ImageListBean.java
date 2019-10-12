package com.yunbao.phonelive.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class ImageListBean {
   private int code;
   private String datetime;
   private String id;
   private String title;
   private String cover;
   private String total;
   private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    @JSONField(name = "cover")
    public String getCover() {
        return cover;
    }
    @JSONField(name = "cover")
    public void setCover(String cover) {
        this.cover = cover;
    }
    @JSONField(name = "total")
    public String getTotal() {
        return total;
    }
    @JSONField(name = "total")
    public void setTotal(String total) {
        this.total = total;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
