package com.game.sdk.floatmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.game.sdk.activity.AutoLoginActivity;
import com.game.sdk.configurator.ConfigKeys;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LogUtil;
import com.game.sdk.util.TodayTimeUtils;
import com.game.sdk.util.Util;
import com.game.sdkproxy.R;

import java.util.ArrayList;

/**
 *
 */

public final class SusViewMager {

    FloatLogoMenu mFloatMenu;

    ArrayList<FloatItem> itemList = new ArrayList<>();
    private Activity mActivity;

    String  CANCELLATION = "注销";
    String[] MENU_ITEMS = {CANCELLATION};
    private int[] menuIcons = new int[]{R.drawable.mc_game_menu_msg};

    private boolean islogout =false;

    private String username ;

  //回调接口
    public interface  OnLogoutListener{
        void onExitFinish();
    }
    public OnLogoutListener mListener;

    public void setOnLogoutListener(OnLogoutListener listener){
        mListener = listener;
    }


    /*public static SusViewMager getInstance(){

        if (mInstance ==null){

            synchronized (SusViewMager.class){
                if (mInstance == null){
                    mInstance = new SusViewMager();
                }
            }
        }
        return mInstance;
    }*/

    //静态内部类单例模式
    public static class Hoderl{

        public static final SusViewMager INSTANCE = new SusViewMager();
    }

   public static SusViewMager getInstance(){
        return Hoderl.INSTANCE;

    }



    public void showWithCallback(final Activity activity){

        mActivity = activity;



        if (mFloatMenu ==null){

            KnLog.log("创建悬浮窗");

            for (int i =0 ;i<menuIcons.length;i++){

                //设置悬浮窗里面icon背景色，字体颜色等
                itemList.add(new FloatItem(MENU_ITEMS[i],Color.parseColor("#FFFFFFFF"), Color.parseColor("#80000000"),
                        BitmapFactory.decodeResource(mActivity.getResources(),menuIcons[i]),String.valueOf(i +1)));
            }

            mFloatMenu = new FloatLogoMenu.Builder()
                    .withActivity(mActivity)
                    .logo(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.mc_game_logo))
                    .drawCicleMenuBg(true) //设置悬浮窗里面的注销按钮，false:正方形  true ：圆形
                    .backMenuColor(Color.parseColor("#80000000")) //悬浮窗logo背景颜色
                   .setBgDrawable(mActivity.getResources().getDrawable(R.drawable.mc_game_float_menu_bg)) //展开背景色
                    //
                    .setFloatItems(itemList)
                    .defaultLocation(FloatLogoMenu.LEFT) //悬浮球 坐落 左 右 标记
                    .drawRedPointNum(false) //绘制提醒的红点数字
                    .showWithListener(new FloatMenuView.OnMenuClickListener() { //悬浮窗里面的item点击事件
                        @Override
                        public void onItemClick(final int position, String title) {
                            if (position ==0){ //注销账号
                                //判断是否是首次安装app，注销提示先登录
                                final String usernames[] = DBHelper.getInstance().findAllUserName();
                                if( usernames != null && usernames.length >0 ){
                                    username = usernames[0];
                                    KnLog.log("========首次调用注销："+username);
                                }else {
                                    username =null;
                                    KnLog.log("========首次调用注销，没有账号："+username);
                                }
                                if (username== null){
                                    Util.ShowTips(activity,"请登录！");
                                    return;
                                }

                                LayoutInflater inflater = LayoutInflater.from(activity);
                                View v = inflater.inflate(R.layout.mc_float_logout_dialog, null); //绑定手机
                                LinearLayout layout = (LinearLayout) v.findViewById(R.id.mc_float_logout_dialog);
                                final AlertDialog dia = new AlertDialog.Builder(activity).create();
                                Button close = (Button) v.findViewById(R.id.mc_logout_account); //取消
                                Button bin = (Button) v.findViewById(R.id.mc_logout_continue);//确定
                                // bind.setText("绑定手机");
                                dia.show();
                                dia.setContentView(v);
                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dia.dismiss();
                                    }
                                });

                                bin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (null !=mListener){
                                            mListener.onExitFinish();
                                            KnLog.log("sdk注销账号了1");
                                        }
                                        dia.dismiss();

                                        //延迟1.5S游戏跳转到登录界面后弹出登录框
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //防止第一没有账号就点击注销
                                                    Intent intent1 = new Intent(activity, AutoLoginActivity.class);
                                                    // intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent1.putExtra("logout","logout");
                                                    activity.startActivity(intent1);
                                                    KnLog.log("sdk注销账号了2");
                                            }
                                        },1000);
                                        //注销
                                        HttpService.doCancel("2", new ISuccess() {
                                            @Override
                                            public void onSuccess(String response) {
                                                LogUtil.log("doCancel。。。。"+response);
                                            }
                                        }, new IError() {
                                            @Override
                                            public void onError(int code, String msg) {
                                                LogUtil.log("doCancel is Errot:"+ " code:["+ code+"],msg["+ msg+"]");
                                            }
                                        });
                                    }
                                });

                            }


                        }

                        @Override
                        public void dismiss() {
                            KnLog.log("收缩悬浮窗");
                        }
                    });

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshDot();
                }
            },5000);
        }else {
            return;
        }
    }

    public void refreshDot() {
        for (FloatItem menuItem : itemList) {
            if (TextUtils.equals(menuItem.getTitle(), "注销")) {
                menuItem.dotNum = String.valueOf(8);
            }
        }
        mFloatMenu.setFloatItemList(itemList);
    }




    //关闭菜单
    public void hideFloat() {
        if (mFloatMenu != null) {
            mFloatMenu.hide();
        }
    }

    public void destroyFloat() {
        if (mFloatMenu != null) {
            mFloatMenu.destoryFloat();
        }
        mFloatMenu = null;
        mActivity = null;
    }



    private void isname(Activity activity){

        final String usernames[] = DBHelper.getInstance().findAllUserName();
        if( usernames != null && usernames.length >0 ){
            username = usernames[0];

            KnLog.log("========首次调用注销："+username);
        }else {
            username =null;
            KnLog.log("========首次调用注销，没有账号："+username);
        }

        if (username== null){
            Util.ShowTips(activity,"请登录！");
            return;
        }

    }



}
