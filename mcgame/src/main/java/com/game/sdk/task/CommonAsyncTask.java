package com.game.sdk.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.game.sdk.ResultCode;
import com.game.sdk.bean.Result;
import com.game.sdk.listener.BaseListener;
import com.game.sdk.util.HttpUtil;
import com.game.sdk.util.KnLog;
import com.game.sdkproxy.R;

import org.json.JSONObject;

import java.util.Map;

public class CommonAsyncTask extends AsyncTask<Map<String, String>, Void, Void> {
	private String postUrl;
	private Activity activity;
	private BaseListener listener;

	public CommonAsyncTask(Activity activity, String postUrl) {
		this.activity = activity;
		this.postUrl = postUrl;
	}

	public CommonAsyncTask(Activity activity, String postUrl, BaseListener listener) {
		this.activity = activity;
		this.postUrl = postUrl;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		KnLog.e("postUrl : " + this.postUrl+ "CommonAsyncTask params = " + params[0]);
		
		for (int i = 0; i < 3; i++) {
			try {
				String result = HttpUtil.doHttpPost(params[0], this.postUrl);
				
				KnLog.e( "CommonAsyncTask result = " + result );
				
				if (result == null) {
					Thread.sleep(500L);
					if (i == 2) {
						KnLog.e("请检查网络是否连接");
						excuteCallback(ResultCode.FAIL , new Result(ResultCode.NET_DISCONNET, activity.getResources().getString(R.string.mc_tips_34) ).toString() );
						break;
					}
				}
				else{
					
					JSONObject jsonObject = new JSONObject(result);
					int resultCode = jsonObject.getInt("code");
	
					switch (resultCode) {
					case ResultCode.SUCCESS:
						excuteCallback(ResultCode.SUCCESS, result);
						break;
	
					default:
						excuteCallback(ResultCode.FAIL , result);
						break;
					}
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
				if (i == 2)
				{
					excuteCallback(ResultCode.UNKNOW , new Result(ResultCode.NET_DISCONNET, "catch the exception").toString());
				}
			}
		}

		return null;
	}
	
	public void excute(final int type , final String result){
		switch (type) {
		case ResultCode.SUCCESS:
			this.listener.onSuccess(result);
			break;
			
		case ResultCode.FAIL:
			this.listener.onFail(result);
			break;
			
		case ResultCode.UNKNOW:
			this.listener.onFail(result);
			break;
		default:
			break;
		}
	}
	
	public void excuteCallback(final int type , final String result){
		
		if (activity==null) {
			excute(type, result);
		}else{
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					excute(type, result);
				}
			});
		}
	}
}