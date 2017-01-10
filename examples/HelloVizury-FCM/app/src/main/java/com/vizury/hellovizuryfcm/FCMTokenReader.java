package com.vizury.hellovizuryfcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vizury.mobile.VizuryHelper;

/**
 * Created by anurag on 1/4/17.
 *
 * FCMTokenReader IntentService is used to get the FCM token
 * and store in a shared preference
 */

public class FCMTokenReader extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FCMTokenReader() {
        super("FCMTokenReader");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        // pass the refreshed token to vizury
        if(null != fcmToken)
            VizuryHelper.getInstance(getApplicationContext()).setGCMToken(fcmToken);
    }
}
