package com.game.sdk.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.game.sdk.ResultCode;
import com.game.sdk.bean.Result;
import com.game.sdk.util.HttpUtil;
import com.game.sdk.util.KnLog;
import com.game.sdkproxy.R;

import org.json.JSONObject;

import java.util.Map;
//import com.kngame.sdk.util.KnLog;

public class RecordActivateAsyncTask extends AsyncTask<Map<String, String>, Void, Void>  {

	private Context context = null;
	private String loginUrl = null;
	private Handler handler = null;

	public RecordActivateAsyncTask(Context context, Handler handler, String loginUrl) {
		this.loginUrl = loginUrl;
		this.handler  = handler;
		this.context = context;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		Message msg = handler.obtainMessage();
		KnLog.log("loginUrl : " + this.loginUrl+  "   RecordActivateAsyncTask params = " + params[0] );
		for (int i = 0; i < 3; i++) {
			
			try {
				 String result = HttpUtil.doHttpPost(params[0], this.loginUrl); 
				 KnLog.log("result = "+result);
				 if(result == null){
					 Thread.sleep(500L);
					 if ( i==2 ) {
						 msg.what = ResultCode.FAIL;
						 msg.obj = new Result(ResultCode.NET_DISCONNET,  context.getResources().getString(R.string.mc_tips_34).toString() ).toString() ;
						 break;
					}
				 }
				 else{
					msg.obj = result;
					
					KnLog.log("result !=null");
					JSONObject obj = new JSONObject(result);
					KnLog.log("+++++");
					int resultCode = obj.getInt("code");
					KnLog.log("obj  = "+ obj.toString());
					switch (resultCode) {
					case ResultCode.SUCCESS:

						 msg.what = ResultCode.SUCCESS;

						break;

					default:
						msg.what = ResultCode.FAIL;
						break;
					}
					
					break;
				 }
				 
			} catch (Exception e) {
				KnLog.log("Exception++");
				e.printStackTrace();
				if(i==2){
					msg.what = ResultCode.UNKNOW;
					msg.obj = new Result(ResultCode.NET_DISCONNET, "catch the exception").toString() ;
				}
			}
		
		}
		handler.sendMessage(msg);
		return null;
	}
}
