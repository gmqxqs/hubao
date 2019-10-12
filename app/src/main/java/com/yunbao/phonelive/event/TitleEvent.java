package com.yunbao.phonelive.event;

public class TitleEvent {
    private String title;
    public TitleEvent(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
