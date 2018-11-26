package com.wj.goodhttp.config;

import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Interceptor;


public final class Configurator {

    private static final HashMap<Object, Object> APP_CONFIGS = new HashMap<>();
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();

    private Configurator() {
    }

    static Configurator getInstance() {
        return Holder.INSTANCE;
    }

    final HashMap<Object, Object> getAppConfigs() {
        return APP_CONFIGS;
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    public final void configure() {
        Utils.init(NetConfig.getApplicationContext()); //初始化工具类
    }

    public final Configurator withApiHost(String hostUrl) {
        APP_CONFIGS.put(ConfigKeys.API_HOST, hostUrl);
        return this;
    }

    public final Configurator isNetLogDebug(boolean isDebug) {
        APP_CONFIGS.put(ConfigKeys.NET_LOG_DEBUG, isDebug);
        return this;
    }

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        final Object value = APP_CONFIGS.get(key);
        if (value == null) {
//            throw new NullPointerException(key.toString() + " IS NULL");
            Logger.e(key.toString() + " IS NULL");
        }
        return (T) value;
    }
}
