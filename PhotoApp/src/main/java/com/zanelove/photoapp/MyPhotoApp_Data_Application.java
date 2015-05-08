package com.zanelove.photoapp;

import android.app.Application;

/**
 * Created by zanelove on 15-5-5.
 */
public class MyPhotoApp_Data_Application extends Application {
    //TODO
    private String url_host = "http://xxx/xxx",sessionid="xxx";

    public String getUrl_host() {
        return url_host;
    }

    public String getSessionid() {
        return sessionid;
    }
}
