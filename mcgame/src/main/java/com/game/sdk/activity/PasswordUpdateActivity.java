package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.LogUtil;
import com.game.sdk.util.Util;
import com.game.sdk.util.timer.BaseTimerTask;
import com.game.sdk.util.timer.ITimerListener;
import com.game.sdkproxy.R;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;

import java.util.Timer;
import java.util.TimerTask;
/**
 * 通过手机验证码重新修改密码
 */
public class PasswordUpdateActivity extends Activity implements OnClickListener,ITimerListener {
	
	private Activity m_activity = null ;
	private EditText m_code = null ;
	private EditText password_new = null ;
	private EditText password_new_ng = null ;
	private ImageView m_password_update_back,m_select_login_close;
	private Button m_password_update_submit,m_update_code;
	private String   va_phone = null ;
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
	private String  qdPwd;
	private String newpassword; //输入的密码进行md5加密存入本地数据库
	//倒计时秒数
	private int mCount=60;
	private Timer mTimer =null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_activity = this ;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mc_password_update);
		initView();
		Intent intent = getIntent();
		va_phone = intent.getStringExtra("phone");
	}

	@Override
	public void onClick(View v ) {
		int id = v.getId();
		if(id== R.id.password_update_back){ //返回
			if (m_activity!=null){
				TimerClos();
				Intent intent1 = new Intent(PasswordUpdateActivity.this,ForgotPasswordActivity.class);
				startActivity(intent1);
				m_activity.finish();
			}
		}else if(id==R.id.password_update_submit){ //确定提交
			String cell_num = va_phone; //手机号
			String security_code = m_code.getText().toString().trim(); //验证码
			String newPwd  = password_new.getText().toString().trim(); //新密码
			qdPwd =password_new_ng.getText().toString().trim(); //确定密码
			if(!Util.isUserCode(m_activity,security_code)){
				return;
			}
			if(!Util.isUserPassword(m_activity,newPwd)){
				return;
			}
			if (!newPwd.equals(qdPwd)) {
				Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_65) );
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			newpassword = newPwd;
			KnLog.log("开始更改新密码请求");
			LoadingDialog.show(m_activity, "请求中...", true);
			//发送更改新密码请求
		    //HttpService.passwordNewSubmit(m_activity, handler, cell_num , security_code, newpassword,newSdk );
            //TODO 修改密码
			initPsSubmit(cell_num,security_code,newpassword);

		}else if(id==R.id.update_code){ //验证码
			String cell_Num = va_phone; //手机号
			if (!Util.isUserPhone(m_activity,cell_Num)){
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			if(null==m_activity){

			}else{
				LoadingDialog.show(m_activity, "获取验证码中...", true);
				//TODO 发送手机验证码
				initSecCode(cell_Num);
			}
		}else if (id==R.id.passwd_select_login_close){
			/*if (m_activity!=null){
				m_activity.finish();
				m_activity=null;
			}*/
		}
	}

	private void initView() {
		m_code = (EditText) findViewById(R.id.phone_code); //验证码
		password_new = (EditText) findViewById(R.id.password_new); //新密码
		password_new_ng = (EditText) findViewById(R.id.password_new_ng); //确认密码
		m_password_update_back = (ImageView) findViewById(R.id.password_update_back);//返回
		m_select_login_close= (ImageView) findViewById(R.id.passwd_select_login_close);//关闭
		m_password_update_submit= (Button) findViewById(R.id.password_update_submit); //确定
		m_update_code= (Button) findViewById(R.id.update_code); //验证码
		m_code.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_password_update_back.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_update_code.setOnClickListener(this);
		m_password_update_submit.setOnClickListener(this);
		//全屏，进入输入用户名
		m_code.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				m_code.setCursorVisible(true);
			}
		} );

		//输入完毕，下一步，进入输入密码
		m_code.setOnEditorActionListener( new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					m_code.clearFocus();
					password_new.requestFocus();
					m_code.setCursorVisible(false);
				}
				return false;
			}
		} );

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}*/
	}


	//发送手机验证码
	private void initSecCode(String phone){

		HttpService.getSecCode(phone, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				LoadingDialog.dismiss();
				KnLog.log("获取手机验证码接口========"+JSON.parseObject(response));
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code){
					case ResultCode.SUCCESS: //成功
						initTimer();
						break;
					default: //失败
						Util.ShowTips(PasswordUpdateActivity.this,JSON.parseObject(response).getString("reason") );
						break;
				}
			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				LoadingDialog.dismiss();
				Util.ShowTips(m_activity,"服务器响应失败了！code:"+code+"  msg:"+msg);
			}
		});

	}




	//修改密码
	private void initPsSubmit(String phone,String security_code, final String newpassword){
		HttpService.passwordNewSubmit(phone, security_code, newpassword, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				LoadingDialog.dismiss();
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code) {
					case ResultCode.SUCCESS: //成功
						KnLog.log("=========修改密码成功+======："+JSON.parseObject(response));
							String reason = JSON.parseObject(response).getString("reason");
							String user_name = JSON.parseObject(response).getString("user_name");
							Util.ShowTips(m_activity,reason);
							DBHelper.getInstance().insertOrUpdateUser(user_name ,newpassword ); //账号密码保存本地数据库
							Intent intent1=new Intent(PasswordUpdateActivity.this, AutoLoginActivity.class);
							intent1.putExtra("userName", user_name);
							intent1.putExtra("password",qdPwd);

						if(null==m_activity){

						}else{
							TimerClos();
							m_activity.startActivity(intent1);
							m_activity.finish();
							m_activity = null ;
						}
						break;
					default:
						Util.ShowTips(m_activity,JSON.parseObject(response).getString("reason"));
						break;
				}



			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				LoadingDialog.dismiss();
				Util.ShowTips(m_activity,"服务器相应失败! code:"+code+"  msg:"+msg);
			}
		});

	}


	@Override
	public void onBackPressed() { //禁止返回键

	}



	//开始倒计时
	private void initTimer(){

		mTimer = new Timer();
		final BaseTimerTask task = new BaseTimerTask(this);
		mTimer.schedule(task,1000,1000);

	}


	@Override
	public void onTimer() {

		LogUtil.log("开始了-------------");
		m_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (m_update_code != null) {

					m_update_code.setEnabled(false);
					mCount--;
					m_update_code.setText(String.valueOf(mCount) + "秒");
					m_update_code.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));

					if (mCount <= 0) {
						m_update_code.setText("重新发送");
						m_update_code.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
						m_update_code.setEnabled(true);
						mTimer.cancel();
						mCount=60;
					}
				}

			}

		});
		LogUtil.log("结束了-------------");

	}

	//关闭Timer
	private void TimerClos(){
		if (mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}

	}


}
