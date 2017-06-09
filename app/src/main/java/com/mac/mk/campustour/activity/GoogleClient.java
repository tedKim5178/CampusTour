package com.mac.mk.campustour.activity;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by mk on 2017. 6. 9..
 */

public class GoogleClient {

    static GoogleApiClient googleApiClient = null;

    public static GoogleApiClient getInstance(MainActivity mainActivity, GoogleSignInOptions gso){
        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(mainActivity)
                    .enableAutoManage(mainActivity, mainActivity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return googleApiClient;
    }

    public static GoogleApiClient getInstance(){
        return googleApiClient;
    }
}
