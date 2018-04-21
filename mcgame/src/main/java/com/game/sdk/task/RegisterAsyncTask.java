package com.game.sdk.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.game.sdk.ResultCode;
import com.game.sdk.bean.Result;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.HttpUtil;
import com.game.sdk.util.KnLog;
import com.game.sdkproxy.R;

import org.json.JSONObject;

import java.util.Map;

public class RegisterAsyncTask extends
		AsyncTask<Map<String, String>, Void, Void> {

	private String regUrl = null;
	private Handler handler = null;
	private Context context = null;

	public RegisterAsyncTask(Context context, Handler handler, String regUrl) {
		this.context = context;
		this.regUrl = regUrl;
		this.handler = handler;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		KnLog.i("regUrl : " + this.regUrl+ "RegisterAsyncTask params = " + params[0]);
		
		Message msg = handler.obtainMessage();
		for (int i = 0; i < 3; i++) {

			try {
				String result = HttpUtil.doHttpPost(params[0], this.regUrl);

				 KnLog.i("result = " + result);

				if (result == null) {

					Thread.sleep(500L);

					if (i == 2) {
						 msg.obj = new Result(ResultCode.NET_DISCONNET, context.getResources().getString(R.string.mc_tips_34).toString() ).toString() ;
						handler.sendMessage(msg);
						break;
					}
				} 
				else {
					JSONObject obj = new JSONObject(result);
					int resultCode = obj.getInt("code");

					switch (resultCode) {
					case ResultCode.SUCCESS:

						JSONObject content = new JSONObject(
								params[0].get("content"));
						DBHelper.getInstance().insertOrUpdateUser(
								content.getString("user_name"),
								content.getString("passwd"));

						msg.what = ResultCode.SUCCESS;
						break;
					default:
						msg.what = ResultCode.FAIL;
						break;
					}

					msg.obj = result;
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();

				if (i == 2) {
					msg.what = ResultCode.UNKNOW;
					msg.obj = new Result(ResultCode.NET_DISCONNET, "catch the exception").toString() ;
				}
			}
		}
		handler.sendMessage(msg);
		return null;
	}

}
