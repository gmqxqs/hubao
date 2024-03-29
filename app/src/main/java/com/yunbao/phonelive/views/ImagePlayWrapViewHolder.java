package com.yunbao.phonelive.views;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.HorizontalVideoPlayActivity;
import com.yunbao.phonelive.activity.UserHomeActivity;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.dialog.VideoShareDialogFragment;
import com.yunbao.phonelive.event.ImageNumEvent;
import com.yunbao.phonelive.event.VideoLikeEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.CommUtil;
import com.yunbao.phonelive.utils.PhotoStorge;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2018/11/26.
 * 视频播放外框
 */

public class ImagePlayWrapViewHolder extends AbsViewHolder implements View.OnClickListener {
    private ViewGroup mVideoContainer;
    private boolean IsFrist;
    private ImageView mCover;
   /* private ImageView mAvatar;
    public ImageView getAvatar(){
        return  mAvatar;
    }
    private TextView mName;
    public  TextView getName(){
        return  mName;
    }
    private TextView mTitle;
    public  TextView getTitle(){
        return  mTitle;
    }
    private ImageView mBtnLike;//点赞按钮
    public  ImageView getBtnLike(){
        return mBtnLike;
    }
    private TextView mLikeNum;//点赞数
    public  TextView getLikeNum(){
        return mLikeNum;
    }
    private TextView mCommentNum;//评论数
    public  TextView getCommentNum(){
        return  mCommentNum;
    }
    private TextView mShareNum;//分享数
    public TextView getShareNum(){
        return mShareNum;
    }
    private ImageView mBtnFollow;//关注按钮
    public  ImageView getBtnFollow(){
        return mBtnFollow;
    }

    private Drawable mFollowDrawable;//已关注
    private Drawable mUnFollowDrawable;//未关注
    private Animation mFollowAnimation;

    private ValueAnimator mLikeAnimtor;
    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    private int mLikeAnimIndex;

    private ImageView btn_comment;
    public ImageView getBtnComment(){
        return  btn_comment;
    }
    private ImageView btn_share;
    public ImageView getBtnShare(){
        return  btn_share;
    }*/
   private boolean mCurPageShowed;//当前页面是否可见
   private PhotoBean mPhotoBean;
   private String mTag;
    public ImagePlayWrapViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_image_play_wrap;
    }

    @Override
    public void init() {
        mTag = this.toString();
        mVideoContainer = (ViewGroup) findViewById(R.id.video_container);
        mCover = (ImageView) findViewById(R.id.cover);
      /*  mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mTitle = (TextView) findViewById(R.id.title);
        mBtnLike = (ImageView) findViewById(R.id.btn_like);
        mLikeNum = (TextView) findViewById(R.id.like_num);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mShareNum = (TextView) findViewById(R.id.share_num);
        mBtnFollow = (ImageView) findViewById(R.id.btn_follow);
        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_1);
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_0);
        mAvatar.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnLike.setOnClickListener(this);
        btn_comment = (ImageView) findViewById(R.id.btn_comment);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        findViewById(R.id.btn_comment).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);*/
    }

    /**
     * 初始化点赞动画
     */
    private void initLikeAnimtor() {
       /* if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
            mLikeAnimtor = ValueAnimator.ofFloat(0, mLikeAnimDrawables.length);
            mLikeAnimtor.setDuration(800);
            mLikeAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    int index = (int) v;
                    if (mLikeAnimIndex != index) {
                        mLikeAnimIndex = index;
                        if (mBtnLike != null && mLikeAnimDrawables != null && index < mLikeAnimDrawables.length) {
                            mBtnLike.setImageDrawable(mLikeAnimDrawables[index]);
                        }
                    }
                }
            });
        }*/
    }

    /**
     * 初始化关注动画
     */
    public void initFollowAnimation() {
       /* mFollowAnimation = new ScaleAnimation(1, 0.3f, 1, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mFollowAnimation.setRepeatMode(Animation.REVERSE);
        mFollowAnimation.setRepeatCount(1);
        mFollowAnimation.setDuration(200);
        mFollowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (mBtnFollow != null && mPhotoBean != null) {
                  *//*  if (mVideoBean.getAttent() == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }*//*
                }
            }
        });*/
    }

  /*  public void setLikeAnimDrawables(Drawable[] drawables) {
        mLikeAnimDrawables = drawables;
    }*/

    public void setData(PhotoBean bean, Object payload) {

        if (bean == null) {
            return;
        }
        mPhotoBean = bean;

        // UserBean u = mVideoBean.getUserBean();
        if (payload == null) {
            if (mCover != null) {
               //setCoverImage();
            }
            /*if (mTitle != null) {
                //mTitle.setText(bean.getTitle());
            }*/
           /* if (u != null) {
                if (mAvatar != null) {
                    ImgLoader.displayAvatar(u.getAvatar(), mAvatar);
                }
                if (mName != null) {
                    mName.setText("@" + u.getUserNiceName());
                }
            }*/
        }
        /*if (mBtnLike != null) {
           *//* if (bean.getLike() == 1) {
                if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
                    mBtnLike.setImageDrawable(mLikeAnimDrawables[mLikeAnimDrawables.length - 1]);
                }
            } else {
                mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
            }*//*
        }
        if (mLikeNum != null) {
           // mLikeNum.setText(bean.getLikeNum());
        }
        if (mCommentNum != null) {
           // mCommentNum.setText(bean.getCommentNum());
        }
        if (mShareNum != null) {
          //  mShareNum.setText(bean.getShareNum());
        }*/
      /*  if (u != null && mBtnFollow != null) {
            String toUid = u.getId();
            if (!TextUtils.isEmpty(toUid) && !toUid.equals(AppConfig.getInstance().getUid())) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
                if (bean.getAttent() == 1) {
                    mBtnFollow.setImageDrawable(mFollowDrawable);
                } else {
                    mBtnFollow.setImageDrawable(mUnFollowDrawable);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            }
        }*/
    }

    private void setCoverImage() {
      //  mPhotoBean.setCover("http://oss-sns.youguoquan.com/prod/album/5c9f0ba044746e6ac7bd3696/cbb_A5pJbEPF.gif");

        Log.e("Imagehref1",mPhotoBean.getCover());
        //Log.e("Thumb",mVideoBean.getThumb());

        ImgLoader.displayDrawable(mPhotoBean.getCover(), new ImgLoader.DrawableCallback() {
            @Override
            public void callback(Drawable drawable) {
                Log.e("drawable",drawable+"sss");
                if (mCover != null && drawable != null) {
                    float w = drawable.getIntrinsicWidth();
                    float h = drawable.getIntrinsicHeight();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCover.getLayoutParams();
                    int targetH = 0;
                    if (w / h > 0.5625f) {//横屏  9:16=0.5625
                        targetH = (int) (mCover.getWidth() / w * h);
                    } else {
                        targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (targetH != params.height) {
                        params.height = targetH;
                        mCover.requestLayout();
                    }
                    mCover.setImageDrawable(drawable);
                }
            }
        });
    }

    public void addVideoView(View view) {
        if (mVideoContainer != null && view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                ViewGroup viewGroup = (ViewGroup) parent;
                if (viewGroup != mVideoContainer) {
                    viewGroup.removeView(view);
                    mVideoContainer.addView(view);
                }
            } else {
                mVideoContainer.addView(view);
            }
        }
    }

    public PhotoBean getVideoBean() {
        return mPhotoBean;
    }


    /**
     * 获取到视频首帧回调
     */
    public void onFirstFrame() {
        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
            mCover.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 滑出屏幕
     */
    public void onPageOutWindow() {
        mCurPageShowed = false;
        if (mCover != null && mCover.getVisibility() != View.VISIBLE) {
            mCover.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滑入屏幕
     */
    public void onPageInWindow() {
        if (mCover != null) {
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
            mCover.setImageDrawable(null);
            setCoverImage();
        }
    }

    /**
     * 滑动到这一页 准备开始播放
     */
    public void onPageSelected() {
        mCurPageShowed = true;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
       /* switch (v.getId()) {
            case R.id.btn_follow://关注
                clickFollow();
                break;
            case R.id.btn_comment://评论
                clickComment();
                break;
            case R.id.btn_share://分享
                clickShare();
                break;
            case R.id.btn_like://点赞
                clickLike();
                break;
            case R.id.avatar:
                clickAvatar();
                break;
        }*/
    }

    /**
     * 点击头像
     */
    public void clickAvatar() {
        if (mPhotoBean != null) {
           // UserHomeActivity.forward(mContext, mVideoBean.getUid());
        }
    }

    /**
     * 点赞,取消点赞
     */
   /* private void clickLike() {
        if (mPhotoBean == null) {
            return;
        }
        HttpUtil.setVideoLike(mTag, mPhotoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String likeNum = obj.getString("likes");
                    int like = obj.getIntValue("islike");
                    if (mPhotoBean != null) {
                      //  mVideoBean.setLikeNum(likeNum);
                        //  mVideoBean.setLike(like);
                        EventBus.getDefault().post(new VideoLikeEvent(mPhotoBean.getId(), like, likeNum));
                    }
                    if (mLikeNum != null) {
                        mLikeNum.setText(likeNum);
                    }
                    if (mBtnLike != null) {
                        if (like == 1) {
                            if (mLikeAnimtor == null) {
                                initLikeAnimtor();
                            }
                            mLikeAnimIndex = -1;
                            if (mLikeAnimtor != null) {
                                mLikeAnimtor.start();
                            }
                        } else {
                            mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }*/

    /**
     * 点击关注按钮
     */
    private void clickFollow() {
        if (mPhotoBean == null) {
            return;
        }
      /*  final UserBean u = mVideoBean.getUserBean();
        if (u == null) {
            return;
        }
        HttpUtil.setAttention(mTag, Constants.FOLLOW_FROM_VIDEO_PLAY, u.getId(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer attent) {
                mVideoBean.setAttent(attent);
                if (mCurPageShowed) {
                    if (mFollowAnimation == null) {
                        initFollowAnimation();
                    }
                    mBtnFollow.startAnimation(mFollowAnimation);
                } else {
                    if (attent == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }
                }
            }
        });*/
    }

    /**
     * 点击评论按钮
     */
    private void clickComment() {
        Activity activity = CommUtil.scanForActivity(mContext);
        if(activity instanceof HorizontalVideoPlayActivity){
          //  ((HorizontalVideoPlayActivity) activity).openCommentWindow(mVideoBean);
        } else if(activity instanceof  VideoPlayActivity){
            //((VideoPlayActivity) activity).openCommentWindow(mVideoBean);
        }
   //     ((VideoPlayActivity) mContext).openCommentWindow(mVideoBean);
    }

    /**
     * 点击分享按钮
     */
    private void clickShare() {
        if (mPhotoBean == null) {
            return;
        }
        VideoShareDialogFragment fragment = new VideoShareDialogFragment();
        Bundle bundle = new Bundle();
      //  bundle.putParcelable(Constants.VIDEO_BEAN, mVideoBean);
        fragment.setArguments(bundle);
        Activity activity = CommUtil.scanForActivity(mContext);
        if(activity instanceof  HorizontalVideoPlayActivity){

            fragment.show(((HorizontalVideoPlayActivity) activity).getSupportFragmentManager(),"VideoShareDialogFragment");
        } else if(activity instanceof  VideoPlayActivity){
            ((VideoPlayActivity) activity).getSupportFragmentManager();
            fragment.show(((VideoPlayActivity) activity).getSupportFragmentManager(), "VideoShareDialogFragment");
        }
        //fragment.show(((VideoPlayActivity) mContext).getSupportFragmentManager(), "VideoShareDialogFragment");
    }

    public void release() {
        HttpUtil.cancel(mTag);
       /* if (mLikeAnimtor != null) {
            mLikeAnimtor.cancel();
        }
        if (mBtnFollow != null && mFollowAnimation != null) {
            mBtnFollow.clearAnimation();
        }*/
    }


}
