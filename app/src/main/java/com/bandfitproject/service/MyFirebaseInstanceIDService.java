package com.bandfitproject.service;

import android.util.Log;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private final static String TAG = "FCM_ID";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println(TAG + "FirebaseInstanceId Refreshed token: " + refreshedToken);
       //Log.d(TAG, "FirebaseInstanceId Refreshed token: " + refreshedToken);
    }
}
