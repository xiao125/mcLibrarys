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
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机号绑定账号
 */
public class BindCellActivity  extends Activity implements OnClickListener {
	
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




    //验证是否是手机
	private boolean isphone(String cell_num) {

		if (!Util.isUserPhone(m_activity,cell_num)){
			return true;
		}

		return false;
	}

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
		
		m_activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_bind_cell);

		initView();

		
		//m_get_security_codeBtn = (TextView) findViewById(R.id.get_security_code);
		

		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");
		
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

		HttpService.getSecCode(BindCellActivity.this,phone, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				LoadingDialog.dismiss();

				KnLog.log("获取手机验证码接口========"+JSON.parseObject(response));
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code){
					case ResultCode.SUCCESS: //成功

						m_get_security_codeBtn.setClickable(false);
						m_timer = new Timer();
						m_time = 60 ;
						m_timer.schedule(new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								m_msg = new Message();
								if(0==m_time){
									m_msg.what = 10001;
									m_timer.cancel();
								}else{
									m_time -- ;
									m_msg.what = 10000;
								}
								m_handler.sendMessage(m_msg);
							}
						},1000,1000);


						break;

					default: //失败
						Util.ShowTips(BindCellActivity.this,Util.getJsonStringByName( response , "reason" ) );
						break;

				}





			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {

				LoadingDialog.dismiss();


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
				final String msg_content  = JSON.parseObject(response).getString("reason");

				switch (code){
					case ResultCode.SUCCESS: //成功

						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onSuccess(response);

							Util.ShowTips(m_activity,msg_content);

							if (m_activity!=null){
								m_activity.finish();
								m_activity = null ;
							}
						}


						break;

					default: //失败

						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onFail(response);
							Util.ShowTips(m_activity,msg_content);
						}

						break;
				}
			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				LoadingDialog.dismiss();

			}
		});




	}

	
	private Handler m_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(10001==msg.what){
				m_get_security_codeBtn.setClickable(true);
				m_get_security_codeBtn.setText(R.string.mc_tips_48);
			}
			else if(10000==msg.what){
				String text =+m_time+"秒";

				m_get_security_codeBtn.setText(text);
			}	
		}
		
	};
	
}
