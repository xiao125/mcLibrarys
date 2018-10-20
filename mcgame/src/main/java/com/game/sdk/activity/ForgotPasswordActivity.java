package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
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

/**
 * 忘记密码选择找回密码方式
 * Created
 */

public class ForgotPasswordActivity extends Activity {

    private Activity activity;
    private EditText m_phone;
    private ImageView m_close;
    private Button m_zh_qd,m_phone_ks_code;
    private CheckBox m_cb_phone;
    private TextView mphone;
    private FrameLayout m_frameLayout;
    private Boolean iscb=true;
    private Timer m_timer = null ;
    private int   m_time  = 60 ;
    private Message m_msg = null ;
    private String newSdk="1";
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this ;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mc_forgot_password_layout);
        initView();
        initLinerter();
    }


    private void initLinerter() {

        m_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this,AutoLoginActivity.class);
                 activity.startActivity(intent);
                activity.finish();
                activity=null;
                if (activity!=null){
                    activity.finish();
                    activity=null;
                }
            }
        });

        //手机号找回
        m_cb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    iscb=true;
                    mphone.setTextColor(getResources().getColor(R.color.mc_Kn_Username));
                  //  memail.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                   // m_cb_email.setChecked(false);
                    m_cb_phone.setChecked(true);
                    m_zh_qd.setEnabled(true);
                    m_zh_qd.setBackgroundColor(getResources().getColor(R.color.mc_Kn_Username));
                }else {
                    mphone.setTextColor(getResources().getColor(R.color.mc_kn_selecte_log));
                    m_zh_qd.setEnabled(false);
                    m_zh_qd.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                }
            }
        });

        //邮箱找回
      /*  m_cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){

                    iscb=false;
                    memail.setTextColor(getResources().getColor(R.color.Kn_Username));
                    mphone.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                    m_cb_phone.setChecked(false);
                    m_cb_email.setChecked(true);
                }
            }
        });*/

        //确定
        m_zh_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始验证是否绑定手机号
                String phone= m_phone.getText().toString().trim();
                //TODO 查询账号是否绑定手机号
                initQueryBind(phone);
            }
        });

        //查询是否注册了该账号
        m_phone_ks_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountBindParams(activity,m_phone);
            }
        });

    }


    //判断是否是手机号或账号
    private void checkAccountBindParams(Activity context, EditText mUsername) {

        String username =  m_phone.getText().toString().trim(); //账号或手机号
        if (ismobile(activity, username)) return;
        KnLog.log("判断是否是手机="+ismobile(activity, username));
        if (!Util.NameLength(username)){
            Util.ShowTips(context,context.getResources().getString(R.string.mc_tips_4) );
            return ;
        }
        if (!Util.isAccordName(username)){
            Util.ShowTips(context, context.getResources().getString(R.string.mc_tips_3));
            return ;
        }
        KnLog.log("判断是否是手机="+ismobile(activity, username));
        LoadingDialog.show(activity, "正在验证手机账号中...", true);
        //TODO 查询账号是否存在
        initGetUserName(username);
    }


    private boolean ismobile(Activity context, String username) {
        if(!Util.isMobileNO(username)) { //如果不是手机号
            //手机号或者账号不能为空
           if (!Util.isName(context,username)){
               return true;
           }
        }
        return false;
    }

    private void initView() {
        m_close = (ImageView) findViewById(R.id.select_forgot_close);
        m_phone = (EditText) findViewById(R.id.phone_ks_va); //手机号或账号
        m_phone_ks_code = (Button) findViewById(R.id.phone_ks_code_va); //验证手机号或账号是否存在
        m_zh_qd= (Button) findViewById(R.id.zh_qd);
        m_cb_phone= (CheckBox) findViewById(R.id.zh_phone);
     // m_cb_email= (CheckBox) findViewById(R.id.zh_email);
        mphone = (TextView) findViewById(R.id.tv);
      //  memail = (TextView) findViewById(R.id.tv_1);
        m_frameLayout= (FrameLayout) findViewById(R.id.zh_view); //显示找回密码view
    }


    //查询账号是否绑定手机号
    private void initQueryBind(String userName){

        HttpService.queryBindAccont(userName, new ISuccess() {
            @Override
            public void onSuccess(String response) {
                final int code = JSON.parseObject(response).getIntValue("code");
                switch (code) {
                    case ResultCode.SUCCESS: //成功
                        if (GameSDK.getInstance().getmLoginListener() != null) {
                            GameSDK.getInstance().getmLoginListener().onSuccess(response);
                            KnLog.log("sdk账号绑定了手机"+response);
                            String mobile = JSON.parseObject(response).getString("mobile"); //服务器返回的手机号
                            //手机验证码更改密码
                            Intent intent1 = new Intent(ForgotPasswordActivity.this,PasswordUpdateActivity.class);
                            intent1.putExtra("phone",mobile);
                            startActivity(intent1);
                            activity.finish();
                            activity=null;
                        }
                        break;
                    default:
                        Util.ShowTips(activity,JSON.parseObject(response).getString("reason"));
                        break;
                }

            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {
                Util.ShowTips(activity,"服务器响应失败了！"+msg);
            }
        });
    }


    //验证帐号是否存在
    private void initGetUserName(String username){

        HttpService.getUsername(username, new ISuccess() {
            @Override
            public void onSuccess(String response) {
                KnLog.log("查询账号是否存在接口========"+JSON.parseObject(response));
                final int code = JSON.parseObject(response).getIntValue("code");
                LoadingDialog.dismiss();
                switch (code){
                    case ResultCode.SUCCESS: //成功
                        //显示view
                        m_frameLayout.setVisibility(View.VISIBLE);
                        break;
                    case ResultCode.GET_USER:
                        Util.ShowTips(activity,JSON.parseObject(response).getString("reason"));
                        break;
                    default: //失败
                        Util.ShowTips(activity,JSON.parseObject(response).getString("reason"));
                        break;
                }
            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {
                LoadingDialog.dismiss();
                Util.ShowTips(activity,"服务器响应失败了！"+msg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        KnLog.log("====屏蔽返回键1");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;//不执行父类点击事件
        }
        KnLog.log("====屏蔽返回键1");
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

}
