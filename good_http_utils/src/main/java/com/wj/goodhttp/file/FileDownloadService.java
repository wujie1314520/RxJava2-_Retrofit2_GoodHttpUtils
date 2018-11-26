package com.wj.goodhttp.file;

import com.google.gson.GsonBuilder;
import com.wj.goodhttp.config.ConfigKeys;
import com.wj.goodhttp.config.NetConfig;
import com.wj.goodhttp.creator.IApiService;
import com.wj.goodhttp.exception.ErrorAction;
import com.wj.goodhttp.interceptors.DownloadInterceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：wujie on 2018/11/24 22:10
 * 邮箱：705030268@qq.com
 * 功能：文件下载
 */

public class FileDownloadService {
    private static FileDownloadService mInstance = null;
    private static final int DEFAULT_TIMEOUT = 30; //默认超时时间30秒
    private final IApiService mApiService;
    private final IDownloadListener mListener;

    private FileDownloadService(IDownloadListener listener) {
        mListener = listener;
        OkHttpClient OkhttpClient = new OkHttpClient.Builder()
                .addInterceptor(new DownloadInterceptor(listener))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofitClient = new Retrofit.Builder()
                .client(OkhttpClient)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create())) //转换器，请求结果转换成VO
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //配合Rxjava使用，将retofit的call映射成Observable
                .baseUrl((String) NetConfig.getConfiguration(ConfigKeys.API_HOST))
                .build();
        mApiService = retrofitClient.create(IApiService.class);
    }

    public static FileDownloadService getInstance(IDownloadListener listener) {
        if(null == mInstance) {
            synchronized (FileDownloadService.class) {
                if(null == mInstance) {
                    mInstance = new FileDownloadService(listener);
                }
            }
        }
        return mInstance;
    }

    /**
     * 文件下载
     *  subscribeOn()改变调用它之前代码的线程
     *  observeOn()改变调用它之后代码的线程
     */
    public void download(String url, String filePath) {
        mApiService.download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(responseBody -> responseBody.byteStream())
                .doOnError(new ErrorAction())
//                .observeOn(Schedulers.computation()) // 用于计算任务
                .doOnNext(inputStream -> saveFile(inputStream, filePath))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 将输入流写入文件
     * @param inputString
     * @param filePath
     */
    private void saveFile(InputStream inputString, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b,0,len);
            }
            inputString.close();
            fos.close();
            mListener.onDownloadSuccess();
        } catch (Exception exception) {
            mListener.onDownloadFail(exception);
        }
    }

}
