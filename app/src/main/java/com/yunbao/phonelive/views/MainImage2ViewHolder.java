package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ImagePlayActivity;
import com.yunbao.phonelive.adapter.MainHomeImageListAdapter;
import com.yunbao.phonelive.adapter.RefreshAdapter;
import com.yunbao.phonelive.bean.ImageBean;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.event.ImageNumEvent;
import com.yunbao.phonelive.event.TitleEvent;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.event.VideoScrollPageEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.ImageScrollDataHelper;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ImageStorge;
import com.yunbao.phonelive.utils.PhotoStorge;
import com.yunbao.phonelive.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainImage2ViewHolder extends AbsMainChildTopViewHolder implements OnItemClickListener<VideoBean> {

    private MainHomeImageListAdapter mAdapter;
    private ImageScrollDataHelper mVideoScrollDataHelper;
    HttpCallback httpCallback =  new HttpCallback() {
        private int mDataCount;
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (info != null) {
               // List list = mDataHelper.processData(info);
                List<VideoBean> listImage2 =  JSON.parseArray(Arrays.toString(info), VideoBean.class);
                Log.e("list写真1",listImage2.toString()+"");
                List<ImageBean> listImage =  JSON.parseArray(Arrays.toString(info), ImageBean.class);
              //  Log.e("list写真2",listImage.toString()+"");
                if (listImage == null) {
                    return;
                }
                mDataCount = listImage.size();
                Log.e("mDataCount:" , mDataCount+"");
                if(mDataCount > 0){
                    List<PhotoBean> photoList = listImage.get(0).getHref();
                    Log.e("photo写真:" , photoList.size()+"");
                    for(int i = 0; i < photoList.size(); i++){
                        Log.e("url",photoList.get(i).toString());
                    }
                    PhotoStorge.getInstance().put(Constants.VIDEO_IMAGE_DETAIL, photoList);
                    ImageStorge.getInstance().put(Constants.VIDEO_IMAGE_GROUP,listImage);
                    Log.e("PhotoStorge",PhotoStorge.getInstance().get(Constants.VIDEO_IMAGE_DETAIL)+"gggg");
                    EventBus.getDefault().post(new ImageNumEvent(1+"",(photoList.size()-1)+""));
                    ImagePlayActivity.forward(mContext, 0, Constants.VIDEO_IMAGE_DETAIL, 1,photoList.size(),Constants.VIDEO_IMAGE_GROUP);

                }
            }
        }


    };
    public MainImage2ViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_image2;
    }
    @Override
    public void init() {
        super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_live_video);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeImageListAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainImage2ViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                Log.e("获取套图列表","111kkk");
                HttpUtil.getHomeImageList(p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefresh(List<VideoBean> list) {
                Log.e("imageListVideo",list.get(0).toString());
                VideoStorge.getInstance().put(Constants.VIDEO_IMAGE, list);
            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
                Log.e("数据加载完成","kkkkk");
                if (dataCount < 10) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onCreate() {
                EventBus.getDefault().register(MainImage2ViewHolder.this);
            }

            @Override
            public void onDestroy() {
                EventBus.getDefault().unregister(MainImage2ViewHolder.this);
            }
        };
        mVideoScrollDataHelper = new ImageScrollDataHelper() {

            @Override
            public void loadData(int p, HttpCallback callback) {

                HttpUtil.getImageDetail(p+"", callback);
            }
        };
    }

    @Override
    public void loadData() {
        /*if (!isFirstLoadData()) {
            return;
        }*/
        Log.e("ImageRefreshView:",mRefreshView+"kkk");
        if (mRefreshView != null) {
            mRefreshView.initData();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_IMAGE.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPage(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showNoData();
            }
        }
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        Log.e("beanId",bean.getId());
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPage();
        }
        HttpUtil.getImageDetail(bean.getId(), httpCallback);
        PhotoStorge.getInstance().putDataHelper(Constants.VIDEO_IMAGE_DETAIL, mVideoScrollDataHelper);
        Log.e("VideoStorge",PhotoStorge.getInstance().getDataHelper(Constants.VIDEO_IMAGE_DETAIL)+"");
//        ImagePlayActivity.forward(mContext, position, Constants.VIDEO_IMAGE_DETAIL, page);
    }






}
