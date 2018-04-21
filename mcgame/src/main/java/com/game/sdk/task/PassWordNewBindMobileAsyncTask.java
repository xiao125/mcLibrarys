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

public class PassWordNewBindMobileAsyncTask extends AsyncTask<Map<String, String>, Void, Void>  {
	
	private Context context = null;
	private String loginUrl = null;
	private Handler handler = null;
	
	public PassWordNewBindMobileAsyncTask(Context context, Handler handler,String loginUrl) {
		this.loginUrl = loginUrl;
		this.handler  = handler;
		this.context = context;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		Message msg = handler.obtainMessage();
		
		KnLog.i("loginUrl : " + this.loginUrl+  "LoginAsyncTask params = " + params[0] );
		KnLog.log("loginUrl : " + this.loginUrl+  "LoginAsyncTask params = " + params[0] );
		
		for (int i = 0; i < 3; i++) {
			
			try {
				 String result = HttpUtil.doHttpPost(params[0], this.loginUrl);
				
				 KnLog.i("LoginAsyncTask result = " + result);
				 KnLog.log("LoginAsyncTask result = " + result);
				 
				 if(result == null){
					 Thread.sleep(500L);
					 if ( i==2 ) {
						 msg.what = ResultCode.FAIL;
						 msg.obj = new Result(ResultCode.NET_DISCONNET,  context.getResources().getString(R.string.mc_tips_34).toString() ).toString() ;
						 break;
					}
				 }
				 else{
					 
					JSONObject obj = new JSONObject(result);
					int resultCode = obj.getInt("code");
					
					String reason  = obj.getString("reason");
					
					msg.obj = reason;

					
					switch (resultCode) {
					case ResultCode.SUCCESS:
		
						 KnLog.log(" get code OK : OBJ:"+msg.obj);
						 String userName = obj.getString("user_name");
						 JSONObject json = new JSONObject();
						 json.put("reason", reason);
						 json.put("user_name", userName);
						 msg.obj = json.toString();
						 KnLog.log("obj:"+msg.obj);
						 msg.what = ResultCode.PASSWORD_NEW_SUCCESS;
						 
						break;
					default:
						msg.what = ResultCode.PASSWORD_NEW_FAIL;
						break;
					}
					
					break;
				 }
				 
			} catch (Exception e) {
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
