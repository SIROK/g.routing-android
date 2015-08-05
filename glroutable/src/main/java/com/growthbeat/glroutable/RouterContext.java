package com.growthbeat.glroutable;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Created by tabatakatsutoshi on 2015/08/04.
 */
public class RouterContext {
    Map<String, String> params;
    Bundle extras;
    Context context;

    public RouterContext(Map<String, String> params, Bundle extras, Context context) {
        this.params = params;
        this.extras = extras;
        this.context = context;

    }

    public Map<String,String> getParams() {
        return params;
    }

    public Bundle getExtras() {
        return extras;
    }

    public Context getContext() {
        return  context;
    }



}