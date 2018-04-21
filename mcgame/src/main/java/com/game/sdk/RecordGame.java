package com.game.sdk;

import android.app.Activity;
import com.alibaba.fastjson.JSON;
import com.game.sdk.bean.GameUser;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.KnLog;


/**
 *  sdk数据上报
 */

public class RecordGame {

    private static RecordGame inst=null;

    public static RecordGame getInstance(){
        if (inst==null){
            inst = new RecordGame();
        }
        return inst;
    }

    public void roleInfo(GameUser gameUser){

        //TODO  sdk上报游戏数据接口
        HttpService.enterGame(gameUser, new ISuccess() {
            @Override
            public void onSuccess(String response) {

                final int code = JSON.parseObject(response).getIntValue("code");

                switch (code) {
                    case ResultCode.SUCCESS: //成功

                        if (GameSDK.getInstance().getmReportListener() != null) {
                            GameSDK.getInstance().getmReportListener().onSuccess(response);

                            KnLog.log("上报数据成功" + JSON.parseObject(response));
                        }

                        break;

                    default:

                        KnLog.log("上报数据失败"+JSON.parseObject(response));
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
