package com.wj.goodhttp.config;

import android.content.Context;

/**
 * 作者：wujie on 2017/9/19 11:21
 * 邮箱：705030268@qq.com
 * 功能：
 */

public final class NetConfig {

    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getAppConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key);
    }

    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }
}
