package com.yunbao.phonelive.views;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.mob.tools.utils.LocationHelper;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ContextUtil;
import com.yunbao.phonelive.activity.LauncherActivity;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.adapter.ImageAdapter;
import com.yunbao.phonelive.adapter.MainHomeVideoAdapter;
import com.yunbao.phonelive.adapter.RefreshAdapter;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.ItemDecoration;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.event.VideoScrollPageEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.interfaces.VideoScrollDataHelper;
import com.yunbao.phonelive.utils.PhotoLoader;
import com.yunbao.phonelive.utils.SourceUtil;
import com.yunbao.phonelive.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;

import indi.liyi.viewer.ImageDrawee;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.Utils;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.listener.OnBrowseStatusListener;
import indi.liyi.viewer.listener.OnItemChangedListener;

/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

//public class MainImageViewHolder extends AbsMainChildTopViewHolder implements OnItemClickListener<VideoBean> {
public class MainImageViewHolder extends AbsMainChildTopViewHolder {
   /* private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    public MainImageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_image;
    }
    @Override
    public void init() {
        super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_live_video);
     //   mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
     //   mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 5);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeVideoAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainImageViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHomeImageList(p, callback);
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefresh(List<VideoBean> list) {
                VideoStorge.getInstance().put(Constants.VIDEO_HOME, list);
            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
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
                EventBus.getDefault().register(MainImageViewHolder.this);
            }

            @Override
            public void onDestroy() {
                EventBus.getDefault().unregister(MainImageViewHolder.this);
            }
        };
        mVideoScrollDataHelper = new VideoScrollDataHelper() {

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHomeImageList(p, callback);
            }
        };
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_HOME.equals(e.getKey()) && mRefreshView != null) {
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
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPage();
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, Constants.VIDEO_HOME, page);
    }
*/


    Context context = AppContext.getContext();
    public MainImageViewHolder(Context context, ViewGroup parentView) {

        super(context, parentView);
        this.context = context;

    }
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    //   private LinearLayoutManager linearManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    //  private GridLayoutManager gridLayoutManager;
    private ImageAdapter adapter;
    private Point mScreenSize;
    private List<String> mImgList;
    private List<ViewData> mVdList;
    private int mStatusBarHeight;

    @Override
    public int getLayoutId() {
        return R.layout.view_main_home_image;
    }
    @Override
    public void init() {
        imageViewer = (ImageViewer) findViewById(R.id.imageViewer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        initData();
        //      linearManager = new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //       gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        //   linearManager.setStackFromEnd(true);
        //   recyclerView.setLayoutManager(linearManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        // recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter = new ImageAdapter(1);
        adapter.setData(mImgList);
        addListener();
        imageViewer.overlayStatusBar(false)
                .imageLoader(new PhotoLoader());
    }

    private void initData() {
        Log.e("AppContext.getContext()",AppContext.getContext()+"7777");
        mScreenSize = com.yunbao.phonelive.utils.Utils.getScreenSize(AppContext.getContext());
        mStatusBarHeight = com.yunbao.phonelive.utils.Utils.getStatusBarHeight(AppContext.getContext());
        Log.e("mStatusBarHeight",mStatusBarHeight+"222");
        mImgList = SourceUtil.getImageList();
        mVdList = new ArrayList<>();
        for (int i = 0, len = mImgList.size(); i < len; i++) {
            ViewData viewData = new ViewData();
            viewData.setImageSrc(mImgList.get(i));
            viewData.setTargetX(Utils.dp2px(AppContext.getContext(), 10));
            viewData.setTargetWidth(mScreenSize.x - Utils.dp2px(AppContext.getContext(), 20));
            viewData.setTargetHeight(Utils.dp2px(AppContext.getContext(), 200));
            mVdList.add(viewData);
        }
    }

   // @Override
    public void addListener() {
        adapter.setOnItemClickCallback(new ImageAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                Log.e("点击Item","11111");
                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                // 去掉状态栏的高度
                Log.e("location[0]",location[0]+"1111");
                Log.e("location[1]",location[1]+"1111");
                mVdList.get(position).setTargetY(location[1] + mStatusBarHeight);
                imageViewer.viewData(mVdList)
                        .watch(position);
            }
        });
       recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    boolean b = imageViewer.onKeyDown(keyCode, event);
                    if (b) {
                        return b;
                    }
                }
                return false;
            }
        });

        recyclerView.setAdapter(adapter);
        Log.e("getItemCount",adapter.getItemCount()+"");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                staggeredGridLayoutManager.invalidateSpanAssignments();


            }
        });
        //    linearManager.scrollToPositionWithOffset(0, 0);
        staggeredGridLayoutManager.scrollToPositionWithOffset(0,0);
        //  staggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
        imageViewer.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onItemChanged(int position, ImageDrawee drawee) {
                Log.e("imageViewer",imageViewer.getViewStatus()+"");
                if (imageViewer.getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    int top = getTop(imageViewer.getCurrentPosition());
                    Log.e("top1",top+"");
                    mVdList.get(imageViewer.getCurrentPosition()).setTargetY(top);
                    // linearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                    //staggeredGridLayoutManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                    staggeredGridLayoutManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });

        imageViewer.setOnBrowseStatusListener(new OnBrowseStatusListener() {
            @Override
            public void onBrowseStatus(int status) {
                if (status == ViewerStatus.STATUS_BEGIN_CLOSE) {
                    int top = getTop(imageViewer.getCurrentPosition());
                    mVdList.get(imageViewer.getCurrentPosition()).setTargetY(top);
                    staggeredGridLayoutManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });
    }

    private int getTop(int position) {
        int top = 0;
        // 当前图片的高度
        float imgH = Float.valueOf(mVdList.get(position).getTargetHeight());
        // 图片距离 imageViewer 的上下边距
        int dis = (int) ((imageViewer.getHeight() - imgH) / 2);
        // 如果图片高度大于等于 imageViewer 的高度
        if (dis <= 0) {
            return top + dis;
        } else {
            float th1 = 0;
            float th2 = 0;
            // 计算当前图片上方所有 Item 的总高度
            for (int i = 0; i < position; i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th1 += Utils.dp2px(AppContext.getContext(), 210);
            }
            // 计算当前图片下方所有 Item 的总高度
            for (int i = position + 1; i < mImgList.size(); i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th2 += Utils.dp2px(AppContext.getContext(), 210);
            }
            if (th1 >= dis && th2 >= dis) {
                return top + dis;
            } else if (th1 < dis) {
                return (int) (top + th1);
            } else if (th2 < dis) {
                return (int) (recyclerView.getHeight() - imgH);
            }
        }
        return 0;
    }



}
