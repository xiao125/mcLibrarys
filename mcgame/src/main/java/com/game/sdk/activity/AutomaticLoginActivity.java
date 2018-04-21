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
 * Created by Administrator on 2018/3/13 0013.
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

        if(GameSDK.getInstance().ismScreenSensor()){

        }else{
            setRequestedOrientation(GameSDK.getInstance().getmOrientation());
        }

        setContentView(R.layout.mc_automaticlogin_layout);

        m_activity = this ;

        LoadingDialog.show(m_activity,"正在登录中...",false); //开启提示自动登录中

        AutLogin();


        lastTime =String.valueOf(TodayTimeUtils.LastTime(m_activity));
        lastName = String.valueOf(TodayTimeUtils.LastName(m_activity,username));
        todayTime = TodayTimeUtils.TodayTime();
        KnLog.log("==========lastTime========"+lastName+"  ============lastName="+lastName);

      /*  getpreferences();
        getTimesnight();
        getTime();*/

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







    //preferences 保存第一次登录与今日提醒
    private void getpreferences(){
        //创建sp存储
        preferences = getSharedPreferences("LastLoginTime1", MODE_PRIVATE);
        editor= preferences.edit();

    }

    //获得最后一次保存的日期
    public  void getTimesnight(){
       lastTime = preferences.getString("LoginTime", "2018-03-10");
        lastName = preferences.getString("Loginname", "mc");
        KnLog.log(" ===========lastTime="+lastTime+" lastName="+lastName);



    }


    //获取当前时间
    private String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        todayTime = df.format(new Date());// 获取当前的日期

        return  todayTime;
    }

    //保存勾选后的日期
    private void saveExitTime(String extiLoginTime) {


        editor.putString("LoginTime", extiLoginTime);
        //这里用apply()而没有用commit()是因为apply()是异步处理提交，不需要返回结果，而我也没有后续操作
        //而commit()是同步的，效率相对较低
        //apply()提交的数据会覆盖之前的,这个需求正是我们需要的结果
        editor.apply();
        KnLog.log("保存退出时间");
    }
    //保存勾选后的日期
    private void saveExiName(String LastName) {
        editor.putString("Loginname",LastName);

        //这里用apply()而没有用commit()是因为apply()是异步处理提交，不需要返回结果，而我也没有后续操作
        //而commit()是同步的，效率相对较低
        //apply()提交的数据会覆盖之前的,这个需求正是我们需要的结果
        editor.commit();
        KnLog.log("保存退出账号");
    }



    //用户登录
    private void initLogin(final String muserName,final String mpassWord){

        HttpService.doLogin(muserName, mpassWord, new ISuccess() {
            @Override
            public void onSuccess(String response) {

                LoadingDialog.dismiss();

                switch (Integer.valueOf(response)) {
                    case ResultCode.SUCCESS: //成功

                        //登录成功之后就保存账号密码
                        DBHelper.getInstance().insertOrUpdateUser( muserName, mpassWord );
                        Util.ShowTips(m_activity,muserName+"登录成功！");

                        //TODO 查询账号是否绑定手机号
                        initQueryBind(muserName);

                        break;

                    case ResultCode.FAILS: //帐号或密码输入错误,请重新输入

                        Util.ShowTips(m_activity,"帐号或密码输入错误,请重新输入!");

                        m_activity.finish();
                        m_activity = null ;


                        break;
                    case ResultCode.NONEXISTENT: //账号不存在
                        Util.ShowTips(m_activity,"账号不存在!");

                        m_activity.finish();
                        m_activity = null ;

                        break;
                    default:

                        Util.ShowTips(m_activity,"数据错误了！");
                        m_activity.finish();
                        m_activity = null ;

                        break;
                }

            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {
                LoadingDialog.dismiss();
                Util.ShowTips(m_activity,"数据错误了！");
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
                    // TODO Auto-generated method stub

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

                            //保存勾选后的日期
                            TodayTimeUtils.saveExitTime(m_activity);
                            TodayTimeUtils.saveExitName(m_activity, username, username);

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
