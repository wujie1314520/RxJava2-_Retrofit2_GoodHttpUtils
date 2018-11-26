package com.wj.goodhttp.interceptors;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：wujie on 2018/11/24 20:09
 * 邮箱：705030268@qq.com
 * 功能：身份认证拦截器
 */

public class AuthIntercepter extends BaseInterceptor {
    //客户端标志
    private String APP_TAG = "";

    private AuthIntercepter(Builder builder) {
        this.APP_TAG = builder.appTag;
    }

    public static final class Builder {
        private String appTag;

        public Builder() {
        }

        public Builder appTag(String val) {
            appTag = val;
            return this;
        }

        public AuthIntercepter build() {
            return new AuthIntercepter(this);
        }
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder().addHeader("User-Agent", APP_TAG); // 标明发送本次请求的客户端
        Logger.d(APP_TAG);
        //如果用户已经登陆，每次请求头带上token
//        if (StringUtils.isNotBlank(AccountManager.getInstance().getToken())) {
//            builder.addHeader("token", AccountManager.getInstance().getToken());
//            Logger.i("token：%s", AccountManager.getInstance().getToken());
//        }
        Request request = builder.build();
        Response response = null;
        try {
            response = chain.proceed(request);
            String responseBody = getResponse(response);
            //处理登录过期情况
//            handleTokenExpired(responseBody);
        } catch (IOException e) {
            Logger.e(e.getMessage());
        }
        return response;
    }


}
