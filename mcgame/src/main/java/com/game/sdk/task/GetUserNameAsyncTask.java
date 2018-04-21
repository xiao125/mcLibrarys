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

public class GetUserNameAsyncTask extends AsyncTask<Map<String, String>, Void, Void>  {

	private Context context = null;
	private String loginUrl = null;
	private Handler handler = null;

	public GetUserNameAsyncTask(Context context, Handler handler, String loginUrl) {
		this.loginUrl = loginUrl;
		this.handler  = handler;
		this.context = context;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		Message msg = handler.obtainMessage();
		
		KnLog.i("Url : " + this.loginUrl+  "GetUserNameAsyncTask params = " + params[0] );
		KnLog.log("Url : " + this.loginUrl+  "GetUserNameAsyncTask params = " + params[0] );
		
		for (int i = 0; i < 3; i++) {
			
			try {
				 String result = HttpUtil.doHttpPost(params[0], this.loginUrl);
				
				 KnLog.i("GetUserNameAsyncTask  result = " + result);
				 KnLog.log("GetUserNameAsyncTask  result = " + result);
				 
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

					 KnLog.log("验证账号返回参数="+obj);

					int resultCode = obj.getInt("code");
					

					
					switch (resultCode) {
					case ResultCode.SUCCESS:	
						 KnLog.log(" get code OK ");
						 msg.what = ResultCode.GET_USER_SUCCRESS;
					/*	String data  = obj.getString("data");*/
						 msg.obj = obj ;

						break;

					case ResultCode.GET_USER: //返回code：6

						msg.what = ResultCode.GET_USER_NoEXIStTENT;

						msg.obj = obj.toString(); //返回账号不存在的信息


						break;

					default:
						msg.what = ResultCode.GET_USER_FAIL;

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
