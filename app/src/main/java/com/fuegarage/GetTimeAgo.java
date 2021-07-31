package com.fuegarage;

import android.app.Application;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;

public class GetTimeAgo extends Application {
    private static final int MINUTE_SEC = 60 ;
    private static final int HOUR_SEC = 60 *MINUTE_SEC ;
    private static final int DAY_SEC= 24* HOUR_SEC;



    public static String getTimeLater(long time) {
        time /=1000;
        StringBuilder s = new StringBuilder();
        s.append("");

        while (time > 0 ){

            Log.i("time", "getTimeLater: "+time);
            if(time > DAY_SEC){
                s.append(time/DAY_SEC + "days, ");
                time = time % DAY_SEC;
                Log.i("time", "DAY_SEC: "+time);

            }
            if(time > HOUR_SEC){
                s.append(time/ HOUR_SEC + "hours, ");
                time = time % HOUR_SEC;
                Log.i("time", "HOUR_SEC: "+time);

            }
            if(time > MINUTE_SEC ){
                s.append(time/MINUTE_SEC + "minutes, ");
                time = time % MINUTE_SEC;
                Log.i("time", "MINUTE_SEC: "+time);

            }if(time <= MINUTE_SEC ) {
                s.append(time + "seconds, ");
                time=0;
                Log.i("time", "last: "+time);

            }
        }
        String finalString = s.toString();

        return finalString;
    }



}
