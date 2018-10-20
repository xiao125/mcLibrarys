package com.game.sdk;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.KnLog;


/**
 * 上报激活设备接口
 */

public class RecordActivate {

    private static RecordActivate inst=null;
    public static RecordActivate getInstance(){
        if (inst==null){
            inst = new RecordActivate();
        }
        return inst;
    }

    public void initrecordActivate(Context context){

        //设备激活
        HttpService.recordActivate(context, new ISuccess() {
            @Override
            public void onSuccess(String response) {
                final int code = JSON.parseObject(response).getIntValue("code");
                switch (code) {
                    case ResultCode.SUCCESS: //成功

                        KnLog.log("sdk上报设备激活成功"+response);

                        break;

                    default:

                        KnLog.log("sdk上报设备激活失败"+response);
                        break;
                }

            }
        }, new IError() {
            @Override
            public void onError(int code, String msg) {

            }
        });

    }


}
