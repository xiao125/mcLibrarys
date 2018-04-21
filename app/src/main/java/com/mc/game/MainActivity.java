package com.mc.game;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity   {

    private WebView mwb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mwb = findViewById(R.id.wb);

      mwb.setWebViewClient(new WebViewClient(){

           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {

               // 如下方案可在非微信内部WebView的H5页面中调出微信支付
               if (url.startsWith("weixin://wap/pay?")){
                   Intent intent = new Intent();
                   intent.setAction(Intent.ACTION_VIEW);
                   intent.setData(Uri.parse(url));
                   startActivity(intent);
                  return true;
               }else {

                   //请求头
                   Map extraHeaders = new HashMap();
                   extraHeaders.put("Referer","https://pay.ipaynow.cn");
                   view.loadUrl(url,extraHeaders);
               }


               return super.shouldOverrideUrlLoading(view, url);
           }

          //处理https请求
          @Override
          public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
             handler.proceed();
          }

          @Override
           public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                  if (url!=null){
                      Log.d("GTA","================"+url );
                      Map extraHeaders = new HashMap();
                      extraHeaders.put("Referer","https://pay.ipaynow.cn");
                     // mwb.loadUrl(url,extraHeaders);
                  }

               return super.shouldInterceptRequest(view, url);
           }
       });

        WebSettings webSettings = mwb.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Map extraHeaders = new HashMap();
        extraHeaders.put("Referer","https://pay.ipaynow.cn");
        String url = "http://h5pay.u7game.cn/pay/wap/?m=gamepay&a=ipaynow_demo";
        String urll="https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx041701480840861c0a5cf2a52918108920&package=3068484999";
        String urlls="http://wxpay.weixin.qq.com/pub_v2/pay/wap.v2.php";

        mwb.loadUrl(url,extraHeaders);


        Log.d("GTA","" );

    }


   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mwb.canGoBack()){

            mwb.goBack();// 返回前一个页面
            mwb.goBack();// 返回前一个页面

           /* this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mwb.reload(); //刷新
                        }
                    },500);
                }
            });
*/


           // mwb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mwb.removeAllViews();
        mwb.destroy();
        mwb=null;
    }
}
