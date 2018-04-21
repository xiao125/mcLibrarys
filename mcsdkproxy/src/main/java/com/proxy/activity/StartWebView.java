package com.proxy.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.game.sdk.GameSDK;
import com.proxy.Data;
import com.proxy.R;
import com.proxy.bean.GameInfo;
import com.proxy.util.JsInterface;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付webview
 */

public class StartWebView extends Activity {

    private WebView webView;
    private  Activity m_activity = null;
    private TextView tv_back;
    private static String m_url;
    private static String m_wxUrl;
    private JsInterface mJsInterface = new JsInterface();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(Data.getInstance().getGameInfo().getScreenOrientation());

        setContentView(R.layout.mcpr_web_vidio);
        m_activity = this;

        m_url = getIntent().getExtras().getString("url");
        m_wxUrl = getIntent().getExtras().getString("wxUrl");
        LogUtil.e("url:" + m_url+"  wxUrl="+m_wxUrl);

        initview();
        initonClient();

        initwebsetting();

    }


    private void initview(){

        tv_back =  (TextView) findViewById(R.id.tv_back_id);
        webView = (WebView) findViewById(R.id.webView);


    }


    private void initonClient(){

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initwebsetting(){

        WebSettings ws = webView.getSettings();
        // 如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        ws.setJavaScriptEnabled(true);
        ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
        ws.setUseWideViewPort(true);// 可任意比例缩放
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setSupportMultipleWindows(true);
        webView.setWebViewClient(new mwebViewClient());
        webView.addJavascriptInterface(mJsInterface, "JsInterface");
        mJsInterface.setWvClientClickListener(new WebviewJS());// 这里就是js调用java端的具体实现
        webView.loadUrl(m_url);



    }



    public class mwebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.e("shouldOverrideUrlLoading  url = " + url);

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

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            LoadingDialog.dismiss();
        }

    }



    //js调用方法
    class WebviewJS implements JsInterface.wvClientClickListener {

        @Override
        public void wvHasClickEnvent(String title, String content,
                                     String imageUrl, String url) {

            LogUtil.e("title:" + title + "content:" + content + "imageUrl:"
                    + imageUrl + "url:" + url);

        }

        @Override
        public void wvCloseWebEvent() {
            // TODO Auto-generated method stub
            m_activity.finish();
            m_activity = null;
        }

        @Override
        public void wvWxWebPayEvent() {
            // TODO Auto-generated method stub
            LogUtil.e("wvWxWebPayEvent 回调");

            webView.loadUrl(m_wxUrl);
            Map extraHeaders = new HashMap();
            extraHeaders.put("Referer", "https://pay.ipaynow.cn");//例如 http://www.baidu.com
            webView.loadUrl(m_wxUrl, extraHeaders);//targetUrl为微信下单地址


        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume");
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause");
        if (webView != null) {
            LogUtil.e("webView is not null");
            webView.onPause();
            webView.pauseTimers();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtil.e("返回++");
                webView.loadUrl("about:blank");
                StartWebView.this.finish();
        }
        return false;
    }

}
