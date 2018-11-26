package com.wj.example;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.wj.goodhttp.config.NetConfig;

/**
 * 作者：wujie on 2018/11/26 15:27
 * 邮箱：705030268@qq.com
 * 功能：
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        initLog();
    }

    //初始化日志：生产环境不输出日志
    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    //初始化自定义配置
    private void initConfig() {
        NetConfig.init(this)
                .withApiHost(BuildConfig.DEBUG ? Constant.API_URL.ENV_DEV : Constant.API_URL.ENV_PROD) //调试环境Url Or 生产环境Url
                .isNetLogDebug(BuildConfig.DEBUG)  //生产环境不打印网络日志
                .configure();
    }
}
