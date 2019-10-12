package com.yunbao.phonelive.bean;

import java.util.List;

public class ImageBean {
    private String title;
    private List<PhotoBean> href;
    private String id;
    private String userNiceName;
    private String thumb;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PhotoBean> getHref() {
        return href;
    }

    public void setHref(List<PhotoBean> href) {
        this.href = href;
    }
}
