package com.game.sdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;

import java.util.ArrayList;

/**
 * 免密码开始登录
 */
public class AutoLoginActivity extends Activity implements View.OnClickListener {

	private Button m_btn = null;
	private Activity m_activity = null;
	private String m_passwords = null;
	private TextView m_uppw, m_account_bt;
	private ArrayList<String> arrlist = new ArrayList<String>();//存放下拉listView中的账号；
	private PopupAdapter adapter;
	private ImageButton autoLogin_drop;
	private PopupWindow pop;
	private boolean isShow = false;
	private ListView etLv = null;
	private EditText account_et, m_aps;
	private String etname;
	private String etpassword;
	private Boolean iscb = false;
	private String lastTime; //退出日期
	private String todayTime;//当前日期
	private String lastName; //最后退出名字
	private String Spname; //存入sp中的key名
	private String xname, xpassword; //忘记密码页面传递的参数


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mc_auto_login_manager);
		m_activity = this;
		initview();
		initUser();
		initlistener();
	}


	private void initlistener() {
		//下拉框，记录多个账号
		autoLogin_drop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (arrlist.size() > 0) {
					if (pop == null) {
						if (adapter == null) {

							View view = View.inflate(AutoLoginActivity.this, R.layout.mc_popup_layout, null);
							etLv = view.findViewById(R.id.mc_pop_ls);
							etLv.setBackgroundColor(Color.parseColor("#BCBCBC"));
							pop = new PopupWindow(view, account_et.getWidth(),
									LayoutParams.WRAP_CONTENT, true);
							pop.setOutsideTouchable(true);
							pop.setFocusable(true);
							pop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BCBCBC")));
							pop.update();
							pop.showAsDropDown(account_et, 0, 5);
							adapter = new PopupAdapter(m_activity);
							etLv.setAdapter(adapter);
							isShow = true;
						}
					} else if (isShow) {
						pop.dismiss();
						isShow = false;
					} else if (!isShow) {
						pop.showAsDropDown(account_et, 0, 5);
						isShow = true;
					}
				}
			}
		});
	}

	//获取输入框信息
	private void EtUser() {
		etname = account_et.getText().toString().trim();
		etpassword = m_aps.getText().toString().trim();
	}

	private void TomastUser() {
		Util.ShowTips(m_activity, etname + ",已登录成功");
	}


	//获取是否提醒信息
	private void remind(String name) {
		Spname = name;
		lastTime = String.valueOf(TodayTimeUtils.LastTime(m_activity));
		lastName = String.valueOf(TodayTimeUtils.LastName(m_activity, name));
		todayTime = TodayTimeUtils.TodayTime();
		KnLog.log("==========lastTime========" + lastName + "  ============lastName=" + lastName);
	}

	//退出保存提醒信息
	private void exitsave(String spname, String extname) {
		TodayTimeUtils.saveExitTime(m_activity);
		TodayTimeUtils.saveExitName(m_activity, spname, extname);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.login_game_bt) { //登录
			//直接登录
			if (!Util.isNetWorkAvailable(getApplicationContext())) {
				Util.ShowTips(getApplicationContext(), getResources().getString(R.string.mc_tips_34));
				return;
			}

			//判断用户名与密码输入格式
			checkAccountBindParams(m_activity);
			EtUser();
			//TODO 账号登录
			initLogin(etname, etpassword);

		} else if (id == R.id.mc_up_password) { //忘记密码
			GameSDK.getInstance().Update_password(AutoLoginActivity.this, true); //修改密码
			m_activity.finish();
		} else if (id == R.id.mc_new_account_bt) { //账号注册

			Intent inten = new Intent(m_activity, FastLoginActivity.class);
			startActivity(inten);
			if (m_activity != null) {
				m_activity.finish();
				m_activity = null;
			}
		} else if (id == R.id.auto_account_et) { //输入账号
			//TODO
			if (pop != null) {
				pop.dismiss();
			}

		}

	}


	//判断是否是手机号或账号
	private void checkAccountBindParams(Activity context) {

		final String username = account_et.getText().toString().trim(); //账号或手机号

		if (ismobile(context, username)) return;

		if (!Util.NameLength(username)) {
			Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_4));
			return;
		}

		if (!Util.isAccordName(username)) {
			Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_3));
			return;
		}

		KnLog.log("判断是否是手机=" + ismobile(context, username));

	}


	private boolean ismobile(Activity context, String username) {
		if (!Util.isMobileNO(username)) { //如果不是手机号
			//Util.ShowTips(m_activity, getResources().getString(R.string.tips_57)); //如果不是手机号
			//手机号或者账号不能为空
			if (!Util.isName(context, username)) {
				return true;
			}
		}
		return false;
	}


	//初始化view
	private void initview() {
		m_btn = (Button) findViewById(R.id.login_game_bt);
		autoLogin_drop = (ImageButton) findViewById(R.id.drop_down);//
		account_et = (EditText) findViewById(R.id.auto_account_et); //输入的账号
		m_aps = (EditText) findViewById(R.id.mc_auto_password); //输入密码
		m_uppw = (TextView) findViewById(R.id.mc_up_password); // 忘记密码
		m_account_bt = (TextView) findViewById(R.id.mc_new_account_bt);//账号注册
		account_et.setOnClickListener(this);
		m_account_bt.setOnClickListener(this);
		m_uppw.setOnClickListener(this);
		m_btn.setOnClickListener(this);
	}


	//读取数据库账号名
	private void initUser() {
		String usernames[] = DBHelper.getInstance().findAllUserName();
		String username = "";
		for (int i = 0; i < usernames.length; i++) {
			arrlist.add(usernames[i]);
		}
		if (usernames != null && usernames.length > 0) {
			username = usernames[0];
			m_passwords = DBHelper.getInstance().findPwdByUsername(username);
			account_et.setText(username); //显示账号
			m_aps.setText(m_passwords);//显示密码
		}

	}

	@Override
	public void onBackPressed() { //禁止返回键

	}


	//用户登录
	private void initLogin(final String muserName, final String mpassWord) {

		HttpService.doLogin(muserName, mpassWord, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code) {
					case ResultCode.SUCCESS: //成功
						remind(etname);
						KnLog.log("=====sdk已经登录成功======" + response);
						//修改完密码后跳转，就不需要判断是否绑定手机
						if (xname != null && xpassword != null) {
							KnLog.log("=====忘记密码跳转======name=" + xname + " ps=" + xpassword);
							m_activity.finish();
							m_activity = null;
						} else {
							//TODO 查询账号是否绑定手机号
							initQueryBind(muserName);
						}

						break;

					default:
						Util.ShowTips(m_activity, JSON.parseObject(response).getString("reason"));

						break;
				}

			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				Util.ShowTips(m_activity, "请求服务器失败!" + msg);
			}
		});
	}


	//查询账号是否绑定手机号
	private void initQueryBind(String userName) {

		HttpService.queryBindAccont(userName, new ISuccess() {
			@Override
			public void onSuccess(String response) {
				final int code = JSON.parseObject(response).getIntValue("code");
				switch (code) {
					case ResultCode.SUCCESS: //成功
						TomastUser();
						if (null == m_activity) {

						} else {
							m_activity.finish();
							m_activity = null;
						}
						break;
					default:
						//提示绑定手机
						initViewdialog();
						break;
				}

			}
		}, new IError() {
			@Override
			public void onError(int code, String msg) {
				Util.ShowTips(m_activity, "服务器响应失败了！");

			}
		});


	}


	//绑定手机号提示dialog
	private void initViewdialog() {

		if (lastTime.equals(todayTime) && lastName.equals(etname)) { //如果两个时间段相等
			KnLog.log("今天不提醒,今天日期" + todayTime + " 最后保存日期:" + lastTime + " 现在登录的账号:" + etname + " 最后保存的账号:" + lastName);
			TomastUser();
			m_activity.finish();
			m_activity = null;
		} else {
			LayoutInflater inflater = LayoutInflater.from(m_activity);
			View v = inflater.inflate(R.layout.mc_bind_mobile_dialog_ts, null); //绑定手机
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
			final AlertDialog dia = new AlertDialog.Builder(m_activity).create();
			Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
			Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
			TextView ts = (TextView) v.findViewById(R.id.ts);
			ImageView close = (ImageView) v.findViewById(R.id.mc_da_lose);//关闭
			CheckBox mcheckBox = (CheckBox) v.findViewById(R.id.mc_tx);//选择今日不提醒

			dia.show();
			dia.setContentView(v);
			cont.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DBHelper.getInstance().insertOrUpdateUser(etname, etpassword);
					Intent intent = new Intent(m_activity, BindCellActivity.class);
					intent.putExtra("userName", etname);
					startActivity(intent);
					if (null == m_activity) {
					} else {
						dia.dismiss();
						m_activity.finish();
						m_activity = null;
					}
				}
			});

			//稍后绑定
			bind.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (iscb) {
						//保存勾选后的日期
						exitsave(Spname, etname);
						TomastUser();
						dia.dismiss();
						m_activity.finish();
						m_activity = null;
						KnLog.log("勾选了今天不提醒,今天日期" + todayTime + " 最后保存日期:" + lastTime + " 现在登录的账号:" + etname + " 最后保存的账号:" + lastName);
					} else {
						TomastUser();
						dia.dismiss();
						m_activity.finish();
						m_activity = null;
					}
				}
			});


			//关闭AlertDialog
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dia.dismiss();
					m_activity.finish();
					m_activity = null;
				}
			});

			//选择提醒
			mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						iscb = true;
					} else {
						iscb = false;
					}
				}
			});
		}
	}

	//可编辑下拉框
	class PopupAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private Context context;

		public PopupAdapter() {
			// TODO Auto-generated constructor stub
		}

		public PopupAdapter(Context context) {

			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return arrlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder = null;
			final String names = arrlist.get(position);
			if (convertView == null) {
				layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.mc_list_item, null);
				holder = new Holder();
				holder.tv = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (holder != null) {
				convertView.setId(position);
				holder.tv.setText(names);
				holder.tv.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						boolean touch = false; //是否点击下拉的选项；
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							pop.dismiss();
							isShow = false;
							account_et.setText(names);
							String poppassword = DBHelper.getInstance().findPwdByUsername(names);
							m_aps.setText(poppassword);
							touch = true;
						} else {
							touch = false;
						}
						return touch;
					}

				});
			}
			return convertView;
		}
	}

	class Holder {
		TextView tv;

		void setId(int position) {
			tv.setId(position);

		}
	}



}
