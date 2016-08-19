package com.vizury.hellovizuryfcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vizury.mobile.Push.PushHandler;
import com.vizury.mobile.Utils;

import java.util.Map;

/**
 * Created by anurag on 5/27/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message){
        Map data = message.getData();
        if (Utils.getInstance(getApplicationContext()).isPushFromVizury(data))
            PushHandler.getInstance(getApplicationContext()).handleNotificationReceived(data);
        else {
            // your own logic goes here
        }
    }
}
