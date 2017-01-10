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
        // start an intent service to get the fcm token
        startService(new Intent(this,FCMTokenReader.class));
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
