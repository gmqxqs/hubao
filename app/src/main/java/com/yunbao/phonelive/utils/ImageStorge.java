package com.yunbao.phonelive.utils;

import android.media.Image;

import com.yunbao.phonelive.bean.ImageBean;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.interfaces.ImageScrollDataHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/6/9.
 */

public class ImageStorge {

    private static ImageStorge sInstance;
    private Map<String, List<ImageBean>> mMap;
    private Map<String, ImageScrollDataHelper> mHelperMap;


    private ImageStorge() {
        mMap = new HashMap<>();
        mMap = new HashMap<>();
        mHelperMap = new HashMap<>();

    }

    public static ImageStorge getInstance() {
        if (sInstance == null) {
            synchronized (ImageStorge.class) {
                if (sInstance == null) {
                    sInstance = new ImageStorge();
                }
            }
        }
        return sInstance;
    }

    public void put(String key, List<ImageBean> list) {
        if (mMap != null) {
            mMap.put(key, list);
        }
    }




    public List<ImageBean> get(String key) {
        if (mMap != null) {
            return mMap.get(key);
        }
        return null;
    }



    public void remove(String key) {
        if (mMap != null) {
            mMap.remove(key);
        }

    }


    public void clear() {
        if (mMap != null) {
            mMap.clear();
        }

        if (mHelperMap != null) {
            mHelperMap.clear();
        }

    }

    public void putDataHelper(String key, ImageScrollDataHelper helper) {
        if (mHelperMap != null) {
            mHelperMap.put(key, helper);
        }

    }




    public ImageScrollDataHelper getDataHelper(String key) {
        if (mHelperMap != null) {
            return mHelperMap.get(key);
        }
        return null;
    }

    public void removeDataHelper(String key) {
        if (mHelperMap != null) {
            mHelperMap.remove(key);
        }
    }
}
