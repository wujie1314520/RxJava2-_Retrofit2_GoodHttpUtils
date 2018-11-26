package com.wj.goodhttp.exception;


import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;


public class ErrorAction implements Consumer<Throwable> {
    @Override
    public void accept(Throwable throwable) throws Exception {
        Logger.e("异常日志", throwable);
        if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            ToastUtils.showShortSafe("网络错误");
        } else if (throwable instanceof SocketTimeoutException) {
            ToastUtils.showShortSafe("连接超时，请重试");
        } else if (throwable instanceof HttpException) {
            ToastUtils.showShortSafe("服务器错误(" + ((HttpException) throwable).code());
        } else if (throwable instanceof ApiException) {
            onApiError((ApiException) throwable);
        } else {
            //未知错误，最好将其上报给服务端，供异常排查
            if (!TextUtils.isEmpty(throwable.getMessage())) {
                ToastUtils.showShortSafe(throwable.getMessage());
            }
        }
    }

    public void onApiError(ApiException throwable) {
        //有errorMsg优先吐msg,没有吐errcode,两者区别：msg一般是比较友好的中文说明
        if (throwable.getMessage() != null)
            ToastUtils.showShortSafe(throwable.getMessage());
        else if (throwable.getErrorCode() != null) {
            ToastUtils.showShortSafe(throwable.getErrorCode());
        }
    }

}
