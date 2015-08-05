package com.growthbeat.glroutable;

import android.app.Activity;

import java.util.Map;

/**
 * Created by tabatakatsutoshi on 2015/08/04.
 */
public class RouterOptions {
    Class<? extends Activity> klass;
    Router.RouterCallback callback;
    Map<String, String> defaultParams;

    public RouterOptions() {
    }

    public RouterOptions(Class<? extends Activity> klass) {

    }

    public RouterOptions(Map<String, String> defaultParams) {

    }

    public void setTargetActivity(Class<? extends Activity> klass) {
        this.klass = klass;
    }

    public Class<? extends Activity> getTargetActivity() {
        return klass;
    }

    public Router.RouterCallback getCallback() {
        return callback;
    }

    public void setCallback(Router.RouterCallback callback) {
        this.callback = callback;
    }

    public void setDefaultParams(Map<String, String> defaultParams) {
        this.defaultParams = defaultParams;
    }

    public Map<String, String> getDefaultParams() {
        return defaultParams;
    }

}
