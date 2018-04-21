package com.game.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 *  读写时间
 */

public class TodayTimeUtils {





    //获得最后一次保存的日期
    public static Object LastTime(Activity activity ){

      Object  lastTime =  SpUtil.get(activity,"LoginTime","2018-03-10");
       return  lastTime;
    }

    //获得最后一次账号名
    public static Object   LastName(Activity activity,String name ){
        Object lastName = SpUtil.get(activity,name,"mc");

        return lastName;

    }



    //获取当前时间
    public static String TodayTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return  df.format(new Date());// 获取当前的日期
    }

    //保存勾选后的日期
    public static void saveExitTime(Activity activity) {
        SpUtil.put(activity,"LoginTime",TodayTime());
        KnLog.log("保存日期");
    }

    //保存勾选后的账号
    public static void saveExitName(Activity activity,String fistname, String lastName) {


        SpUtil.put(activity,fistname,lastName);
        KnLog.log("保存账号");
    }

}
