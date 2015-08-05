package com.growthbeat.link.glroutable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by tabatakatsutoshi on 2015/08/04.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intentExtras = getIntent().getExtras();
        // Note this extra, and how it corresponds to the ":id" above
        String userId = intentExtras.getString("id");
        String key1 =  intentExtras.getString("key1");
        Log.d("userid", userId);



    }
}
