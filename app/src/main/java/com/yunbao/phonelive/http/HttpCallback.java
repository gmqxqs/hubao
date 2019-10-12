package com.yunbao.phonelive.http;

import android.app.Dialog;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.umeng.analytics.MobclickAgent;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LauncherActivity;
import com.yunbao.phonelive.activity.LoginActivity;
import com.yunbao.phonelive.activity.LoginInvalidActivity;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

/**
 * Created by cxf on 2017/8/7.
 */

public abstract class HttpCallback extends AbsCallback<JsonBean> {

    private Dialog mLoadingDialog;
    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            Log.e("uiddLogin:", uid);
            Log.e("tokenLogin:", token);

            AppConfig.getInstance().setLoginInfo(uid, token, true);
            getBaseUserInfo();
            //友盟统计登录
            String mLoginType = "phone";
            MobclickAgent.onProfileSignIn(mLoginType, uid);

        } else {
            ToastUtil.show(msg);
        }
    }
    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (bean != null) {
                    MainActivity.forward(AppContext.getContext());
                }
            }
        });
    }
    @Override
    public JsonBean convertResponse(okhttp3.Response response) throws Throwable {
        return JSON.parseObject(response.body().string(), JsonBean.class);
    }

    @Override
    public void onSuccess(Response<JsonBean> response) {
        JsonBean bean = response.body();
        if (bean != null) {
            Log.e("JsonBean",bean.toString());
            if (200 == bean.getRet()) {
                Data data = bean.getData();
                if (data != null) {
                    Log.e("JsonData2",data.toString());
                    if (700 == data.getCode()) {
                        //token过期，重新登录
                     //   LoginInvalidActivity.forward(data.getMsg());
                        String uuid = Settings.Secure.getString(AppContext.getContext().getContentResolver(),Settings.Secure.ANDROID_ID);
                        Log.e("uuidtoken",uuid);
                        HttpUtil.TouristLogin(uuid, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                onLoginSuccess(code, msg, info);
                            }
                        });
                    } else {
                        onSuccess(data.getCode(), data.getMsg(), data.getInfo());
                    }
                } else {
                    L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                }
            } else {
                L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
            }

        } else {
            L.e("服务器返回值异常--->bean = null");
        }
    }

    @Override
    public void onError(Response<JsonBean> response) {
        Throwable t = response.getException();
        L.e("网络请求错误---->" + t.getClass() + " : " + t.getMessage());
        if (t instanceof SocketTimeoutException || t instanceof ConnectException || t instanceof UnknownHostException || t instanceof UnknownServiceException || t instanceof SocketException) {
            ToastUtil.show(R.string.load_failure);
        }
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        onError();
    }

    public void onError() {

    }


    public abstract void onSuccess(int code, String msg, String[] info);

    @Override
    public void onStart(Request<JsonBean, ? extends Request> request) {
        onStart();
    }

    public void onStart() {
        if (showLoadingDialog()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = createLoadingDialog();
            }
            mLoadingDialog.show();
        }
    }

    @Override
    public void onFinish() {
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }

}
