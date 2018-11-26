package com.wj.example.api;

import com.blankj.utilcode.util.AppUtils;
import com.wj.example.vo.ActivityArticle;
import com.wj.example.vo.AppVersion;
import com.wj.goodhttp.creator.RequestCreator;
import com.wj.goodhttp.map.BooleanResultFunc;
import com.wj.goodhttp.map.HttpResultFunc;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 作者：wujie on 2018/11/23 18:30
 * 邮箱：705030268@qq.com
 * 功能：RxJava + Retrofit + OkHttp
 */

public class ApiService {
    private final INetService mApiService;

    private ApiService() {
        mApiService = RequestCreator.getRetrofitClient().create(INetService.class);
    }

    //静态内部类创建单例
    public static class InstanceHolder {
        private static final ApiService INSTANCE = new ApiService();
    }

    public static ApiService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    //订阅的时候在IO线程，数据返回的时候在主线程
    public static <T> ObservableTransformer<T, T> io_main() {
        return tObservable -> tObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取最新版本号和补丁文件
     */
    public Observable<AppVersion> getLatestVersion() {
        return mApiService.getLatestVersion(AppUtils.getAppVersionCode())
                .map(new HttpResultFunc<>())
                .compose(io_main());
    }

    public Observable<List<ActivityArticle>> getActivites(int pagenumber, int pagesize) {
        return mApiService.getActivites(pagenumber, pagesize)
                .map(new HttpResultFunc<>())
                .compose(io_main());
    }

    public Observable<Boolean> addActivity(Object activity) {
        return mApiService.addActivityTheme(activity)
                .map(new BooleanResultFunc<>())
                .compose(io_main());
    }

}
