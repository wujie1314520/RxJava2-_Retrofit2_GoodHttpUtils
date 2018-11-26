package com.wj.example.api;


import com.wj.example.vo.ActivityArticle;
import com.wj.example.vo.AppVersion;
import com.wj.goodhttp.vo.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * 作者：wujie on 2018/11/23 18:30
 * 邮箱：705030268@qq.com
 * 功能：
 */

public interface INetService {

    @GET("activity/query")
    Observable<HttpResult<List<ActivityArticle>>> getActivites(@Query("pageNumber") int pagenumber, @Query("pageSize") int pagesize);

    @POST("version/android/patch")
    Observable<HttpResult<AppVersion>> getLatestVersion(@Query("code") int code);

    @POST("activity/add")
    Observable<HttpResult<ActivityArticle>> addActivityTheme(@Body Object jsonObject);

    @Multipart
    @POST("userprofile/uploadavatar")
    Observable<ResponseBody> uploadAvatar(@Part MultipartBody.Part file);

    @Multipart
    @POST("userprofile/uploadavatar2")
    Observable<ResponseBody> uploadAvatar2(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("bike/fault/add")
    Observable<ResponseBody> uploadIssueReport(@PartMap Map<String, RequestBody> params);
}
