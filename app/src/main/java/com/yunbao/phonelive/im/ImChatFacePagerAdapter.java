package com.yunbao.phonelive.im;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.HorizontalVideoPlayActivity;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.interfaces.OnFaceClickListener;
import com.yunbao.phonelive.utils.CommUtil;
import com.yunbao.phonelive.utils.FaceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/7/11.
 * 聊天表情的ViewPager Adapter
 */

public class ImChatFacePagerAdapter extends PagerAdapter {

    private List<View> mViewList;
    private static  int FACE_COUNT = 20;//每页20个表情

    public ImChatFacePagerAdapter(Context context, OnFaceClickListener onFaceClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mViewList = new ArrayList<>();
        List<String> faceList = FaceUtil.getFaceList();
        Activity activity = CommUtil.scanForActivity(context);
        if(activity instanceof HorizontalVideoPlayActivity){
            FACE_COUNT = 47;
        }else if(activity instanceof VideoPlayActivity){

            FACE_COUNT = 20;
        }
        int fromIndex = 0;
        int size = faceList.size();
        int pageCount = size / FACE_COUNT;
        if (size % FACE_COUNT > 0) {
            pageCount++;
            for (int i = 0, count = pageCount * FACE_COUNT - size; i < count; i++) {
                faceList.add("");
            }
        }
        for (int i = 0; i < pageCount; i++) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.view_chat_face_page, null, false);
            recyclerView.setHasFixedSize(true);
            if(activity instanceof HorizontalVideoPlayActivity){
                recyclerView.setLayoutManager(new GridLayoutManager(context, 16, GridLayoutManager.VERTICAL, false));
            }else if(activity instanceof VideoPlayActivity){
                recyclerView.setLayoutManager(new GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false));
            }
          //  recyclerView.setLayoutManager(new GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false));
            int endIndex = fromIndex + FACE_COUNT;
            List<String> list = new ArrayList<>();
            for (int j = fromIndex; j < endIndex; j++) {
                list.add(faceList.get(j));
            }
            list.add("<");
            recyclerView.setAdapter(new ImChatFaceAdapter(list, inflater, onFaceClickListener));
            mViewList.add(recyclerView);
            fromIndex = endIndex;
        }
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@Nullable View view, @Nullable Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@Nullable ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@Nullable ViewGroup container, int position, @Nullable Object object) {
        container.removeView(mViewList.get(position));
    }
}
