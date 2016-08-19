package com.vizury.hellovizury;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
