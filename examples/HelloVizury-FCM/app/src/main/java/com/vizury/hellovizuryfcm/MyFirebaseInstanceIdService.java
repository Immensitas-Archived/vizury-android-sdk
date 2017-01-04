package com.vizury.hellovizuryfcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vizury.mobile.VizuryHelper;

/**
 * Created by anurag on 5/27/16.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // save the token in shared preference so that
        // the fcm token can be retrieved if required
        SharedPreferences sharedPreferences	=	getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
        SharedPreferences.Editor editor	=	sharedPreferences.edit();
        editor.putString(Constants.PREFS_FCM_TOKEN, refreshedToken);
        editor.apply();

        // pass the refreshed token to vizury
        VizuryHelper.getInstance(getApplicationContext()).setGCMToken(refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
    }

}
