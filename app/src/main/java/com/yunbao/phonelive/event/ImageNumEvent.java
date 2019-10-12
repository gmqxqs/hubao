package com.yunbao.phonelive.event;

public class ImageNumEvent {
    private String mCurrentNum;
    private String mTotalNaum;
    public  ImageNumEvent(String mCurrentNum,String mTotalNaum){
        this.mCurrentNum = mCurrentNum;
        this.mTotalNaum = mTotalNaum;
    }

    public String getmCurrentNum() {
        return mCurrentNum;
    }

    public void setmCurrentNum(String mCurrentNum) {
        this.mCurrentNum = mCurrentNum;
    }

    public String getmTotalNaum() {
        return mTotalNaum;
    }

    public void setmTotalNaum(String mTotalNaum) {
        this.mTotalNaum = mTotalNaum;
    }

    @Override
    public String toString() {
        return "ImageNumEvent{" +
                "mCurrentNum='" + mCurrentNum + '\'' +
                ", mTotalNaum='" + mTotalNaum + '\'' +
                '}';
    }
}
