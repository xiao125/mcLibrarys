package com.game.sdk.util.timer;

import java.util.TimerTask;

/**
 * TimerTask
 */

public class BaseTimerTask extends TimerTask {

    private ITimerListener mITimerListener = null;

    public BaseTimerTask(ITimerListener mITimerListener) {
        this.mITimerListener = mITimerListener;
    }

    @Override
    public void run() {

        if (mITimerListener !=null){
            mITimerListener.onTimer();
        }

    }
}
