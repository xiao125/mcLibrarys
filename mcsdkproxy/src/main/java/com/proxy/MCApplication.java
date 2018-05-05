package com.proxy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.game.sdk.configurator.MCSDK;
import com.proxy.configurator.MCProxys;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class MCApplication extends Application{


    private Activity mActivity;
    @Override
    public void onCreate() {
        super.onCreate();

        MCProxys.init(MCApplication.this)
                .withApiHost(Constants.OMD_URLS)
                .withLoaderDelayed(0)
                .withActivity(mActivity)
                .configure();




        MCSDK.init(MCApplication.this)
                .withApiHost(Constants.OMD_URLS)
                .withLoaderDelayed(0)
                .withActivity(mActivity)
                .configure();

    }


}
