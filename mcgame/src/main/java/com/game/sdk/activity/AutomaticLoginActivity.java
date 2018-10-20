package com.game.sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import java.text.SimpleDateFormat;
import java.util.Date;

/**自动登录
 *
 */

public class AutomaticLoginActivity extends Activity {


    private Activity m_activity = null ;
    private static  String username =null;
    private static  String password =null;
    //声明一个SharedPreferences对象和一个Editor对象
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean iscb=false;
    private String lastTime; //退出日期
    private String todayTime;//当前日期
    private String lastName; //最后退出名字


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mc_automaticlogin_layout);
        m_activity = this ;
        LoadingDialog.show(m_activity,"正在登录中...",false); //开启提示自动登录中
        AutLogin();
        todayPs();
    }


    //记录是否需要提醒绑定账号
    private void todayPs(){
        lastTime =String.valueOf(TodayTimeUtils.LastTime(m_activity));
        lastName = String.valueOf(TodayTimeUtils.LastName(m_activity,username));
        todayTime = TodayTimeUtils.TodayTime();
        KnLog.log("==========lastTime========"+lastName+"  ============lastName="+lastName);
    }

    //弹窗提示操作后记录
    private void ExitTime(){
        //保存勾选后的日期
        TodayTimeUtils.saveExitTime(m_activity);
        TodayTimeUtils.saveExitName(m_activity, username, username);
    }

    //自动登录
    private void AutLogin(){
        String usernames[] = DBHelper.getInstance().findAllUserName();
        if( usernames != null && usernames.length >0 ){
            username = usernames[0]; //获得到用户名
            password = DBHelper.getInstance().findPwdByUsername(username); //密码
            //TODO 账号登录
            initLogin(username,password);
        }
    }

    //用户登录
    private void initLogin(final String muserName,final String mpassWord){

        HttpService.doLogin(muserName, mpassWord, new ISuccess() {
            @Override
            public void onSuccess(String response) {
                LoadingDialog.dismiss();
                final int code = JSON.parseObject(response).getIntValue("code");
                switch (code) {
                    case ResultCode.SUCCESS: //成功
                        //登录成功之后就保存账号密码
                        DBHelper.getInstance().insertOrUpdateUser( muserName, mpassWord );
                        Util.ShowTips(m_activity,muserName+"登录成功！");
                        //TODO 查询账号是否绑定手机号
                        initQueryBind(muserName);
                        break;
                    default:
                        Util.ShowTips(m_activity,JSON.parseObject(response).getString("reason"));
                        m_activity.finish();
                        m_activity = null ;
                        break;
                }
            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {
                LoadingDialog.dismiss();
                Util.ShowTips(m_activity,"请求服务器失败！"+msg);
            }
        });
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
                            if(null==m_activity){
                            }else{
                                m_activity.finish();
                                m_activity = null ;
                            }
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

            }
        });


    }

    //绑定手机号提示dialog
    private void initViewdialog() {
        if (lastTime.equals(todayTime) && lastName.equals(username)) { //如果两个时间段相等
            KnLog.log("今天不提醒,今天日期" + todayTime + " 最后保存日期:" + lastTime + " 现在登录的账号:" + username + " 最后保存的账号:" + lastName);
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
                    DBHelper.getInstance().insertOrUpdateUser(username, password);
                    Intent intent = new Intent(m_activity, BindCellActivity.class);
                    intent.putExtra("userName", username);
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
                    if (null == m_activity) {
                    } else {
                        if (iscb) {
                            ExitTime();
                            dia.dismiss();
                            m_activity.finish();
                            m_activity = null;
                            KnLog.log("勾选了今天不提醒,今天日期" + todayTime + " 最后保存日期:" + lastTime + " 现在登录的账号:" + username + " 最后保存的账号:" + lastName);
                        } else {
                            dia.dismiss();
                            m_activity.finish();
                            m_activity = null;
                        }
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

}
