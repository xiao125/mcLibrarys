package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
 *  游客直接绑定手机号，用户名就是手机
 * Created
 */

public class TourtistRegActivity extends Activity implements View.OnClickListener{

    private Activity activity;
    private ImageView m_close;
    private EditText m_etcode;
    private Button m_tortist_code,m_account_bind_bt;
    private String newSdk="1";
    private String phone;
    private String password;
    private Timer    m_timer = null ;
    private int      m_time  = 60 ;
    private Message  m_msg = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        activity = this;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (GameSDK.getInstance().ismScreenSensor()) {

        } else {
            setRequestedOrientation(GameSDK.getInstance().getmOrientation());
        }

        setContentView(R.layout.mc_tourist_reg);

        Intent intent = getIntent();
        phone   = intent.getStringExtra("phone");
        password = intent.getStringExtra("password");

        initView();






    }

    private void initView() {

        m_close = (ImageView) findViewById(R.id.account_bind_back);
        m_etcode= (EditText) findViewById(R.id.tourist_phone_code); //输入验证码
        m_tortist_code = (Button) findViewById(R.id.tortist_code); //发送验证码
        m_account_bind_bt= (Button) findViewById(R.id.account_bind_bt); //确定注册
        m_close.setOnClickListener(this);
        m_account_bind_bt.setOnClickListener(this);
        m_tortist_code.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.account_bind_back) {

            if (activity!=null){
                activity.finish();
                activity=null;
            }


        }else if (id == R.id.tortist_code){ //发送验证码

            if(null==activity){

            }else{

                KnLog.log("获取的手机号是="+phone);
                LoadingDialog.show(activity, "获取验证码中...", true);

                //TODO 发送手机验证码
                initSecCode(phone);


            }


        }else if(id==R.id.account_bind_bt){

            String phonecode= m_etcode.getText().toString().trim();
            if(TextUtils.isEmpty(phonecode)){
                Util.ShowTips(activity,"验证码不能为空");
                return ;
            }
            if(!Util.isNetWorkAvailable(getApplicationContext())){
                Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
                return ;
            }


            //游客绑定手机，用户名就为手机号

            KnLog.log("游客绑定手机，手机号="+phone+" 密码="+password);
            HttpService.visitorbindMobile(getApplicationContext(), handler, phone , phonecode,phone,password);


            //手机注册
           // HttpService.doMobileRegister(getApplicationContext(), handler, phone,phonecode, password);

        }




    }


    //发送手机验证码
    private void initSecCode(String phone){

        HttpService.getSecCode(phone, new ISuccess() {
            @Override
            public void onSuccess(String response) {
                LoadingDialog.dismiss();
            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {
                LoadingDialog.dismiss();


            }
        });



    }






    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            String msg_content = msg.obj.toString();
            int resultCode = msg.what;
            Intent intent = null;
            LoadingDialog.dismiss();
            switch (resultCode) {

                case ResultCode.VISITOR_BIND_MODILE_SUCCESS: //游客绑定手机号成功

                  //  Util.ShowTips(activity,msg_content);
				/* intent = new Intent(m_activity.getApplicationContext(), AutoLoginActivity.class);
				 intent.putExtra("userName",m_userNames);*/
                    Util.ShowTips(activity,"升级账号成功！");

                    if (activity!=null){
                        activity.finish();
                        activity = null ;
                    }


                    break;

                case ResultCode.VISITOR_BIND_MODILE_FAIL://游客绑定手机号失败

                    Util.ShowTips(activity,"绑定失败："+msg_content);

                    break;


                case ResultCode.SECURITY_SUCCESS: //验证码获取成功

                    KnLog.log("收到验证码了");

                    m_tortist_code.setClickable(false);
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
                case ResultCode.SECURITY_FAIL:
                    Util.ShowTips(activity,msg_content);
                    break;
                default:
                    Util.ShowTips(activity,msg_content);
                    break;
            }
        }
    };

    private Handler m_handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(10001==msg.what){
                m_tortist_code.setClickable(true);
                m_tortist_code.setText(R.string.mc_tips_48);
            }
            else if(10000==msg.what){
                m_tortist_code.setBackgroundColor(getResources().getColor(R.color.mc_kn_text));
                String text =String.valueOf(m_time)+"秒";
                m_tortist_code.setText(text);
            }
        }

    };


}
