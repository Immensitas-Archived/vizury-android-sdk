package com.vizury.hellovizuryfcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vizury.mobile.AttributeBuilder;
import com.vizury.mobile.VizuryHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the VizuryHelper. Ensure that you have added
        // services, receivers, permissions and the Meta Tags with correct values.
        VizuryHelper.getInstance(getApplicationContext()).init();

        // read the fcm token from shared preference to be passed to vizury,
        // if not present then start an intent service to get the token.
        SharedPreferences sharedPreferences	=	getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,0);
        String fcmToken = sharedPreferences.getString(Constants.PREFS_FCM_TOKEN, null);
        if(fcmToken == null)
            startService(new Intent(this,FCMTokenReader.class));
        else
            VizuryHelper.getInstance(getApplicationContext()).setGCMToken(fcmToken);
    }

    /**
     * example method to show how to create an attributeBuilder object and
     * call logEvent with the event name that you want to pass and the created
     * attributeBuilder associated with the event
     */
    private void exampleAppEvent() {
        AttributeBuilder builder = new AttributeBuilder.Builder()
                .addAttribute("pid", "AFGEMSBBLL")
                .addAttribute("quantity", "1")
                .addAttribute("price", "876")
                .addAttribute("category","clothing")
                .build();

        VizuryHelper.getInstance(getApplicationContext()).logEvent("product page", builder);
    }
}
