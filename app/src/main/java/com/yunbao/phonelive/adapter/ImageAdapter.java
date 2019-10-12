package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
/*import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;*/
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yunbao.phonelive.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;


public class ImageAdapter extends RecyclerView.Adapter {
    // 0: 水平方向  1: 垂直方向
    private int mOrientation;

    private List<String> mImgList;
    private OnItemClickCallback mCallback;
    private HashMap<Integer, Integer> mHeightMap = new HashMap<Integer, Integer>();

    public ImageAdapter(int orientation) {
        mOrientation = orientation;
    }

    public void setData(List<String> list) {

        this.mImgList = list;
        Log.e("mImgList",mImgList.size()+"");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (mOrientation == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_vertical, parent, false);
            Log.e("View2",view.toString());
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_vertical, parent, false);
            Log.e("View2",view.toString());
        }
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e("滑动","1");
        final int mPosition = position;
        final ItemHolder itemHolder = (ItemHolder) holder;

     //   Glide.loadImage(itemHolder.iv_pic.getContext(), mImgList.get(position), itemHolder.iv_pic);
        Glide.with(itemHolder.iv_pic.getContext()).load(mImgList.get(position)).placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder).into(itemHolder.iv_pic);
        RequestListener mRequestListener = new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                Log.e("Exception", "onException: " + e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);
                return false;

            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e("onResourceReady",  "model:"+model+" isFirstResource: "+isFirstResource);
                return false;
            }
        };
        final int fp = position;
        itemHolder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击Item","");
                if (mCallback != null) {
                    mCallback.onItemClick(fp, itemHolder.iv_pic);
                }
            }
        });

    //    StaggeredGridLayoutManager.LayoutParams layoutParams =  (StaggeredGridLayoutManager)itemHolder.iv_pic.getLayoutParams();

    //   GlideApp.with(itemHolder.iv_pic.getContext()).load(mImgList.get(position)).override(lp.width,lp.height).fitCenter().into(itemHolder.iv_pic);

        //GlideApp.with(itemHolder.iv_pic.getContext()).load(mImgList.get(position)).override(lp.width,lp.height).fitCenter().into(itemHolder.iv_pic);
      /*  Glide.with(itemHolder.iv_pic.getContext())
                .asBitmap()
                .load(mImgList.get(position))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        itemHolder.iv_pic.setImageBitmap(resource);
                        //屏幕宽度
                        WindowManager manager = (WindowManager) itemHolder.iv_pic.getContext().getSystemService(Context.WINDOW_SERVICE);
                        int width = manager.getDefaultDisplay().getWidth()/2;
                        Log.e("screenWidth:" , width+"");
                      //  float width = ScreenUtil.getScreenWidth();
                        //缩放比例
                        DecimalFormat df=new DecimalFormat("0.00");
                        String scaleStr = df.format((float)width / resource.getWidth());
                        Log.e("scale:" , scaleStr+"");
                        Log.e("resource.getWidth:" , resource.getWidth()+",mPosition"+mPosition);
                        Log.e("resource.getHeight:" , resource.getHeight()+",mPosition"+mPosition);
                        //缩放后的宽度和高度
                        float scale = Float.valueOf(scaleStr);
                        int afterWidth = (int) (resource.getWidth() * scale);
                        int afterHeight = (int) (resource.getHeight() * scale);
                        ViewGroup.LayoutParams lp = itemHolder.iv_pic.getLayoutParams();
                        if (!mHeightMap.containsKey(mPosition)) {
                            lp.height= afterHeight;
                            mHeightMap.put(mPosition, lp.height);
                        } else {
                            lp.height = mHeightMap.get(mPosition);
                        }
                        lp.width = afterWidth;
                        Log.e("lp.width:" , lp.width +",mPosition"+mPosition);
                        Log.e("lp.height:" , lp.height+",mPosition"+mPosition);
                        itemHolder.iv_pic.setLayoutParams(lp);
                    }
                });*/

    }

    @Override
    public int getItemCount() {
        return mImgList != null ? mImgList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_pic;

        public ItemHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.iv_pic);
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback clickCallback) {
        Log.e("点击Item","");
        this.mCallback = clickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position, ImageView view);
    }
}
