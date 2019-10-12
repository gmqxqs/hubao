package com.yunbao.phonelive.bean;

public class PhotoBean {
    private String cover;
    private String id;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PhotoBean{" +
                "cover='" + cover + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
