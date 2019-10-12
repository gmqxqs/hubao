package com.yunbao.phonelive.event;

public class VideoPlayEvent {
    private boolean mPause;
    public VideoPlayEvent(boolean mPause){
        this.mPause = mPause;
    }
    public void setPause(boolean mPause){
        this.mPause = mPause;
    }
    public boolean getPause(){
        return mPause;
    }
}
