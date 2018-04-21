package com.game.sdk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.game.sdk.activity.StartWebView;
import com.game.sdkproxy.R;
import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class WxTools {

	private WebView m_view = null ;
	private static Activity   mActivity = null;
	private static WxTools m_instance = null ;
	private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	private  static View		    m_View = null ;

	
	public View getM_View() {
		return m_View;
	}

	public void setM_View(View m_View) {
		this.m_View = m_View;
	}

	private WxTools(){
	
	}
	
	public static  WxTools getIntance(){
		if(m_instance!=null){
			
		}else{
			m_instance = new WxTools();
		}
		return m_instance;
	}


	//打开web支付页面
	public void openWeb( Activity act , final String url ){

		Intent intent = new Intent(act,StartWebView.class);
		intent.putExtra("url",  url );
		act.startActivity( intent );

	}

	 
	
	 
}
