package com.proxy.configurator;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Interceptor;

/**
 * 配置相关,存入数组中
 */

public class Configurator {

    private static final HashMap<Object, Object> LATTE_CONFIGS = new HashMap<>();
    private static final Handler HANDLER = new Handler();
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();

    private Configurator() {
        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY, false); //初始化
        LATTE_CONFIGS.put(ConfigKeys.HANDLER, HANDLER);
    }

    static Configurator getInstance() {
        return Holder.INSTANCE;
    }

    final HashMap<Object, Object> getLatteConfigs() {
        return LATTE_CONFIGS;
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    public final void configure() {

        LATTE_CONFIGS.put(ConfigKeys.CONFIG_READY, true);
      Utils.init(MCProxys.getApplicationContext());
    }

    public final Configurator withApiHost(String host) {
        LATTE_CONFIGS.put(ConfigKeys.API_HOST, host);
        return this;
    }

    //网络加载延时时间
    public final Configurator withLoaderDelayed(long delayed) {
        LATTE_CONFIGS.put(ConfigKeys.LOADER_DELAYED, delayed);
        return this;
    }


    public final Configurator withGameID(String gameId){
        LATTE_CONFIGS.put(ConfigKeys.GAME_ID,gameId);
        return this;
    }
    public final Configurator withGameName(String gameName){
        LATTE_CONFIGS.put(ConfigKeys.GAME_NAME,gameName);
        return this;
    }

    public final Configurator withGamekey(String gamekey){
        LATTE_CONFIGS.put(ConfigKeys.GAME_KEY,gamekey);
        return this;
    }


    public final Configurator withGameOrientation(int Orientation){
        LATTE_CONFIGS.put(ConfigKeys.GAME_ORIEN,Orientation);
        return this;
    }


    public final Configurator withActivity(Activity activity) {
        LATTE_CONFIGS.put(ConfigKeys.ACTIVITY, activity);
        return this;
    }



    public Configurator withJavascriptInterface(@NonNull String name) {
        LATTE_CONFIGS.put(ConfigKeys.JAVASCRIPT_INTERFACE, name);
        return this;
    }



    private void checkConfiguration() { //是否初始化
        final boolean isReady = (boolean) LATTE_CONFIGS.get(ConfigKeys.CONFIG_READY);
        if (!isReady) {
            throw new RuntimeException("Configuration is not ready,call configure");
        }
    }

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        final Object value = LATTE_CONFIGS.get(key);
        if (value == null) {
            throw new NullPointerException(key.toString() + " IS NULL");
        }
        return (T) LATTE_CONFIGS.get(key);
    }
}
