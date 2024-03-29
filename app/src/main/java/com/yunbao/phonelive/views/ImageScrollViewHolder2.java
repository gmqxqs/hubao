package com.yunbao.phonelive.views;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.HorizontalVideoPlayActivity;
import com.yunbao.phonelive.activity.ImagePlayActivity;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.adapter.ImageScrollAdapter2;
import com.yunbao.phonelive.adapter.VideoScrollAdapter;
import com.yunbao.phonelive.bean.ImageBean;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.VideoLoadingBar;
import com.yunbao.phonelive.event.FollowEvent;
import com.yunbao.phonelive.event.ImageNumEvent;
import com.yunbao.phonelive.event.TitleEvent;
import com.yunbao.phonelive.event.VideoCommentEvent;
import com.yunbao.phonelive.event.VideoLikeEvent;
import com.yunbao.phonelive.event.VideoPauseEvent;
import com.yunbao.phonelive.event.VideoPlayEvent;
import com.yunbao.phonelive.event.VideoScrollPageEvent;
import com.yunbao.phonelive.event.VideoShareEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ImageScrollDataHelper;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.interfaces.VideoScrollDataHelper;
import com.yunbao.phonelive.utils.CommUtil;
import com.yunbao.phonelive.utils.ImageStorge;
import com.yunbao.phonelive.utils.PhotoStorge;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/11/26.
 * 视频滑动
 */

