package com.proxy.configurator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

/**
 *
 */

public class MCProxys {

    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getLatteConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key); //获取 LATTE_CONFIGS数组中存储的状态
    }

    //获取Context
    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }

    //获取Activity
    public static Activity getActivity() {
        return getConfiguration(ConfigKeys.ACTIVITY);
    }

    //获取Handler
    public static Handler getHandler() {
        return getConfiguration(ConfigKeys.HANDLER);
    }


    /**
     * 游戏分配的gameId
     * @return
     */
    public static Activity getGameId() {
        return getConfiguration(ConfigKeys.GAME_ID);
    }


    /**
     * 游戏名字
     * @return
     */
    public static Activity getGameName() {
        return getConfiguration(ConfigKeys.GAME_NAME);
    }

    /**
     * 游戏分配的key
     * @return
     */
    public static Activity getGameKey() {
        return getConfiguration(ConfigKeys.GAME_KEY);
    }

    /**
     * 游戏横竖屏：0: 横屏 1：竖屏
     * @return
     */
    public static Activity getGameOrien() {
        return getConfiguration(ConfigKeys.GAME_ORIEN);
    }

}
