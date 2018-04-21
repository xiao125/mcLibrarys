package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;

/**
 * 游客登录第一次出现
 */
public class AccountManagerActivity extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private String  m_userNames = null ;
	private ImageView m_image_back,m_select_log_close;
	private Button m_visit_continue,m_visit_bind_account;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		m_activity = this ; 
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_visit_manager_dialog);

		initView();

		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");
		 

		
	}

	private void initView() {

		m_image_back = (ImageView) findViewById(R.id.image_back);
		m_select_log_close = (ImageView) findViewById(R.id.select_log_close);
		m_visit_continue= (Button) findViewById(R.id.visit_continue);
		m_visit_bind_account= (Button) findViewById(R.id.visit_bind_account_zh);

		m_image_back.setOnClickListener(this);
		m_select_log_close.setOnClickListener(this);
		m_visit_continue.setOnClickListener(this);
		m_visit_bind_account.setOnClickListener(this);
	}


	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;

		if(id==R.id.visit_bind_account_zh){ //升级萌创账号
//			KnLog.log("账号绑定++");
			intent = new Intent(AccountManagerActivity.this,BindCellActivity.class);
			intent.putExtra("userName",m_userNames);

		}else if (id== R.id.image_back){ //返回

			if (m_activity!=null){
				m_activity.finish();
				m_activity = null ;
			}

		}else if (id==R.id.select_log_close){ //关闭

			if (m_activity!=null){
				m_activity.finish();
				m_activity = null ;
			}
		}else if (id == R.id.visit_continue){ //使用游客登录

			//	直接登录
			KnLog.log("游客直接登录++");
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			if(	null == DeviceUtil.getDeviceId()){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_59).toString());
				return ;
			}
			KnLog.log("游客登录开始");
			LoadingDialog.show(m_activity, "请求中...", true);
			HttpService.visitorReg(m_activity,handler);

		}


	}


	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
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
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
				case ResultCode.VISITOR_LOGIN_SUCCESS:
					KnLog.log("游客登录成功");
					if(msg.obj!=null){
						KnLog.log("游客登录成功结果:"+msg.obj.toString());
						if(GameSDK.getInstance().getmLoginListener()!=null){
							KnLog.log("getmLoginListener().onSuccess++");
							GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
							if(m_activity!=null){
								m_activity.finish();
								m_activity = null ;
							}
						}else{

						}
					}
					break;
				case ResultCode.VISITOR_LOGIN_FAIL:
					KnLog.log("游客登录失败");
					if(msg.obj!=null)
					{
						KnLog.log("游客登录失败结果:"+msg.obj.toString());
						if(GameSDK.getInstance().getmLoginListener()!=null){
							GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
							Util.ShowTips(m_activity,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
							m_activity.finish();
						}else{

						}
					}
					break;
				default:
					break;
			}
		}
	};
	
}
