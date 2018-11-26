package com.wj.goodhttp.creator;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 作者：wujie on 2018/11/26 15:26
 * 邮箱：705030268@qq.com
 * 功能：
 */

public interface IApiService {

    @Streaming  //防止OOM最好加上这个注解
    @GET
    Observable<ResponseBody> download(@Url String url);
}
