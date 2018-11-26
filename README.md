# RxJava2_Retrofit2_GoodHttpUtils
该网络库基于OkHttp3，Retrofit2和RxJava2进行了高度封装，使网络请求变得异常方便，支持单文件，多文件上传及进度监听，支持文件下载及进度监听

## 目前对以下需求进行了封装
* 一般的请求（类似表单）
* 传复杂的Json对象
* 单个文件上传监听进度
* 单个文件上传监听进度+其他表单参数
* 多文件上传监听进度+其他表单参数
* 多文件上传监听进度+其他表单参数
* 文件下载监听进度

##项目展示
![](https://github.com/wujie1314520/RxJava2-_Retrofit2_GoodHttpUtils/blob/master/imgs/goodhttp3.png)

## 用法
## 在Application的onCreate中初始化配置
```java
//初始化配置
NetConfig.init(this)
                .withApiHost(BuildConfig.DEBUG ? Constant.API_URL.ENV_DEV : Constant.API_URL.ENV_PROD) //调试环境Url Or 生产环境Url
                .isNetLogDebug(BuildConfig.DEBUG)  //生产环境不打印网络日志
                .configure();
```
##其次，根据服务端的接口协议，申明接口
## INetService
```java
public interface INetService {
    @GET("activity/query")
    Observable<HttpResult<List<ActivityArticle>>> getActivites(@Query("pageNumber") int pagenumber, @Query("pageSize") int pagesize);

    @POST("version/android/patch")
    Observable<HttpResult<AppVersion>> getLatestVersion(@Query("code") int code);

    //post复杂的Json对象
    @POST("activity/add")
    Observable<HttpResult<ActivityArticle>> addActivityTheme(@Body Object jsonObject);

    //上传单个文件
    @Multipart
    @POST("userprofile/uploadavatar")
    Observable<ResponseBody> uploadAvatar(@Part MultipartBody.Part file);

    //上传文件+其他表单参数
    @Multipart
    @POST("userprofile/uploadavatar2")
    Observable<ResponseBody> uploadAvatar2(@PartMap Map<String, RequestBody> params);

    //上传多个文件+其他参数
    @Multipart
    @POST("bike/fault/add")
    Observable<ResponseBody> uploadIssueReport(@PartMap Map<String, RequestBody> params);
}
```
## ApiService
```java
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

    //获取活动列表
    public Observable<List<ActivityArticle>> getActivites(int pagenumber, int pagesize) {
        return mApiService.getActivites(pagenumber, pagesize)
                .map(new HttpResultFunc<>())
                .compose(io_main());
    }

    //Post
    public Observable<Boolean> addActivity(ActivityArticle activity) {
        return mApiService.addActivityTheme(activity)
                .map(new BooleanResultFunc<>())
                .compose(io_main());
    }

}
```
##统一的异常返回处理，需要根据自己服务端返回的格式做适配
```java
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
```
##拦截器，内置了用户身份认证拦截器
```java
@Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder().addHeader("User-Agent", APP_TAG); // 标明发送本次请求的客户端
        Logger.d(APP_TAG);
        //如果用户已经登陆，每次请求头带上token
        if (StringUtils.isNotBlank(AccountManager.getInstance().getToken())) {
            builder.addHeader("token", AccountManager.getInstance().getToken());
            Logger.i("token：%s", AccountManager.getInstance().getToken());
        }
        Request request = builder.build();
        Response response = null;
        try {
            response = chain.proceed(request);
            String responseBody = getResponse(response);
            //处理登录过期情况
            handleTokenExpired(responseBody);
        } catch (IOException e) {
            Logger.e(e.getMessage());
        }
        return response;
    }
```
##如果需要拓展拦截器，可以
```java
NetConfig.init(this)
                .withApiHost(BuildConfig.DEBUG ? Constant.API_URL.ENV_DEV : Constant.API_URL.ENV_PROD) //调试环境Url Or 生产环境Url
                .isNetLogDebug(BuildConfig.DEBUG)  //生产环境不打印网络日志
                .withInterceptors(Arrays.asList(new CookieInterceptors()))
                .configure();
```
##一般的请求
```java
ApiService.getInstance().getActivites(1, 20)
                .subscribe(activityArticles -> ToastUtils.showShort("请求成功"), new ErrorAction());
```
##Post复杂的Json对象
```java
ActivityArticle article = new ActivityArticle();
        article.title = "ArchSummit全球架构师峰";
        article.articleurl = "https://www.oschina.net/event/2288817";
        article.image1url = "https://static.oschina.net/uploads/space/2018/1102/181140_cfy7_3843409.jpg";
        article.image2url = "https://static.oschina.net/uploads/space/2018/1102/181157_MYXH_3843409.jpg";
        ApiService.getInstance().addActivity(article)
                .subscribe(aBoolean -> ToastUtils.showShort("请求成功"), new ErrorAction());
```
###单个文件上传监听进度+其他表单参数
```java
FileUploadService.getInstance().uploadAvatar2(uploadFile, "20180607090729115PHHBSWE", new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                ToastUtils.showShort("文件上传成功");
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                ToastUtils.showShort("文件上传失败");
            }

            @Override
            public void onProgress(int progress) {
                Logger.d("下载进度为:%d", progress);
            }
        });
```
###多文件上传监听进度+其他表单参数
```java
FileUploadService.getInstance().uploadIssueReport(files, "00000001", "开不了了", "真的开不了了",
                new FileUploadObserver<ResponseBody>() {
                    @Override
                    public void onUpLoadSuccess(ResponseBody responseBody) {
                        ToastUtils.showShort("文件上传成功");
                    }

                    @Override
                    public void onUpLoadFail(Throwable e) {
                        ToastUtils.showShort("文件上传失败");
                    }

                    @Override
                    public void onProgress(int progress) {
                        Logger.d("下载进度为:%d", progress);
                    }
                });
```
###文件下载监听进度
```java
public void download() {
        String url = "http://192.168.1.8:8080/rent/assets/img/bikefault/20181125222314579QDUGWYOO1.png";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡已挂载
            String fileDirPath = new StringBuilder().append(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/AndFix/";
            File fileDir = new File(fileDirPath);
            try {
                if(null == fileDir || !fileDir.exists()) {
                    fileDir.mkdir();
                }
                Logger.d("存储位置:%s", fileDirPath.concat(System.currentTimeMillis() + ".").concat(FileUtils.getFileExtension(url)));
                FileDownloadService.getInstance(new IDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        ToastUtils.showShortSafe("文件下载成功");
                    }

                    @Override
                    public void onDownloadFail(Exception exception) {
                        ToastUtils.showShortSafe("文件下载失败");
                    }

                    @Override
                    public void onProgress(int progress) {
                        Logger.d("文件下载%d", progress);
                    }
                }).download(url, fileDirPath.concat(System.currentTimeMillis() + ".").concat(FileUtils.getFileExtension(url)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.showShortSafe("SD卡未挂载");
        }
    }
```