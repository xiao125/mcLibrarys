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
import android.widget.EditText;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
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
 * 手机号绑定账号
 */
public class BindCellActivity  extends Activity implements OnClickListener,ITimerListener{
	
	private Activity m_activity = null ;
	private EditText m_cellNum_et = null ;
	private EditText m_security_code__et = null ;
	private ImageView m_phone_ks_close,m_select_login_close,m_get_security_back;
	private String  m_userNames = null ;
	private Button   m_get_security_codeBtn,m_get_security_submit;
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
    private String newSdk="1";
	//倒计时秒数
	private int mCount=60;
	private Timer mTimer =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_activity = this ;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mc_bind_cell);
		initView();
		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");
	}

    //验证是否是手机
	private boolean isphone(String cell_num) {
		if (!Util.isUserPhone(m_activity,cell_num)){
			return true;
		}
		return false;
	}

	private void initView() {
		m_cellNum_et = (EditText)findViewById(R.id.cellnumber__et); //手机号
		m_security_code__et = (EditText)findViewById(R.id.security_code__et); //请输入验证码
		m_phone_ks_close = (ImageView) findViewById(R.id.phone_ks_close); //清除验证码
		m_select_login_close = (ImageView) findViewById(R.id.select_login_close); //关闭
		m_get_security_codeBtn= (Button) findViewById(R.id.get_security_code); //验证码
		m_get_security_back= (ImageView) findViewById(R.id.get_security_back); //返回
		m_get_security_submit = (Button) findViewById(R.id.get_security_submit); //确定
		m_get_security_back.setOnClickListener(this);
		m_get_security_codeBtn.setOnClickListener(this);
		m_get_security_submit.setOnClickListener(this);
		m_phone_ks_close.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
	}


	@Override
	public void onClick(View v ) {
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.get_security_back){ //返回
			if(null==m_activity){
			}else{
				KnLog.log("=======返回关闭页面");
				TimerClos();
				m_activity.finish();
				m_activity = null ;
			}
		}else if(id==R.id.get_security_code){ //验证码
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			if (isphone(cell_num)) return;
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			if(null==m_activity){

			}else{
				LoadingDialog.show(m_activity, "获取验证码中...", true);
				//TODO 发送手机验证码
				initSecCode(cell_num);
			}

		}else if(id==R.id.get_security_submit){ //获取到验证码，下一步
			// 成功
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			String security_code = m_security_code__et.getText().toString().trim(); //验证码
			if (isphone(cell_num)) return;
			if (!Util.isUserCode(m_activity,security_code)){
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			if(null==m_activity){

			}else{
				LoadingDialog.show(m_activity, "绑定中...", true);
				//TODO 绑定手机
				initBindMobile(cell_num,security_code,m_userNames);
			}

		}else if (id==R.id.phone_ks_close){ //清除验证码

			m_security_code__et.setText("");

		}else if (id == R.id.select_login_close){ //关闭

			if(null==m_activity){

			}else{

				KnLog.log("=======关闭页面");
				TimerClos();
				m_activity.finish();
				m_activity = null ;

			}
		}
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
						Util.ShowTips(m_activity,JSON.parseObject(response).getString("reason") );
						break;
				}
			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				LoadingDialog.dismiss();
				Util.ShowTips(m_activity,"服务器响应失败了！"+msg);
			}
		});

	}



	//绑定手机号
	private void initBindMobile(String phone,String phonecode,String name){


		HttpService.bindMobile(phone, phonecode, name, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				LoadingDialog.dismiss();
				KnLog.log("绑定手机验接口========"+ JSON.parseObject(response));
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code){
					case ResultCode.SUCCESS: //成功
						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onSuccess(response);
							Util.ShowTips(m_activity,JSON.parseObject(response).getString("reason"));
							if (m_activity!=null){
								TimerClos();
								m_activity.finish();
								m_activity = null ;
							}
						}
						break;
					default: //失败
						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onFail(response);
							Util.ShowTips(m_activity,JSON.parseObject(response).getString("reason"));
						}
						break;
				}
			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				LoadingDialog.dismiss();
				Util.ShowTips(m_activity,"服务器响应失败了！"+msg);
			}
		});

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

				if (m_get_security_codeBtn != null) {

					m_get_security_codeBtn.setEnabled(false);
					mCount--;
					m_get_security_codeBtn.setText(String.valueOf(mCount) + "秒");
					m_get_security_codeBtn.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));

					if (mCount <= 0) {
						m_get_security_codeBtn.setText("重新发送");
						m_get_security_codeBtn.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
						m_get_security_codeBtn.setEnabled(true);
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
