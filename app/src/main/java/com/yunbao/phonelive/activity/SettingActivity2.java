package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.SettingAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.SettingBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.GlideCatchUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VersionUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity2 extends AbsActivity  {


    private Handler mHandler;
    private LinearLayout versionLinearLayout;
    private LinearLayout clearLinearLayout;
    private TextView textVersion;
    private TextView clearText;
    private ImageView btn_back;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, SettingActivity2.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting2;
    }

    @Override
    protected void main() {
        versionLinearLayout = findViewById(R.id.version);
        versionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
        textVersion = findViewById(R.id.textVersion);
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        textVersion.setText(configBean.getVersion());
        clearLinearLayout = findViewById(R.id.clearLinearLayout);
        clearText = findViewById(R.id.clearText);
        clearText.setText(getCacheSize());
        clearLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }





    /**
     * 检查更新
     */
    private void checkVersion() {
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                Log.e("configBean",configBean.toString());
                if (configBean != null) {
                    Log.e("configBean",configBean.getVersion()+"aaahhhh");
                    if (VersionUtil.isLatest(configBean.getVersion())) {

                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        AppConfig.getInstance().clearLoginInfo();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
        LoginActivity.forward();
    }

    /**
     * 修改密码
     */
    private void forwardModifyPwd() {
        startActivity(new Intent(mContext, ModifyPwdActivity.class));
    }

    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(AppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                ToastUtil.show(R.string.setting_clear_cache);
                clearText.setText(getCacheSize());
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        HttpUtil.cancel(HttpConsts.GET_SETTING_LIST);
        HttpUtil.cancel(HttpConsts.GET_CONFIG);
        super.onDestroy();
    }
}
