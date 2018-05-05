package com.proxy.activity;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.R;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InvitationListener;
import com.proxy.net.RestClient;
import com.proxy.net.callback.IError;
import com.proxy.net.callback.IFailure;
import com.proxy.net.callback.ISuccess;
import com.proxy.sdk.SdkCenter;
import com.proxy.task.CommonAsyncTask;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
public class Invitation extends AppCompatActivity {
	
	private static Activity mActivity = null ;
	private static View     mBaseView = null ;
	private static InvitationListener mListener = null ;
	
	private static EditText    mEditText   			= null ;
	private static String      mServer_id            = null ;
	
	private SdkCenter sdkCenter = SdkCenter.getInstance();
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mActivity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//setContentView(R.layout.kn_invite);
		setContentView(R.layout.mcpr_inviteshow_layout);
		
		Intent intent = getIntent();
		if(intent.hasExtra("server_id")){
			mServer_id = intent.getStringExtra("server_id"); 
		}
		
		mEditText = (EditText) findViewById(R.id.invite__et);
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	public static InvitationListener getListener(){
		
		return mListener ; 
		
	}
	
	
	public void submit(View vt){
		
		LogUtil.e("邀请码确定");
		String yaoqing = mEditText.getText().toString().trim();
		
		 if(TextUtils.isEmpty(yaoqing)){
	            Util.ShowTips(mActivity,"请输入邀请码");
	            return ;
	        }
		
		 //请求邀请码接口
		 postactivations(yaoqing);
		
	}
	
	
	private void postactivations (String ed){
		
		LogUtil.e("yaoqing="+ed);
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		String imei = DeviceUtil.getDeviceId();
		final String PROXY_VERSION = "1.0.1" ;
		String gameId= Data.getInstance().getGameInfo().getGameId();

		//获取时间
		 SimpleDateFormat formatter   =   new   SimpleDateFormat("yyyyMMddHHmmss");
	     Date curDate =  new Date(System.currentTimeMillis());
	     String   time  =   formatter.format(curDate);

	     HashMap<String,Object> params = new HashMap<>();

		params.put("time", String.valueOf(time));	
		params.put("cdk", ed);
		params.put("msi", imei);
		params.put("proxyVersion",PROXY_VERSION);
		params.put("game_id",gameId);
		Map<String, Object> update_params = Util.getSign( params , app_secret );

		LoadingDialog.show(mActivity,"请求中...", true);


		//网络请求
		RestClient.builder()
				.url( Constants.URLS.ACTIVATIONS)
				.params(params)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						LoadingDialog.dismiss();
						LogUtil.e("result="+response.toString());

						Util.ShowTips(mActivity,"游戏激活成功，开始登录游戏");
						mActivity.finish();

					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {


						LoadingDialog.dismiss();
						LogUtil.e("result="+msg.toString());

						try {
							JSONObject jsonObject = new JSONObject(msg.toString());
							int resultCode = jsonObject.getInt("code");
							if(resultCode ==5){
								Util.ShowTips(mActivity,"激活码已经被使用");
							}else {
								Util.ShowTips(mActivity,"激活码错误，请重新输入");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {

					}
				})
				.build()
				.post();


/*
		new CommonAsyncTask(mActivity,Constants.URL.ACTIVATIONS, new BaseListener() {

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFail(Object result) {
				// TODO Auto-generated method stub
				LoadingDialog.dismiss();
				LogUtil.e("result="+result.toString());

				try {
					JSONObject jsonObject = new JSONObject(result.toString());
					int resultCode = jsonObject.getInt("code");
					  if(resultCode ==5){
						  Util.ShowTips(mActivity,"激活码已经被使用");
					  }else {
						  Util.ShowTips(mActivity,"激活码错误，请重新输入");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).execute(new Map[] { update_params , null, null });


		*/
		
	}
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
		
			return true;
		}
		
		return false;
	}
	
	
}
