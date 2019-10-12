package com.yunbao.phonelive.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.HorizontalVideoPlayActivity;
import com.yunbao.phonelive.activity.ImagePlayActivity;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.ItemSlideHelper;
import com.yunbao.phonelive.event.ImageNumEvent;
import com.yunbao.phonelive.event.VideoPauseEvent;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.CommUtil;
import com.yunbao.phonelive.utils.IconUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.views.ImagePlayWrapViewHolder;
import com.yunbao.phonelive.views.VideoPlayWrapViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2018/11/27.
 */

public class ImageScrollAdapter2 extends RecyclerView.Adapter<ImageScrollAdapter2.Vh> implements ItemSlideHelper.Callback {
    private static final String TAG = "ImageScrollAdapter";
    private static final int COUNT = 20;//接口每页返回多少条
    private Context mContext;
   // private GestureDetector gestureDetector;
    private List<PhotoBean> mList;
    private SparseArray<ImagePlayWrapViewHolder> mMap;
    private int mCurPosition;
    private ActionListener mActionListener;
    private boolean mFirstLoad;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Drawable[] mLikeAnimDrawables;
    private Handler mHandler;
    public ImageScrollAdapter2(Context context, List<PhotoBean> list, int curPosition) {
        mContext = context;
        mList = list;
        mCurPosition = curPosition;
        mMap = new SparseArray<>();
        mFirstLoad = true;
        List<Integer> likeImageList = IconUtil.getVideoLikeAnim();//点赞帧动画
        mLikeAnimDrawables = new Drawable[likeImageList.size()];
        for (int i = 0, length = mLikeAnimDrawables.length; i < length; i++) {
            mLikeAnimDrawables[i] = ContextCompat.getDrawable(context, likeImageList.get(i));
        }
      //  setListener();
    }

