package com.vizury.hellovizuryfcm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
//        exampleAppEvent();
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
