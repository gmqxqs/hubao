package com.yunbao.phonelive.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cxf on 2018/9/26.
 */

public class HomeListHorizontalItemLayout extends RelativeLayout {

    public HomeListHorizontalItemLayout(Context context) {
        super(context);
    }

    public HomeListHorizontalItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeListHorizontalItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
      //  heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 13 / 9), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize / 2), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
