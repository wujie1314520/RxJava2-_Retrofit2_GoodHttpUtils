package com.wj.goodhttp.creator;

import com.google.gson.GsonBuilder;
import com.wj.goodhttp.config.ConfigKeys;
import com.wj.goodhttp.config.NetConfig;
import com.wj.goodhttp.https.HttpsUtils;
import com.wj.goodhttp.interceptors.AuthIntercepter;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 作者：wujie on 2018/11/24 16:17
 * 邮箱：705030268@qq.com
 * 功能：文件上传
 */

public final class FileUploadCreator {
    /**
     * 构建OkHttp客户端
     */
    private static final class OKHttpHolder {
        private static final int DEFAULT_TIMEOUT = 30; //默认超时时间30秒

        private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .addInterceptor(new AuthIntercepter.Builder().appTag(RequestCreator.APP_TAG).build())
                .sslSocketFactory(HttpsUtils.initSSLSocketFactory(), HttpsUtils.initTrustManager()) //trust all the https point
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 构建Retrofit客户端
     */
    private static final class RetrofitHolder {
        private static final String BASE_URL = NetConfig.getConfiguration(ConfigKeys.API_HOST);
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .client(OKHttpHolder.OK_HTTP_CLIENT)
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
