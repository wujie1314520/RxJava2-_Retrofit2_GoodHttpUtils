package com.wj.goodhttp.creator;

import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.GsonBuilder;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.wj.goodhttp.config.ConfigKeys;
import com.wj.goodhttp.config.NetConfig;
import com.wj.goodhttp.https.HttpsUtils;
import com.wj.goodhttp.interceptors.AuthIntercepter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 作者：wujie on 2018/11/24 16:17
 * 邮箱：705030268@qq.com
 * 功能：普通网络请求
 */

public final class RequestCreator {

    //Build.VERSION.RELEASE: Android系统 比如7.0
    //Build.MODEL: 手机型号 比如 MI 5
    public static final String APP_TAG = new StringBuilder().append("android-").append(AppUtils.getAppVersionName())
            .append(Build.VERSION.RELEASE).append("-").append(Build.MODEL).toString();

    /**
     * 构建OkHttp客户端
     */
    private static final class OKHttpHolder {
        private static final int DEFAULT_TIMEOUT =10; //默认超时时间10秒
        private static final OkHttpClient.Builder HTTP_CLIENT_BUILDER = new OkHttpClient.Builder();

        //外置拦截器：用于拓展其他公用拦截器
        private static final ArrayList<Interceptor> INTERCEPTORS = NetConfig.getConfiguration(ConfigKeys.INTERCEPTOR);

        private static OkHttpClient.Builder addInterceptor() {
            if (INTERCEPTORS != null && !INTERCEPTORS.isEmpty()) {
                for (Interceptor interceptor : INTERCEPTORS) {
                    HTTP_CLIENT_BUILDER.addInterceptor(interceptor);
                }
            }
            //日志拦截器,身份认证拦截器
            addLogAuthorInterceptor();
            return HTTP_CLIENT_BUILDER;
        }

        //必要拦截器：日志拦截器,身份认证拦截器
        private static void addLogAuthorInterceptor() {
            //调试环境，打印网络请求日志；生产环境，关闭
//            if (BuildConfig.DEBUG) { //这样不行，android library中默认是不支持debug模式的，BuildConfig.DEBUG永远是false
                //打印网络请求日志
//                HTTP_CLIENT_BUILDER.addInterceptor(getLoggingInterceptor());
//            }
            boolean isNetLogDebug = NetConfig.getConfiguration(ConfigKeys.NET_LOG_DEBUG);
            if(isNetLogDebug) {
                HTTP_CLIENT_BUILDER.addInterceptor(getLoggingInterceptor());
            }
            //身份认证拦截器
            HTTP_CLIENT_BUILDER.addInterceptor(new AuthIntercepter.Builder().appTag(APP_TAG).build());
        }

        private static final OkHttpClient OK_HTTP_CLIENT = addInterceptor()
                .sslSocketFactory(HttpsUtils.initSSLSocketFactory(), HttpsUtils.initTrustManager()) //trust all the https point
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true) //断网重连，okhttp不支持设置断网重连的次数，如果想对断网重连次数做限制，可以采用拦截器
                .build();
    }


    public static LoggingInterceptor getLoggingInterceptor() {
        LoggingInterceptor httpLoggingInterceptor = new LoggingInterceptor.Builder()
                .loggable(true)
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .build();
        return httpLoggingInterceptor;
    }

    /**
     * 构建Retrofit客户端
     */
    private static final class RetrofitHolder {
        private static final String BASE_URL = NetConfig.getConfiguration(ConfigKeys.API_HOST);
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .client(OKHttpHolder.OK_HTTP_CLIENT)
//                .addConverterFactory(ScalarsConverterFactory.create()) //转换器，请求结果转换成原始json字符串
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create())) //转换器，请求结果转换成VO
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //配合Rxjava使用，将retofit的call映射成Observable
                .baseUrl(BASE_URL)
                .build();
    }

//    /**
//     * Service接口 =》 VO(POJO)
//     */
//    private static final class RequestServiceHolder {
//        private static final IApiService IAPI_SERVICE = RetrofitHolder.RETROFIT_CLIENT.create(IApiService.class);
//    }
//
//    public static IApiService getIApiService() {
//        return RequestServiceHolder.IAPI_SERVICE;
//    }

    public static Retrofit getRetrofitClient() {
        return RetrofitHolder.RETROFIT_CLIENT;
    }
}
