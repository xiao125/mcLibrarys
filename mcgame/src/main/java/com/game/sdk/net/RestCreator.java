package com.game.sdk.net;

import com.game.sdk.configurator.ConfigKeys;
import com.game.sdk.configurator.MCSDK;
import com.game.sdk.task.SDK;
import com.game.sdk.util.KnLog;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 *  Retrofit 与Okhttp组合进行网络请求
 */

public final class RestCreator {

    /**
     * 参数容器
     */
   /* private static final class ParamsHolder {
        private static final WeakHashMap<String, Object> PARAMS = new WeakHashMap<>();
    }

    public static WeakHashMap<String, Object> getParams() {
        return ParamsHolder.PARAMS;
    }*/

    /**
     * 构建OkHttp
     */
    private static final class OKHttpHolder {
        private static final int TIME_OUT = 60;
        private static final OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();

        //日志显示级别
        static final  HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        static final  HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    StringReader reader = new StringReader(message);
                    Properties properties = new Properties();
                    properties.load(reader);
                    properties.list(System.out);
                    KnLog.w("McSdk_Http:"+properties);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        private static OkHttpClient.Builder addInterceptor() {
            return BUILDER;
        }



        private static final OkHttpClient OK_HTTP_CLIENT = addInterceptor()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                //.addNetworkInterceptor(loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    /**
     * 构建全局Retrofit客户端
     */
    private static final class RetrofitHolder {

       // private static final String BASE_URL = MCSDK.getConfiguration(ConfigKeys.API_HOST);
        private static final String BASE_URL = SDK.OMD_URL;
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OKHttpHolder.OK_HTTP_CLIENT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    /**
     * Service接口
     */
    private static final class RestServiceHolder {
        private static final RestService REST_SERVICE =
                RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
    }

    public static RestService getRestService() {
        return RestServiceHolder.REST_SERVICE;
    }



}
