package com.mc.game;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import com.proxy.Constants;
import com.proxy.OpenSDK;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.User;
import com.proxy.listener.ExitListener;
import com.proxy.listener.InitListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.LogoutListener;
import com.proxy.listener.PayListener;
import com.proxy.util.LogUtil;
import com.proxy.util.Md5Util;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

public class TestActivity extends AppCompatActivity {

    public static int       WIDTH = 0 ;
    public static int       HEIGHT = 0 ;




    OpenSDK m_proxy = OpenSDK.getInstance();
    private String m_appKey = "tkvXAqJlLSewyd2h7WgjRZibaMFHIKBp";
    private String m_gameId = "fmsg";
    private String m_gameName = "fmsg";
    private int m_screenOrientation = 0;
    private static Activity m_activity  = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);

        m_activity = this ;

        //		酷牛SDK初始化游戏信息
        m_proxy.isSupportNew(true);
        m_proxy.doDebug(true);

        LogUtil.e("md5值："+ Md5Util.getMd5("tlwz_android"+"#"+"e7a4ea49b6c61f80c844bf2fbda4d8d3"));



        this.runOnUiThread(new Runnable() {


            public void run() {
                // TODO Auto-generated method stub
                m_proxy.init(m_activity, new InitListener() {

                    public void onSuccess(Object arg0) {
                        // TODO Auto-generated method stub
                        LogUtil.log("游戏初始化成功"+arg0);
                    }

                    public void onFail(Object arg0) {
                        // TODO Auto-generated method stub
                        LogUtil.log("游戏初始化失败"+arg0);
                    }
                });
            }
        });


        //  设置SDK登出监听
        m_proxy.setLogoutListener(new LogoutListener() {

            public void onSuccess(Object result) {


                if(result.equals(1)){
                    LogUtil.log("悬浮窗注销成功");
                    //游戏账号注销，返回到登录界面
                }else if(result.equals(2)) {
                    LogUtil.log("游戏内切换账号登出成功");
                    //游戏账号注销，返回到登录界面

                }


            }

            public void onFail(Object result) {

            }
        });

        //	设置SDK退出监听
        m_proxy.setExitListener(new ExitListener() {

            public void onConfirm() {

            }

            public void onCancel() {

            }
        });

        //	设置SDK登录监听
        m_proxy.setLogoinListener( new LoginListener() {


            public void onSuccess(User user) {
                // TODO Auto-generated method stub
                String open_id = user.getOpenId();
                String sId	   = user.getSid();
                LogUtil.log("登录成功");
//					KnUtil.ShowTips(m_activity, "登录成功");
            }


            public void onFail(String result) {
                // TODO Auto-generated method stub
                LogUtil.log("登录失败+result:"+result);

            }
        } );

        //	设置SDK支付监听
        m_proxy.setPayListener(new PayListener() {


            public void onSuccess(Object result) {
                LogUtil.log("setPayListener 支付回调成功"+result.toString());
            }


            public void onFail(Object result) {
            }
        });



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;
        HEIGHT = dm.heightPixels;

    }

    public void onLogin( View vt ){
        m_proxy.login(m_activity);
    }


    public void onEnterGame( View vt ){
        String  userId="1001";         	   				//游戏玩家ID
        String  serverId = "10021";      				//游戏玩家所在服ID
        String  userLv = "100";          				//游戏玩家等级Lv
        String  extraInfo = "expendinfo";      			//玩家信息拓展字段
        String  serverName = "神龙在手"; 					//玩家所在服区名称
        String roleName = "玩家角色名称";					//玩家角色名称
        String vipLevel ="10";          				//玩家VIP等级
        String factionName="虎头帮";     					//用户所在帮派名称
        int senceType = 1 ;           					//场景ID;//(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
        String role_id = "1001" ;       				//角色ID
        String  diamondLeft = "100";        			//玩家货币余额
        String  roleCTime = String.valueOf(System.currentTimeMillis()); //角色创建时间（需要从游戏服务端获取）

        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Constants.USER_ID, userId);			//游戏玩家ID
        data.put(Constants.SERVER_ID, serverId);		//游戏玩家所在的服务器ID
        data.put(Constants.USER_LEVEL, userLv);		//游戏玩家等级
        data.put(Constants.EXPEND_INFO, extraInfo);	//扩展字段
        data.put(Constants.SERVER_NAME, serverName);	//所在服务器名称data.put(Constants.ROLE_NAME,roleName);//角色名称
        data.put(Constants.VIP_LEVEL, vipLevel);		//VIP等级
        data.put(Constants.FACTION_NAME, factionName);//帮派名称
        data.put(Constants.SCENE_ID, senceType);		//场景ID
        data.put(Constants.ROLE_ID, userId);			//角色ID
        data.put(Constants.ROLE_CREATE_TIME,roleCTime);//角色创建时间
        data.put(Constants.BALANCE,diamondLeft);		//剩余货币data.put(Constants.IS_NEW_ROLE,senceType==2?true:false);	//是否是新角色
        data.put(Constants.USER_ACCOUT_TYPE,"1");		//玩家账号类型账号类型，0:未知用于来源1:游戏自身注册用户2:新浪微博用户3:QQ用户4:腾讯微博用户5:91用户(String)
        data.put(Constants.USER_SEX,"0");				//玩家性别，0:未知性别1:男性2:女性；(String)
        data.put(Constants.USER_AGE,"25");			//玩家年龄；(String)
        m_proxy.onEnterGame(data);
    }

    public void onPay( View vt ){
        final KnPayInfo payInfo = new KnPayInfo();
        payInfo.setProductName("传送阵");					//商品名字
        payInfo.setCoinName("黄金");						//货币名称	如:元宝
        payInfo.setCoinRate(10);						//游戏货币的比率	如:1元=10元宝 就传10
        payInfo.setPrice(600);							//商品价格   			分
        payInfo.setProductId("10001");					//商品Id,没有可以填null
        payInfo.setOrderNo("10003");					//支付接口//很特殊的接口就不做统一了
        payInfo.setExtraInfo("ExtraInfo++");
        m_activity.runOnUiThread(new Runnable() {


            public void run() {
                // TODO Auto-generated method stub
                m_proxy.pay(m_activity, payInfo);
            }
        });

    }

    public void onLogout( View vt ){
        m_proxy.logOut();
        finish();
        System.exit(0);
    }

    //游戏内切换账号接口
    public void onSwitch(View vt){
       m_proxy.switchAccount();




    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        m_proxy.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        m_proxy.onResume();
    }



    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        m_proxy.onRestart();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        m_proxy.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        m_proxy.onStop();
    }


    @Override
    public void onBackPressed() {

        LogUtil.log("返回");
        AlertDialog alertDialog = new AlertDialog.Builder(TestActivity.this)
                .setTitle("提示")
                // 对话框消息
                .setMessage("是否要退出游戏？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();


    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
            m_proxy.onDestroy();

    }


}