    private void setListener() {
        //设置手势监听器的处理效果
        //gestureDetector=new GestureDetector(AppContext.getContext(), onGestureListener);
    }
/*

    //手势识别监听器
    private GestureDetector.OnGestureListener onGestureListener
            =new GestureDetector.SimpleOnGestureListener(){
        //当识别的手势是滑动手势时回调onFling方法
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            //得到滑动手势的其实和结束位置的X,Y坐标,并进行计算
            float x=e2.getX()-e1.getX();//位移量
            float y=e2.getY()-e1.getY();

            //通过计算结果判断用户是向左滑动还是向右滑动
            if(x>0){//向右滑动
                */
/*count--;
                count=(count+4)%4;*//*

                Log.e("向右滑动","111");
             //   findCurVideo();
            }else if(x<0){//向左滑动
                */
/*count++;
                count %=4;*//*

                Log.e("向左滑动","222");

                */
/*if(count < array.length-1){
                    count++;
                }else if(count == array.length-1){
                    count = count;
                    return false;
                }*//*

           //     findCurVideo();
            }else if(y > 0){
                return false;
            } else if(y < 0){
                return  false;
            }
           // changeImg();
            //  setImgId();
            return true;
        }
    };
*/

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImagePlayWrapViewHolder vpvh = new ImagePlayWrapViewHolder(mContext, parent);
      //  vpvh.setLikeAnimDrawables(mLikeAnimDrawables);
        return new Vh(vpvh);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
        if (mFirstLoad) {
            mFirstLoad = false;
            ImagePlayWrapViewHolder vpvh = mMap.get(mCurPosition);
            if (vpvh != null && mActionListener != null) {
                vpvh.onPageSelected();
                mActionListener.onPageSelected(vpvh, mList.size() >= COUNT && mCurPosition == mList.size() - 1);
            }
        }
    }


    @Override
    public void onViewRecycled(@NonNull Vh vh) {
        vh.reycle();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 视频数据发生变化，更新item
     */
    public void onVideoBeanChanged(String changedVideoId) {
        if (TextUtils.isEmpty(changedVideoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            PhotoBean  bean = mList.get(i);
            if (bean != null) {
                if (changedVideoId.equals(bean.getId())) {
                    notifyItemChanged(i, Constants.PAYLOAD);
                    break;
                }
            }
        }
    }

    /**
     * 删除视频
     */
    public void deleteVideo(PhotoBean photoBean) {
        if (photoBean == null) {
            return;
        }
        String videoId = photoBean.getId();
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            PhotoBean bean = mList.get(i);
            if (videoId.equals(photoBean.getId())) {
                mList.remove(i);
                notifyItemRemoved(i);
                size = mList.size();
                if (size > 0) {
                    notifyItemRangeChanged(i, size);
                    if (mHandler == null) {
                        mHandler = new Handler();
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCurPosition = -1;
                            findCurVideo();
                        }
                    }, 500);
                } else {
                    if (mActionListener != null) {
                        mActionListener.onVideoDeleteAll();
                    }
                }
                break;
            }
        }
    }

    /**
     * ItemSlideHelper回调
     */
    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        return 0;
    }

    /**
     * ItemSlideHelper回调
     */
    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        if (mRecyclerView != null && childView != null) {
            return mRecyclerView.getChildViewHolder(childView);
        }
        return null;
    }

    /**
     * ItemSlideHelper回调
     */
    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    @Override
    public boolean useLeftScroll() {
        return false;
    }

    @Override
    public void onLeftScroll(RecyclerView.ViewHolder vh) {
       Activity activity = CommUtil.scanForActivity(mContext);
        if(activity instanceof  HorizontalVideoPlayActivity){
            return;
        }
      /*  if(!((HorizontalVideoPlayActivity) mContext).isPaused()){
            return;
        }
        if(((HorizontalVideoPlayActivity) mContext).isPaused()){
            return;
        }*/
        if(activity instanceof ImagePlayActivity){
            return;
        }
       /* if (((VideoPlayActivity) mContext).isPaused()) {
            return;
        }*/

        if (!ClickUtil.canClick()) {
            return;
        }
        if (vh != null) {
            ImagePlayWrapViewHolder vpvh = ((Vh) vh).mImagePlayWrapViewHolder;
            if (vpvh != null) {
                vpvh.clickAvatar();
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImagePlayWrapViewHolder mImagePlayWrapViewHolder;

        public Vh(ImagePlayWrapViewHolder imagePlayWrapViewHolder) {
            super(imagePlayWrapViewHolder.getContentView());
            mImagePlayWrapViewHolder = imagePlayWrapViewHolder;
        }

        void setData(PhotoBean bean, int position, Object payload) {
            if (mImagePlayWrapViewHolder != null) {
                mMap.put(position, mImagePlayWrapViewHolder);
                mImagePlayWrapViewHolder.setData(bean, payload);
            }
        }

        void reycle() {
            if (mImagePlayWrapViewHolder != null) {
                mImagePlayWrapViewHolder.release();
            }
        }
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull Vh vh) {
      //  VideoPlayWrapViewHolder vpvh = vh.mVideoPlayWrapViewHolder;
        ImagePlayWrapViewHolder vpvh = vh.mImagePlayWrapViewHolder;
        if (vpvh != null) {
            vpvh.onPageOutWindow();
            if (mActionListener != null) {
                mActionListener.onPageOutWindow(vpvh);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull Vh vh) {
       // VideoPlayWrapViewHolder vpvh = vh.mVideoPlayWrapViewHolder;
        ImagePlayWrapViewHolder vpvh = vh.mImagePlayWrapViewHolder;
        if (vpvh != null) {
            vpvh.onPageInWindow();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
     //   mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mContext, this));
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mLayoutManager.setInitialPrefetchItemCount(2);
        recyclerView.scrollToPosition(mCurPosition);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //findCurVideo();
                Log.i(TAG, "-----------onScrollStateChanged-----------");
                Log.i(TAG, "newState: " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                findCurVideo();
                Log.e("dx",dx+"");
                Log.e("dy",dy+"");
            }
        });
    }

    public void findCurVideo() {
        int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        L.e("ImageScrollposition:" + position);

        if (position >= 0 && mCurPosition != position) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
          //  VideoPlayWrapViewHolder vh = mMap.get(position);
            ImagePlayWrapViewHolder vh = mMap.get(position);
            if (vh != null && mActionListener != null) {
                vh.onPageSelected();
                boolean needLoadMore = false;
                if (position == mList.size() - 1) {
                    /*if (mList.size() < COUNT) {*/
                        ToastUtil.show(R.string.video_no_more_image);
                   /* } else {
                        needLoadMore = true;
                    }*/
                }
                mActionListener.onPageSelected(vh, needLoadMore);
            }
            mCurPosition = position;
            L.e("mCurPosition:" + mCurPosition);
            if (position == 0) {
                ToastUtil.show(R.string.video_scroll_top);
            }
            EventBus.getDefault().post(new ImageNumEvent((mCurPosition+1)+"",mList.size()+""));
        }
    }

    /**
     * 插入数据
     */
    public void insertList(List<PhotoBean> list) {
        if (list != null && list.size() > 0 && mList != null && mRecyclerView != null) {
            int position = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(position, list.size());
        }
    }

    /**
     * 插入数据
     */
   /* public void insertList(List<VideoBean> list) {
        if (list != null && list.size() > 0 && mList != null && mRecyclerView != null) {
            int position = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(position, list.size());
        }
    }*/


    /**
     * 刷新列表
     */
    public void setList(List<PhotoBean> list) {
        if (list != null && list.size() > 0 && mList != null && mRecyclerView != null) {
            mList.clear();
            mList.addAll(list);
            mFirstLoad = true;
            mCurPosition = 0;
            notifyDataSetChanged();
        }
    }

    /**
     * 刷新列表
     */
 /*   public void setList(List<VideoBean> list) {
        if (list != null && list.size() > 0 && mList != null && mRecyclerView != null) {
            mList.clear();
            mList.addAll(list);
            mFirstLoad = true;
            mCurPosition = 0;
            notifyDataSetChanged();
        }
    }*/

    /**
     * 关注发生变化
     */
   /* public void onFollowChanged(boolean needExclude, String excludeVideoId, String toUid, int isAttention) {
        if (mList != null && !TextUtils.isEmpty(toUid) && !TextUtils.isEmpty(excludeVideoId)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                VideoBean videoBean = mList.get(i);
                if (videoBean != null) {
                    if (needExclude && excludeVideoId.equals(videoBean.getId())) {
                        continue;
                    }
                    if (toUid.equals(videoBean.getUid())) {
                        videoBean.setAttent(isAttention);
                        notifyItemChanged(i, Constants.PAYLOAD);
                    }
                }
            }
        }
    }*/

    /**
     * 点赞发生变化
     */
  /*  public void onLikeChanged(boolean needExclude, String videoId, int like, String likeNum) {
        if (mList != null && !TextUtils.isEmpty(videoId)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                VideoBean videoBean = mList.get(i);
                if (videoBean != null && videoId.equals(videoBean.getId()) && !needExclude) {
                    videoBean.setLike(like);
                    videoBean.setLikeNum(likeNum);
                    notifyItemChanged(i, Constants.PAYLOAD);
                    break;
                }
            }
        }
    }*/



    /**
     * 评论数发生变化
     */
  /*  public void onCommentChanged(String videoId, String commentNum) {
        if (mList != null && !TextUtils.isEmpty(videoId)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                VideoBean videoBean = mList.get(i);
                if (videoBean != null && videoId.equals(videoBean.getId())) {
                    videoBean.setCommentNum(commentNum);
                    notifyItemChanged(i, Constants.PAYLOAD);
                    break;
                }
            }
        }
    }*/

    /**
     * 分享数发生变化
     */
    /*public void onShareChanged(String videoId, String shareNum) {
        if (mList != null && !TextUtils.isEmpty(videoId)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                VideoBean videoBean = mList.get(i);
                if (videoBean != null && videoId.equals(videoBean.getId())) {
                    videoBean.setShareNum(shareNum);
                    notifyItemChanged(i, Constants.PAYLOAD);
                    break;
                }
            }
        }
    }*/

    public void release() {
        mActionListener = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }


    public interface ActionListener {
        void onPageSelected(ImagePlayWrapViewHolder vh, boolean needLoadMore);
        void onPageOutWindow(ImagePlayWrapViewHolder vh);
        void onVideoDeleteAll();
    }

}
