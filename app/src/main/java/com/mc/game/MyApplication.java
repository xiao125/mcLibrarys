package com.mc.game;

import android.app.Activity;

import com.proxy.MCApplication;
import com.proxy.configurator.MCProxys;


public class MyApplication extends MCApplication {

    private Activity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();

       MCProxys.init(this)
                .withGameID("fmsg") //游戏分配的gameId
                .withGamekey("tkvXAqJlLSewyd2h7WgjRZibaMFHIKBp") //游戏分配的gamekey
                .withGameName("fmsg") //游戏分配的gameName
                .withGameOrientation(1) //游戏横竖屏：   0：横屏   1：竖屏
                .configure();


       /* MCProxys.init(this)
                .withGameID("byyh") //游戏分配的gameId
                .withGamekey("ezoFjqmn3Cv51cgsOAP8RG2dk4Mux7TI") //游戏分配的gamekey
                .withGameName("霸业永恒") //游戏分配的gameName
                .withGameOrientation(1) //游戏横竖屏：   0：横屏   1：竖屏
                .configure();*/
    }
}

