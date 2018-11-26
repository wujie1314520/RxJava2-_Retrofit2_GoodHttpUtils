package com.wj.goodhttp.interceptors;


import com.wj.goodhttp.file.DownloadFileResponseBody;
import com.wj.goodhttp.file.IDownloadListener;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 作者：wujie on 2018/11/26 00:59
 * 邮箱：705030268@qq.com
 * 功能：下载拦截器
 */

public class DownloadInterceptor extends BaseInterceptor {

    private IDownloadListener mDownloadListener;

    public DownloadInterceptor(IDownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(
                new DownloadFileResponseBody(response.body(), mDownloadListener)).build();
    }
}