public class ImageScrollViewHolder2 extends AbsViewHolder implements
        ImageScrollAdapter2.ActionListener, SwipeRefreshLayout.OnRefreshListener,
        VideoPlayViewHolder.ActionListener, View.OnClickListener {

    private VideoPlayViewHolder mVideoPlayViewHolder;
    private View mPlayView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageScrollAdapter2 mImageScrollAdapter;
    private int mPosition;
    private String mVideoKey;
    private String mImageGroupKey;
    private ImagePlayWrapViewHolder mImagePlayWrapViewHolder;
    private VideoLoadingBar mVideoLoadingBar;
    private int mPage;
    private HttpCallback mRefreshCallback;//下拉刷新回调
    private HttpCallback mLoadMoreCallback;//上拉加载更多回调
    private ImageScrollDataHelper mImageDataHelper;
    private PhotoBean mPhotoBean;
    private boolean mPaused;//生命周期暂停
    private LinearLayout inputLinearLayout;
    private VideoLoadingBar  videoLoadingBar;
    public static final int MSG_ONE = 1;
    private TextView mPageNum;
    private TextView mTitle;


    public ImageScrollViewHolder2(Context context, ViewGroup parentView, int position, String videoKey, int page,String group) {
        super(context, parentView, position, videoKey, page,group);
    }


    @Override
    protected void processArguments(Object... args) {
        mPosition = (int) args[0];
        Log.e("mPosition2",mPosition+"");
        mVideoKey = (String) args[1];
        Log.e("VIDEO_KEY2",mVideoKey);
        mPage = (int) args[2];
        Log.e("mPage2",mPage+"");
        mImageGroupKey = (String) args[3];
        Log.e("mImageGroupKey2",mImageGroupKey);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_image_scroll;
    }

    @Override
    public void init() {
        List<PhotoBean> list = PhotoStorge.getInstance().get(mVideoKey);
        List<ImageBean> listImage = ImageStorge.getInstance().get(mImageGroupKey);
        Log.e("photoList",list+"");

        if (list == null || list.size() == 0) {
            return;
        }
        if(listImage != null && listImage.size() != 0){
            Log.e("listImage",listImage.size()+"");
        }
        Log.e("photoId",list.get(0).getId()+"");
        mVideoPlayViewHolder = new VideoPlayViewHolder(mContext, null,mPosition,mVideoKey,mPage);
        mVideoPlayViewHolder.setActionListener(this);
        mPlayView = mVideoPlayViewHolder.getContentView();
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.global);
        mRefreshLayout.setEnabled(false);//产品不让使用刷新
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mPageNum = (TextView) findViewById(R.id.pageNum);
        mTitle =  (TextView) findViewById(R.id.imageTitle);
        mTitle.setText(listImage.get(0).getTitle());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mImageScrollAdapter = new ImageScrollAdapter2(mContext, list, mPosition);
        mImageScrollAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mImageScrollAdapter);
        mVideoLoadingBar = (VideoLoadingBar) findViewById(R.id.video_loading);
        //inputLinearLayout = (LinearLayout) findViewById(R.id.inputLinearLayout);
        videoLoadingBar = (VideoLoadingBar) findViewById(R.id.video_loading);
       /* findViewById(R.id.input_tip).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);*/
        EventBus.getDefault().register(this);

        mLifeCycleListener = new LifeCycleAdapter() {

            @Override
            public void onResume() {
                mPaused = false;
                if (mVideoPlayViewHolder != null) {
                    mVideoPlayViewHolder.resumePlay();
                }
            }

            @Override
            public void onPause() {
                mPaused = true;
                if (mVideoPlayViewHolder != null) {
                    mVideoPlayViewHolder.pausePlay();
                }
            }

        };
        mRefreshCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ImageBean> listImage = JSON.parseArray(Arrays.toString(info), ImageBean.class);
                    List<PhotoBean> listPhotoBean = listImage.get(0).getHref();
                    Log.e("刷新成功","qqq");
                    if (mImageScrollAdapter!= null) {
                        mImageScrollAdapter.setList(listPhotoBean);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        };
        mLoadMoreCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ImageBean> listImageBean = JSON.parseArray(Arrays.toString(info), ImageBean.class);
                    List<PhotoBean> listPhotoBean = listImageBean.get(0).getHref();
                    if (listPhotoBean.size() > 0) {
                        if (mImageScrollAdapter != null) {
                            mImageScrollAdapter.insertList(listPhotoBean);
                        }
                        EventBus.getDefault().post(new VideoScrollPageEvent(mVideoKey, mPage));
                    } else {
                        ToastUtil.show(R.string.video_no_more_image);
                        mPage--;
                    }
                } else {
                    mPage--;
                }
            }
        };
        mImageDataHelper = PhotoStorge.getInstance().getDataHelper(mVideoKey);

    }


    @Override
    public void onPageSelected(ImagePlayWrapViewHolder imagePlayWrapViewHolder, boolean needLoadMore) {
        if (imagePlayWrapViewHolder != null) {
            PhotoBean photoBean = imagePlayWrapViewHolder.getVideoBean();
            if (photoBean != null) {
                mPhotoBean = photoBean;
                mImagePlayWrapViewHolder = imagePlayWrapViewHolder;
                mImagePlayWrapViewHolder.addVideoView(mPlayView);
               /* if (mVideoPlayViewHolder != null) {
                    mVideoPlayViewHolder.startPlay(photoBean);
                }*/
                if (mVideoLoadingBar != null) {
                    mVideoLoadingBar.setLoading(true);
                }
            }
            if (needLoadMore) {
                onLoadMore();
            }
        }
    }

    @Override
    public void onPageOutWindow(ImagePlayWrapViewHolder vh) {
        if (mImagePlayWrapViewHolder != null && mImagePlayWrapViewHolder == vh && mVideoPlayViewHolder != null) {
            mVideoPlayViewHolder.stopPlay();
        }
    }

    @Override
    public void onVideoDeleteAll() {
        ((ImagePlayActivity) mContext).onBackPressed();
    }

    public void release() {
        HttpUtil.cancel(HttpConsts.GET_HOME_VIDEO_LIST);
        EventBus.getDefault().unregister(this);
        if (mVideoPlayViewHolder != null) {
            mVideoPlayViewHolder.release();
        }
        mImagePlayWrapViewHolder = null;
        if (mVideoLoadingBar != null) {
            mVideoLoadingBar.endLoading();
        }
        mVideoLoadingBar = null;
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mImageScrollAdapter != null) {
            mImageScrollAdapter.release();
        }
        mImageScrollAdapter = null;
        mImageDataHelper = null;
    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mPage = 1;
        if (mImageDataHelper != null) {
            mImageDataHelper.loadData(mPage, mRefreshCallback);
        }
    }

    /**
     * 加载更多
     */
    private void onLoadMore() {
        mPage++;
        if (mImageDataHelper != null) {
            mImageDataHelper.loadData(mPage, mLoadMoreCallback);
        }
    }

    @Override
    public void onPlayBegin() {
        if (mVideoLoadingBar != null) {
            mVideoLoadingBar.setLoading(false);
        }
    }

    @Override
    public void onPlayLoading() {
        if (mVideoLoadingBar != null) {
            mVideoLoadingBar.setLoading(true);
        }
    }

    @Override
    public void onFirstFrame() {
        if (mImagePlayWrapViewHolder != null) {
            mImagePlayWrapViewHolder.onFirstFrame();
        }
    }

    /**
     * 关注发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mImageScrollAdapter != null && mImagePlayWrapViewHolder != null) {
            PhotoBean photoBean = mImagePlayWrapViewHolder.getVideoBean();
            if (photoBean != null) {
              //  mImageScrollAdapter.onFollowChanged(!mPaused, photoBean.getId(), e.getToUid(), e.getIsAttention());
            }
        }
    }

    /**
     * 页码发生变化
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageNumEvent(ImageNumEvent e){
        L.e("ImageNumEvent",e.toString());
        mPageNum.setText(e.getmCurrentNum()+"/"+e.getmTotalNaum());
    }



    /**
     * 点赞发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoLikeEvent(VideoLikeEvent e) {
        L.e("VideoLikeEvent",e.toString());
        if (mImageScrollAdapter != null) {
          //  mImageScrollAdapter.onLikeChanged(!mPaused, e.getVideoId(), e.getIsLike(), e.getLikeNum());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPuaseEvent(VideoPauseEvent e){
        L.e("onVideoPuaseEvent",e.toString());
        changUiToPause();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayEvent(VideoPlayEvent e){
        L.e("onVideoPlayEvent",e.toString());
        changUiToPlaying();
    }

    public void changUiToPlaying(){
        Activity activity = CommUtil.scanForActivity(mContext);
        if(activity instanceof  HorizontalVideoPlayActivity){
          /*  mVideoPlayWrapViewHolder.getBtnLike().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getTitle().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getName().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getBtnComment().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getBtnShare().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getBtnFollow().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getAvatar().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getLikeNum().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getCommentNum().setVisibility(View.GONE);
            mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.GONE);
            inputLinearLayout.setVisibility(View.GONE);
            videoLoadingBar.setVisibility(View.GONE);*/
         }
    }

    public void changUiToPause(){
        boolean mPause = mVideoPlayViewHolder.getPaused();
        Activity activity = CommUtil.scanForActivity(mContext);
        if(activity instanceof  HorizontalVideoPlayActivity){
            if(mPause){
               /* mVideoPlayWrapViewHolder.getBtnLike().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getBtnLike().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getTitle().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getName().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getBtnComment().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getBtnShare().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getBtnFollow().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getAvatar().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getLikeNum().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getCommentNum().setVisibility(View.VISIBLE);
                mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.VISIBLE);
                inputLinearLayout.setVisibility(View.VISIBLE);
                videoLoadingBar.setVisibility(View.VISIBLE);*/
            } else{
               /* mVideoPlayWrapViewHolder.getBtnLike().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getTitle().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getName().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getBtnComment().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getBtnShare().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getBtnFollow().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getAvatar().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getLikeNum().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getCommentNum().setVisibility(View.GONE);
                mVideoPlayWrapViewHolder.getShareNum().setVisibility(View.GONE);
                inputLinearLayout.setVisibility(View.GONE);
                videoLoadingBar.setVisibility(View.GONE);*/
            }
        }


    }

    /**
     * 分享数发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoShareEvent(VideoShareEvent e) {
        if (mImageScrollAdapter != null) {
           // mImageScrollAdapter.onShareChanged(e.getVideoId(), e.getShareNum());
        }
    }

    /**
     * 评论数发生变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoCommentEvent(VideoCommentEvent e) {
        if (mImageScrollAdapter != null) {
            //mImageScrollAdapter.onCommentChanged(e.getVideoId(), e.getCommentNum());
        }
    }

    /**
     * 删除视频
     */
    public void deleteVideo(PhotoBean photoBean) {
        if (mImageScrollAdapter != null) {
            mImageScrollAdapter.deleteVideo(photoBean);
        }
    }

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.input_tip:
                openCommentInputWindow(false);
                break;
            case R.id.btn_face:
                openCommentInputWindow(true);
                break;
        }*/
    }

    /**
     * 打开评论输入框
     */
    private void openCommentInputWindow(boolean openFace) {
        if (mPhotoBean != null) {
            Activity activity = CommUtil.scanForActivity(mContext);
            if(activity instanceof HorizontalVideoPlayActivity){
               // ((HorizontalVideoPlayActivity) activity).openCommentInputWindow(openFace, mVideoBean, null);
            } else if(activity instanceof VideoPlayActivity){
                //((VideoPlayActivity) activity).openCommentInputWindow(openFace, mVideoBean, null);
            }


        }
    }

    /**
     * VideoBean 数据发生变化
     */
    public void onVideoBeanChanged(String videoId) {
        if (mImageScrollAdapter != null) {
            mImageScrollAdapter.onVideoBeanChanged(videoId);
        }
    }

}
