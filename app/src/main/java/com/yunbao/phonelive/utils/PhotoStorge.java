package com.yunbao.phonelive.utils;

import android.media.Image;

import com.yunbao.phonelive.bean.ImageBean;
import com.yunbao.phonelive.bean.PhotoBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.interfaces.ImageScrollDataHelper;
import com.yunbao.phonelive.interfaces.VideoScrollDataHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/6/9.
 */

public class PhotoStorge {

    private static PhotoStorge sInstance;
    private Map<String, List<PhotoBean>> mMap;
    private Map<String, ImageScrollDataHelper> mHelperMap;


    private PhotoStorge() {
        mMap = new HashMap<>();
        mMap = new HashMap<>();
        mHelperMap = new HashMap<>();

    }

    public static PhotoStorge getInstance() {
        if (sInstance == null) {
            synchronized (PhotoStorge.class) {
                if (sInstance == null) {
                    sInstance = new PhotoStorge();
                }
            }
        }
        return sInstance;
    }

    public void put(String key, List<PhotoBean> list) {
        if (mMap != null) {
            mMap.put(key, list);
        }
    }




    public List<PhotoBean> get(String key) {
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
