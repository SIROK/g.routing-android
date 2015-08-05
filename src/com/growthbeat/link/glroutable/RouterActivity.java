

package com.growthbeat.link.glroutable;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.growthbeat.glroutable.Router;
import com.growthbeat.glroutable.RouterContext;

import java.util.Map;

public class RouterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the global context
        Router router = Router.getSharedRouter();
        router.initialize(getApplicationContext(), "custom://router");
        // Symbol-esque params are passed as intent extras to the activities
        router.map("users/:id", TestActivity.class);
        router.map("test/:id", new Router.RouterCallback() {
                    @Override
                    public void run(RouterContext context) {
                        Map<String, String> params = context.getParams();
                    }
                });


        Uri uri = getIntent().getData();
        if(uri != null){
            try {
                router.open(uri.toString());
            }catch (Exception e){
                this.finish();
            }

        }


        //router.open("custom://router/test/id?key1=value");

    }
}
