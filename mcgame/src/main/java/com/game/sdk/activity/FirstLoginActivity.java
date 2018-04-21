package com.game.sdk.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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



/**
 * 已有账号登录
 */
public class FirstLoginActivity extends Activity implements OnClickListener {

	public static String m_userName;
	public static String m_pad;
	private EditText userNameEt;
	private EditText passWordEt; 
	private Activity activity ; 
	private static String   m_password ;
	private ImageView image_back;
	private String  m_userNames = null ;
	private TextView findPassword,register;
	private TextView update_Password,new_account_bt;
	private Button login_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
	
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_first_login_new);

		initView();

		initLinerter();


		
		Intent intent = getIntent();
		if(intent.hasExtra("userName")){
			m_userNames   = intent.getStringExtra("userName");
			userNameEt.setText(m_userNames);
		}
		if(intent.hasExtra("password")){
			m_pad   = intent.getStringExtra("password");
			passWordEt.setText(m_pad);
		}
			
	}

	private void initLinerter() {

		//返回
		image_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(null==activity){

				}else {

					Intent intent1 = new Intent(activity, SelecteLoginActivity.class);
					activity.startActivity(intent1);
					activity.finish();
					activity = null;
				}

			}
		});

		//全屏，进入输入用户名
		userNameEt.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				userNameEt.setCursorVisible(true);
			}
		} );

		//输入完毕，下一步，进入输入密码
		userNameEt.setOnEditorActionListener( new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					userNameEt.clearFocus();
					passWordEt.requestFocus();
					userNameEt.setCursorVisible(false);
				}
				return false;
			}
		} );

		//输入密码
		passWordEt.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				passWordEt.setCursorVisible(true);
			}
		} );

		passWordEt.setOnEditorActionListener( new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v , int actionId , KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					passWordEt.clearFocus();
					userNameEt.clearFocus();
					passWordEt.requestFocus();
					passWordEt.setCursorVisible(false);
					Util.hideEditTextWindow(activity, passWordEt);
					Util.hideEditTextWindow(activity, userNameEt);
				}
				return false;
			}
		} );


	}

	private void initView() {

		userNameEt = (EditText) findViewById(R.id.account__et); //用户名或者手机号
		passWordEt = (EditText) findViewById(R.id.password__et); //密码
		image_back=(ImageView) findViewById(R.id.first_login_back); //返回
		update_Password= (TextView) findViewById(R.id.update_Password); //忘记密码
		login_bt = (Button) findViewById(R.id.login_bt); //登录
		new_account_bt = (TextView) findViewById(R.id.new_account_bt);

		login_bt.setOnClickListener(this);
		new_account_bt.setOnClickListener(this);
		update_Password.setOnClickListener(this);


	}

	@Override
	protected void onResume() {
		super.onResume();
		// 0为横屏 ， 1为竖屏
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		if (id == R.id.login_bt) { //登录
			KnLog.log("login");

			Util.hideEditTextWindow(this, passWordEt);
			Util.hideEditTextWindow(this, userNameEt);

			checkAccountBindParams(activity, userNameEt, passWordEt);


			/*Util.hideEditTextWindow(this, passWordEt);
			checkLoginParams(FirstLoginActivity.this, userNameEt, passWordEt);*/
			KnLog.log("login End");
		} 
		else if (id == R.id.new_account_bt) { //快速注册
			// 隐藏虚拟键盘
			KnLog.log("new_account_bt");
			Util.hideEditTextWindow(this, passWordEt);
			//GameSDK.getInstance().register(FirstLoginActivity.this , true); //跳转到注册界面

			GameSDK.getInstance().KsRegister(FirstLoginActivity.this,true); //跳转到快速注册界面

		}else if (id == R.id.update_Password){ //忘记密码


				GameSDK.getInstance().Update_password(FirstLoginActivity.this,true); //修改密码
				activity.finish();



		} else{
			KnLog.log("forget_password null ");
			return ;
		}
		
	}


	//判断账号密码输入格式
	private void checkAccountBindParams(Activity context, EditText mUsername, EditText mPassword) {

		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();


		if(TextUtils.isEmpty(username)){
			Util.ShowTips(activity,getResources().getString(R.string.mc_tips_100));
			return ;
		}

		//KnLog.log("判断是否手机号："+ismobile(context, username));

		//不是手机号
		if (ismobile(context, username)) return;


		if(!Util.isName(context,username)){

			return;
		}

		if (!Util.isUserPassword(context,password)){
			return;
		}

		//LoadingDialog.show(context, "绑定中...",true);

		//查询账号是否存在
		//HttpService.getUsername(activity.getApplicationContext(), handler,username);


		//直接登录
		m_userName = username ;
		m_password = password;
		KnLog.log("输入账号登录username="+username+" password="+password);

		LoadingDialog.show(activity, "登录中...",true);

		//TODO 账号登录
		initLogin(username,password);




	}


	private boolean ismobile(Activity context, String username) {
		if(!Util.isMobileNO(username)) { //如果不是手机号
			//Util.ShowTips(m_activity, getResources().getString(R.string.tips_57)); //如果不是手机号

			if(!Util.isName(context,username)){

				return true;
			}

		}
		return false;
	}




	//用户登录
	private void initLogin(final String muserName,final String mpassWord){

		HttpService.doLogin(muserName, mpassWord, new ISuccess() {
			@Override
			public void onSuccess(String response) {

				switch (Integer.valueOf(response)) {
					case ResultCode.SUCCESS: //成功

						//登录成功之后就保存账号密码
						Util.ShowTips(activity,"登录成功！");
						activity.finish();
						activity=null;


						break;

					case ResultCode.FAILS: //帐号或密码输入错误,请重新输入
						Util.ShowTips(activity,"帐号或密码输入错误,请重新输入!");

						break;
					case ResultCode.NONEXISTENT: //账号不存在
						//Util.ShowTips(activity,"账号不存在!");

						//提示绑定手机弹窗
						LayoutInflater inflater = LayoutInflater.from(activity);
						View v = inflater.inflate(R.layout.mc_bind_mobile_dialog, null);
						LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
						final AlertDialog dia=new AlertDialog.Builder(activity).create();
						Button bind=(Button) v.findViewById(R.id.visit_bind_account); //立即注册
						Button cont=(Button) v.findViewById(R.id.visit_continue); //重新输入]
						TextView name = (TextView) v.findViewById(R.id.username); //提示

						String user= name.getText().toString(); //占位符
						String et=userNameEt.getText().toString().trim(); //账号
						name.setText(user.replace("1",et)); //替换

						// bind.setText("绑定手机");
						dia.show();
						dia.setContentView(v);
						bind.setOnClickListener(new OnClickListener() { //立即注册

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub


								Intent intent1=new Intent(activity, FastLoginActivity.class);
								startActivity(intent1);
								dia.dismiss();

								if(null==activity){

								}else{
									activity.finish();
									activity = null ;
								}

							}
						});
						cont.setOnClickListener(new OnClickListener() { //重新输入

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dia.dismiss();

								if(null==activity){

								}else{


									dia.dismiss();


								}


							}
						});


						break;
					default:

						Util.ShowTips(activity,"数据错误了！");
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



}
