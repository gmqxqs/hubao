package com.yunbao.phonelive.views;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.VideoScrollAdapter;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.VideoLoadingBar;
import com.yunbao.phonelive.event.VideoLikeEvent;
import com.yunbao.phonelive.event.VideoPauseEvent;
import com.yunbao.phonelive.event.VideoPlayEvent;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by cxf on 2018/11/30.
 * 视频播放器
 */

public class VideoPlayViewHolder extends AbsViewHolder implements ITXVodPlayListener, View.OnClickListener {

    private TXCloudVideoView mTXCloudVideoView;
    private View mVideoCover;
    private TXVodPlayer mPlayer;
    private boolean mPaused;//生命周期暂停
    public boolean getPaused(){
        return mClickPaused;
    }
    private boolean mClickPaused;//点击暂停
    private ActionListener mActionListener;
    private View mPlayBtn;
    private ObjectAnimator mPlayBtnAnimator;//暂停按钮的动画
    private boolean mStartPlay;
    private boolean mEndPlay;
    private VideoBean mVideoBean;
    private VideoScrollAdapter mVideoScrollAdapter;
    private String mVideoKey;
    private List<VideoBean> list;
    private int mPosition;
    private int mPage;

    public VideoPlayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);

    }

    public VideoPlayViewHolder(Context context, ViewGroup parentView, int position, String videoKey, int page) {
        super(context, parentView, position, videoKey, page);
    }

    @Override
    protected void processArguments(Object... args) {
        mPosition = (int) args[0];
        mVideoKey = (String) args[1];
        mPage = (int) args[2];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_play;
    }

    @Override
    public void init() {
        /*List<VideoBean> list = VideoStorge.getInstance().get(mVideoKey);
        if (list == null || list.size() == 0) {
            return;
        }*/

        list = VideoStorge.getInstance().get(mVideoKey);
        mVideoScrollAdapter = new VideoScrollAdapter(mContext, list, mPosition);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig config = new TXVodPlayConfig();
        config.setCacheFolderPath(mContext.getCacheDir().getAbsolutePath());
        config.setMaxCacheItems(15);
        mPlayer.setConfig(config);
        mPlayer.setAutoPlay(true);
        mPlayer.setVodListener(this);
        mPlayer.setPlayerView(mTXCloudVideoView);
        findViewById(R.id.root).setOnClickListener(this);
        mVideoCover = findViewById(R.id.video_cover);
        mPlayBtn = findViewById(R.id.btn_play);
        //暂停按钮动画
        mPlayBtnAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayBtn,
                PropertyValuesHolder.ofFloat("scaleX", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 4f, 0.8f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mPlayBtnAnimator.setDuration(150);
        mPlayBtnAnimator.setInterpolator(new AccelerateInterpolator());
    }

    /**
     * 播放器事件回调
     */
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://加载完成，开始播放的回调
                mStartPlay = true;
                if (mActionListener != null) {
                    mActionListener.onPlayBegin();
                }
                L.e("加载完成","PLAY_EVT_PLAY_BEGIN");
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING: //开始加载的回调
                if (mActionListener != null) {
                    mActionListener.onPlayLoading();
                }
                L.e("开始加载","PLAY_EVT_PLAY_LOADING");
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://获取到视频播放完毕的回调
                replay();
                if (!mEndPlay) {
                    mEndPlay = true;
                    if (mVideoBean != null) {
                        HttpUtil.videoWatchEnd(mVideoBean.getUid(), mVideoBean.getId());
                    }
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://获取到视频首帧回调
                if (mActionListener != null) {
                    mActionListener.onFirstFrame();
                }
                L.e("首帧","PLAY_EVT_RCV_FIRST_I_FRAME");

                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取到视频宽高回调
                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
                L.e("宽高","PLAY_EVT_RCV_FIRST_I_FRAME");
                break;
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }


    /**
     * 获取到视频宽高回调
     */
    public void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
            int targetH = 0;
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
            } else {
                targetH = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (targetH != params.height) {
                params.height = targetH;
                mTXCloudVideoView.requestLayout();
            }
            if (mVideoCover != null && mVideoCover.getVisibility() == View.VISIBLE) {
                mVideoCover.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 开始播放
     */
    public void startPlay(VideoBean videoBean) {
        mStartPlay = false;
        mClickPaused = false;
        mEndPlay = false;
        mVideoBean = videoBean;
        if (mVideoCover != null && mVideoCover.getVisibility() != View.VISIBLE) {
            mVideoCover.setVisibility(View.VISIBLE);
        }
        hidePlayBtn();
        L.e("播放视频--->" + videoBean);
        if (videoBean == null) {
            return;
        }
        String url = videoBean.getHref();

        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mPlayer != null) {
            mPlayer.startPlay(url);
        }
        HttpUtil.videoWatchStart(videoBean.getUid(), videoBean.getId());
        EventBus.getDefault().post(new VideoPlayEvent(mPaused));
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
        }
    }

    /**
     * 循环播放
     */
    public void replay() {
        if (mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    public void release() {
        HttpUtil.cancel(HttpConsts.VIDEO_WATCH_START);
        HttpUtil.cancel(HttpConsts.VIDEO_WATCH_END);
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        mPlayer = null;
        mActionListener = null;
    }


    /**
     * 生命周期暂停
     */
    public void pausePlay() {
        mPaused = true;
        if (!mClickPaused && mPlayer != null) {
            mPlayer.pause();
        }
    }


    /**
     * 生命周期恢复
     */
    public void resumePlay() {
        if (mPaused) {
            if (!mClickPaused && mPlayer != null) {
                mPlayer.resume();
            }
        }
        mPaused = false;
    }

    /**
     * 显示开始播放按钮
     */
    private void showPlayBtn() {
        if (mPlayBtn != null && mPlayBtn.getVisibility() != View.VISIBLE) {
            mPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏开始播放按钮
     */
    private void hidePlayBtn() {
        if (mPlayBtn != null && mPlayBtn.getVisibility() == View.VISIBLE) {
            mPlayBtn.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 点击切换播放和暂停
     */
    private void clickTogglePlay() {
        if (!mStartPlay) {
            return;
        }
        if (mPlayer != null) {
            if (mClickPaused) {
                mPlayer.resume();
            } else {
                mPlayer.pause();
            }
        }
        mClickPaused = !mClickPaused;
        if (mClickPaused) {
            showPlayBtn();
            if (mPlayBtnAnimator != null) {
                mPlayBtnAnimator.start();
            }
        } else {
            hidePlayBtn();
        }
        EventBus.getDefault().post(new VideoPauseEvent(mPaused));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                clickTogglePlay();
                break;
        }
    }


    public interface ActionListener {
        void onPlayBegin();

        void onPlayLoading();

        void onFirstFrame();
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
