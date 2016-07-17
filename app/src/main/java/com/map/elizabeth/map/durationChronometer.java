package com.map.elizabeth.map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;
import android.os.SystemClock;
//
/**
 * Created by Elizabeth Jo on 6/23/2016.
 */
public class durationChronometer extends Chronometer {

    private long timeWhenStopped = 0;

    public durationChronometer(Context context) {
        super(context);
    }

    public durationChronometer (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public durationChronometer (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    public void stop() {
        super.stop();
        timeWhenStopped = getBase() - SystemClock.elapsedRealtime();
    }
//
//    public void reset() {
//        stop();
//        setBase(SystemClock.elapsedRealtime());
//        timeWhenStopped = 0;
//    }
//
//    public long getCurrentTime() {
//        return timeWhenStopped;
//    }
//
//    public void setCurrentTime(long time) {
//        timeWhenStopped = time;
//        setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
//    }
}




