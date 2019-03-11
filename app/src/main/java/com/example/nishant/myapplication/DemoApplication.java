package com.example.nishant.myapplication;

import android.app.Application;

import com.mmi.services.account.MapmyIndiaAccountManager;


/**
 * Created by CE on 29/09/15.
 */
public class DemoApplication extends Application {


    private String getAtlasGrantType() {
        return "client_credentials";
    }

    @Override
    public void onCreate() {
        super.onCreate();


        MapmyIndiaAccountManager.getInstance().setRestAPIKey("5ebg9a4971jmd1w5rl72me53cd4xawoq");
        MapmyIndiaAccountManager.getInstance().setMapSDKKey("mbj7lmg4mbpbbxs2jyfhovkz9zwuvwku");
        MapmyIndiaAccountManager.getInstance().setAtlasClientId("bGq2Enj8frSsQOAII6jRD0cQCerbtFeR");
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret("M8TOsT5LpBM8U5it5eBFIAdf8p4-fN02PB17kQq9SV8=");
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType(getAtlasGrantType());


    }
}
