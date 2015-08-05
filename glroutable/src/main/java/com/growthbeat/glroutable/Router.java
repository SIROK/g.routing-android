package com.growthbeat.glroutable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tabatakatsutoshi on 2015/08/04.
 */
public class Router {
    private static final String TAG = "Router";
    private static final Router router = new Router();
    private Context context;
    private Map<String, RouterOptions> routes = new HashMap<String, RouterOptions>();
    private String rootUrl = null;
    private final Map<String, RouterParams> cachedRoutes = new HashMap<String, RouterParams>();

    private static class RouterParams {
        public RouterOptions routerOptions;
        public Map<String, String> openParams;
    }

    public interface RouterCallback {
        void run(RouterContext context);
    }


    public static Router getSharedRouter() {
        return router;
    }

    public void initialize(Context context,String rootUrl) {
        this.context = context;
        this.rootUrl = rootUrl;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    public void map(String format, RouterCallback callback) {
        RouterOptions options = new RouterOptions();
        options.setCallback(callback);
        this.map(format, null, options);
    }

    public void map(String format, Class<? extends Activity> klass) {
        this.map(format, klass, null);
    }

    public void map(String format, Class<? extends Activity> klass, RouterOptions options) {
        if (options == null) {
            options = new RouterOptions();
        }
        options.setTargetActivity(klass);
        this.routes.put(format, options);
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void openExternalUrl(String url) {
        this.openExternalUrl(url, this.context);
    }

    public void openExternalUrl(String url, Context context) {
        this.openExternalUrl(url, null, context);
    }

    public void openExternalUrl(String url, Bundle extras, Context context) {
        if (context == null) {
            throw new ContextNotFoundException("context not found" + this.toString());
        }

        RouterParams params = this.paramsForUrl(url);
        RouterOptions options = params.routerOptions;
        if (options.getCallback() != null) {
            RouterContext routeContext = new RouterContext(params.openParams, extras, context);

            options.getCallback().run(routeContext);
            return;
        }

        Intent intent = this.intentFor(context, params);
        if (intent == null) {
            // Means the options weren't opening a new activity
            return;
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);


    }

    public void open(String url)throws RouteNotFoundException {
        this.open(url, this.context);
    }

    public void open(String url, Context context) throws RouteNotFoundException{
        this.open(url, null, context);
    }

    public void open(String url, Bundle extras, Context context) throws RouteNotFoundException {
        if (context == null) {
            throw new ContextNotFoundException(
                    "You need to supply a context for Router "
                            + this.toString());
        }
        RouterParams params = this.paramsForUrl(url);
        RouterOptions options = params.routerOptions;
        if (options.getCallback() != null) {
            RouterContext routeContext = new RouterContext(params.openParams, extras, context);

            options.getCallback().run(routeContext);
            return;
        }

        Intent intent = this.intentFor(context, params);
        if (intent == null) {
            // Means the options weren't opening a new activity
            return;
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }


    private void addFlagsToIntent(Intent intent, Context context) {
        if (context == this.context) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    public Intent intentFor(String url) {
        RouterParams params = this.paramsForUrl(url);

        return intentFor(params);
    }

    private Intent intentFor(RouterParams params) {
        RouterOptions options = params.routerOptions;
        Intent intent = new Intent();
        if (options.getDefaultParams() != null) {
            for (Map.Entry<String, String> entry : options.getDefaultParams().entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, String> entry : params.openParams.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        return intent;
    }

    public Intent intentFor(Context context, String url) {
        RouterParams params = this.paramsForUrl(url);

        return intentFor(context, params);
    }

    private Intent intentFor(Context context, RouterParams params) {
        RouterOptions options = params.routerOptions;
        if (options.getCallback() != null) {
            return null;
        }

        Intent intent = intentFor(params);
        intent.setClass(context, options.getTargetActivity());
        this.addFlagsToIntent(intent, context);
        return intent;
    }

    public boolean isCallbackUrl(String url) {
        RouterParams params = this.paramsForUrl(url);
        RouterOptions options = params.routerOptions;
        return options.getCallback() != null;
    }



    private RouterParams paramsForUrl(String url) throws RouteNotFoundException{
        try {
            URI uri = new URI(url);
            String givenScheme = uri.getScheme();
            if (givenScheme != null){
                String removeString = "";
                if(rootUrl != null){
                    removeString = rootUrl;
                }
                url = url.replace(removeString,"");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String cleanedUrl = cleanUrl(url);



        URI parsedUri = URI.create("http://tempuri.org/" + cleanedUrl);

        String urlPath = parsedUri.getPath().substring(1);

        if (this.cachedRoutes.get(cleanedUrl) != null) {
            return this.cachedRoutes.get(cleanedUrl);
        }

        String[] givenParts = urlPath.split("/");

        RouterParams routerParams = null;
        for (Map.Entry<String, RouterOptions> entry : this.routes.entrySet()) {
            String routerUrl = cleanUrl(entry.getKey());
            RouterOptions routerOptions = entry.getValue();
            String[] routerParts = routerUrl.split("/");

            if (routerParts.length != givenParts.length) {
                continue;
            }

            Map<String, String> givenParams = urlToParamsMap(givenParts, routerParts);
            if (givenParams == null) {
                continue;
            }

            routerParams = new RouterParams();
            routerParams.openParams = givenParams;
            routerParams.routerOptions = routerOptions;
            break;
        }

        if (routerParams == null) {
            throw new RouteNotFoundException("No route found for url " + url);
        }

        List<NameValuePair> query = URLEncodedUtils.parse(parsedUri, "utf-8");

        for (NameValuePair pair : query) {
            routerParams.openParams.put(pair.getName(), pair.getValue());
        }

        this.cachedRoutes.put(cleanedUrl, routerParams);
        return routerParams;
    }

    private Map<String, String> urlToParamsMap(String[] givenUrlSegments, String[] routerUrlSegments) {
        Map<String, String> formatParams = new HashMap<String, String>();
        for (int index = 0; index < routerUrlSegments.length; index++) {
            String routerPart = routerUrlSegments[index];
            String givenPart = givenUrlSegments[index];

            if (routerPart.charAt(0) == ':') {
                String key = routerPart.substring(1, routerPart.length());
                formatParams.put(key, givenPart);
                continue;
            }

            if (!routerPart.equals(givenPart)) {
                return null;
            }
        }

        return formatParams;
    }

    private String cleanUrl(String url) {
        if (url.startsWith("/")) {
            return url.substring(1, url.length());
        }
        return url;
    }


}
