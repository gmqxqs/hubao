package com.yunbao.phonelive.event;

public class VideoPauseEvent {
    private boolean mPause;
    public VideoPauseEvent(boolean mPause){
        this.mPause = mPause;
    }
    public void setPause(boolean mPause){
        this.mPause = mPause;
    }
    public boolean getPause(){
        return mPause;
    }
}
